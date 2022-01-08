package com.nagisazz.proxyip.dao.base;

import com.nagisazz.proxyip.entity.ProxyIpLog;
import java.util.List;

public interface ProxyIpLogMapper {
    int deleteByPrimaryKey(String ip);

    int insert(ProxyIpLog proxyiplog);

    int insertSelective(ProxyIpLog proxyiplog);

    ProxyIpLog selectByPrimaryKey(String ip);

    int updateByPrimaryKeySelective(ProxyIpLog proxyiplog);

    int updateByPrimaryKey(ProxyIpLog proxyiplog);

    ProxyIpLog selectOne(ProxyIpLog proxyiplog);

    List<ProxyIpLog> selectList(ProxyIpLog proxyiplog);

    int deleteSelective(ProxyIpLog proxyiplog);

    int batchUpdate(List<ProxyIpLog> proxyiplogList);

    int batchUpdateSelective(List<ProxyIpLog> proxyiplogList);

    int batchInsert(List<ProxyIpLog> proxyiplogList);

    int batchDelete(List<String> proxyiplogList);

    List<ProxyIpLog> batchSelect(List<String> proxyiplogList);
}