package com.nagisazz.proxyip.service;

import com.nagisazz.proxyip.common.Constant;
import com.nagisazz.proxyip.dao.ProxyIpLogExtendMapper;
import com.nagisazz.proxyip.entity.ProxyIpLog;
import com.nagisazz.proxyip.util.TestValidTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RemoveService {

    private static final Integer BATCH = 400;

    @Autowired
    private ProxyIpLogExtendMapper proxyIpLogExtendMapper;

    @Autowired
    private TestValidTask testValidTask;

    /**
     * 计算切分次数
     */
    private static Integer countStep(Integer size) {
        return (size + BATCH - 1) / BATCH;
    }

    public void testValid(List<ProxyIpLog> proxyIpLogs) {
        CountDownLatch countDownLatch = new CountDownLatch(proxyIpLogs.size());
        for (ProxyIpLog proxyIP : proxyIpLogs) {
            testValidTask.start(proxyIP, Constant.url, countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Stream.iterate(0, n -> n + 1).limit(countStep(proxyIpLogs.size())).parallel().forEach(i -> {
            List<ProxyIpLog> res = proxyIpLogs.stream().skip(i * BATCH).limit(BATCH).collect(Collectors.toList());
            proxyIpLogExtendMapper.batchUpdate(res);
        });

    }
}
