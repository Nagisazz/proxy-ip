package com.nagisazz.proxyip.service;

import com.nagisazz.proxyip.dao.ProxyIpLogExtendMapper;
import com.nagisazz.proxyip.entity.ProxyIpLog;
import com.nagisazz.proxyip.vo.ProxyIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecordValidProxy {

    @Autowired
    private ProxyIpLogExtendMapper proxyIpLogExtendMapper;

    private Set<String> proxysSet = new HashSet<String>();

    private Set<String> proxysClear = new HashSet<String>();

    //设定Set的size达到一定值时开始写入文件
    private int length = 5;
    //状态标志
    private int clear = 0;

    public void write(Set<String> proxys) {
        List<ProxyIpLog> proxyIpLogs = new ArrayList<>();
        for (String ip:proxys){
            proxyIpLogs.add(ProxyIpLog.builder().ip(ip).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build());
        }

        proxyIpLogExtendMapper.insertOrUpdateBatch(proxyIpLogs);
    }

    public void record(ProxyIP ip) {
        String proxyIP = ip.getAddress() + ":" + ip.getPort();
        log.info("proxysSet :" + proxysSet.size());
        if (proxysSet.size() >= length) {
            proxysClear.addAll(proxysSet);
            proxysSet.clear();
            proxysSet.add(proxyIP);
            if (clear == 0) {
                clear = 1;
                new Thread(() -> {
                    log.info("*********************开始写IP*********************");
                    write(proxysClear);
                    proxysClear.clear();
                    clear = 0;
                    log.info("*********************结束写IP*********************");
                }).start();
            }
        } else {
            proxysSet.add(proxyIP);
        }
    }
}
