package com.nagisazz.proxyip.controller;

import com.nagisazz.proxyip.service.ProxyIpService;
import com.nagisazz.proxyip.service.RecordValidProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ip")
public class ProxyIpController {

    @Autowired
    private ProxyIpService proxyIpService;

    @Autowired
    private RecordValidProxy recordValidProxy;

    @GetMapping("/list/{size}")
    public void list(@PathVariable Integer size) {
        proxyIpService.selectSizeList(size);
    }

    @GetMapping("/remove/{ip}/{port}")
    public void remove(@PathVariable String ip, @PathVariable String port) {
        proxyIpService.remove(ip + ":" + port);
    }

//    @GetMapping("/test")
//    public void test(){
//        Set<String> strings = new HashSet<>(Collections.singletonList("1"));
//        recordValidProxy.write(strings);
//    }
}
