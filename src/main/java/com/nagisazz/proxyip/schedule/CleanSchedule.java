package com.nagisazz.proxyip.schedule;

import com.nagisazz.proxyip.dao.ProxyIpLogExtendMapper;
import com.nagisazz.proxyip.entity.ProxyIpLog;
import com.nagisazz.proxyip.service.RemoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CleanSchedule {

    @Autowired
    private ProxyIpLogExtendMapper proxyIpLogExtendMapper;

    @Autowired
    private RemoveService removeService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void clean() {
        //每月1号检查所有，平时检查前一天
        if (isFirstMonth()) {
            mergeFiles();
        } else {
            operateProxyIP();
        }
    }

    private Boolean isFirstMonth() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        log.info("********************今天是" + year + "年" + month + "月" + day + "********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return day == 1;
    }

    private void mergeFiles() {
        log.info("********************开始清除历史数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        List<ProxyIpLog> proxyIpLogs = proxyIpLogExtendMapper.selectList(ProxyIpLog.builder().valid(1).build());
        removeService.testValid(proxyIpLogs);
        log.info("********************结束清除历史数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private void operateProxyIP() {
        //获取前一天日期
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String format = df.format(calendar.getTime());

        log.info("********************开始清除合并前一天（" + format + "）无用数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        List<ProxyIpLog> proxyIpLogs = proxyIpLogExtendMapper.selectLastList();
        removeService.testValid(proxyIpLogs);
        log.info("********************结束清除合并前一天（" + format + "）无用数据完成********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
