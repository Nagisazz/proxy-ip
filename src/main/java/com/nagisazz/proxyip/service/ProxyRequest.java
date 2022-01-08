package com.nagisazz.proxyip.service;

import com.nagisazz.proxyip.util.HttpRequestUtil;
import com.nagisazz.proxyip.util.ProxyGetUtil;
import com.nagisazz.proxyip.vo.ProxyIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProxyRequest {

    @Autowired
    private RecordValidProxy recordValidProxy;

//    public static void main(String[] args) {
//        //创建目录
//        File file = new File(Constant.filePath);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//
//        //开启定时任务
//        ClearTimeTask task = new ClearTimeTask(Constant.filePath);
//        task.clear();
//
//        visit(Constant.url, Constant.proxyUrl, Constant.filePath);
//    }

    public void visit(String url, String proxyUrl) {
        List<ProxyIP> ipList = ProxyGetUtil.getProxyIP(proxyUrl);

        //当前访问总次数
        int now = 0;
        //访问成功次数
        int count = 0;

        for (int i = 1; ; i++) {
            log.info("--------第" + i + "批代理IP访问开始--------\n");
            for (ProxyIP proxyIP : ipList) {
                now++;
                log.info("现在是第 " + now + " 次访问");
                log.info("使用的代理为------" + proxyIP.getAddress() + ":" + proxyIP.getPort());
                try {
                    String result = HttpRequestUtil.sendProxyGet(proxyIP.getAddress(), Integer.parseInt(proxyIP.getPort()), url, "");
                    if (!result.equals("false")) {
                        count++;
                        log.info("成功访问次数: " + count);
                        log.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                        recordValidProxy.record(proxyIP);
                    } else {
                        log.info("代理GET请求发送异常！");
                        log.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                    }
                } catch (Exception e) {
                }
                log.info("--------本次访问结束--------\n");
            }
            log.info("--------第" + i + "批代理IP访问结束--------\n");

            //30分钟获取一次
            try {
                Thread.sleep(1000 * 60 * 30);
            } catch (InterruptedException e) {
            }

            ipList = ProxyGetUtil.getProxyIP(proxyUrl);
        }
    }

}
