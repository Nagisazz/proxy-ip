package com.nagisazz.proxyip.common;

import com.nagisazz.proxyip.service.ProxyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartRunner implements CommandLineRunner {

    @Autowired
    private ProxyRequest proxyRequest;

    @Override
    public void run(String... args) throws Exception {
        proxyRequest.visit(Constant.url, Constant.proxyUrl);
    }
}
