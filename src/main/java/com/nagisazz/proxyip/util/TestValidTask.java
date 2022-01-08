package com.nagisazz.proxyip.util;

import com.nagisazz.proxyip.entity.ProxyIpLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class TestValidTask {

    private ExecutorService pool = Executors.newFixedThreadPool(30);

    public void start(ProxyIpLog proxyIP, String url, CountDownLatch countDownLatch) {
        pool.execute(new TestValidThread(proxyIP, url, countDownLatch));
    }

    public void stop() {
        pool.shutdown();
    }

    class TestValidThread implements Runnable {

        private ProxyIpLog proxyIP;
        private String url;
        private CountDownLatch countDownLatch;

        public TestValidThread(ProxyIpLog proxyIP, String url, CountDownLatch countDownLatch) {
            this.proxyIP = proxyIP;
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        public void run() {
            String address = proxyIP.getIp().split(":")[0];
            String port = proxyIP.getIp().split(":")[1];
            String result = HttpRequestUtil.sendProxyGet(address, Integer.parseInt(port), url, "");
            proxyIP.setUpdateTime(LocalDateTime.now());
            if (!result.equals("false")) {
                proxyIP.setNumber(proxyIP.getNumber() + 1);
                log.info("********************" + address + ":" + port + "验证有效********************");
            } else {
                proxyIP.setValid(0);
                log.info("********************" + address + ":" + port + "验证！！！无效！！！********************");
            }
            countDownLatch.countDown();
        }
    }
}
