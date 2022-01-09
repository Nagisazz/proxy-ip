package com.nagisazz.proxyip.dao;

import com.nagisazz.proxyip.dao.base.ProxyIpLogMapper;
import com.nagisazz.proxyip.entity.ProxyIpLog;

import java.util.List;

public interface ProxyIpLogExtendMapper extends ProxyIpLogMapper {

    void insertOrUpdateBatch(List<ProxyIpLog> proxyIpLogs);

    List<ProxyIpLog> selectLastList();

    List<ProxyIpLog> selectSizeList(Integer size);
}