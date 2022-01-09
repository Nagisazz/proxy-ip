package com.nagisazz.proxyip.controller;

import com.nagisazz.proxyip.entity.ProxyIpLog;
import com.nagisazz.proxyip.service.ProxyIpService;
import com.nagisazz.proxyip.service.RecordValidProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ip")
public class ProxyIpController {

    @Autowired
    private ProxyIpService proxyIpService;

    @Autowired
    private RecordValidProxy recordValidProxy;

    @GetMapping("/list/{size}")
    public List<ProxyIpLog> list(@PathVariable Integer size, HttpServletRequest request) {
        log.info("访问list接口，访问ip：{}", getIpAddr(request));
        return proxyIpService.selectSizeList(size);
    }

    @GetMapping("/remove/{ip}/{port}")
    public void remove(@PathVariable String ip, @PathVariable String port, HttpServletRequest request) {
        log.info("访问remove接口，访问ip：{}", getIpAddr(request));
        proxyIpService.remove(ip + ":" + port);
    }

//    @GetMapping("/test")
//    public void test(){
//        Set<String> strings = new HashSet<>(Collections.singletonList("1"));
//        recordValidProxy.write(strings);
//    }

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    public static String getIpAddr(HttpServletRequest request) {
        System.out.println(request);
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
