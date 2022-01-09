package com.nagisazz.proxyip.service;

import com.nagisazz.proxyip.dao.ProxyIpLogExtendMapper;
import com.nagisazz.proxyip.entity.ProxyIpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProxyIpService {

    @Autowired
    private ProxyIpLogExtendMapper proxyIpLogExtendMapper;

    public List<ProxyIpLog> selectSizeList(Integer size) {

        return proxyIpLogExtendMapper.selectSizeList(size);
    }

    public void remove(String ip) {
        proxyIpLogExtendMapper.updateByPrimaryKeySelective(ProxyIpLog.builder().ip(ip).valid(0)
                .updateTime(LocalDateTime.now()).build());
    }
}
