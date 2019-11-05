import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClearTimeTask {
    private ScheduledExecutorService scheduExec;
    private String filePath;

    public ClearTimeTask(String filePath) {
        this.filePath = filePath;
        this.scheduExec = Executors.newScheduledThreadPool(3);
    }

    public void clear() {

        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = getTimeMillis("00:01:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;

        scheduExec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (isFirstMonth()) {
                    mergeFiles(filePath);
                }else {
                    operateProxyIP(filePath);
                }
            }
        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

//    public static void main(String[] args) throws ParseException {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2019, 1, 2);
//        calendar.add(Calendar.MONTH, -1);
//        System.out.println(calendar.get(Calendar.MONTH) + 1);
//        System.out.println(calendar.getTime());
//        System.out.println(calendar.get(Calendar.YEAR));
//
//    }

    private long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Boolean isFirstMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    private void mergeFiles(String filePath) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String prefix;
        if (month < 10) {
            prefix = year + "0" + month;
        } else {
            prefix = year + "" + month;
        }

        ProxyRequest.logger.info("********************开始合并 " + year + " 年 " + month + " 月数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        List<File> files = getPrefixFiles(filePath, prefix, new ArrayList<File>());
        MergeFiles.handle(files, prefix);
        ProxyRequest.logger.info("********************结束合并 " + year + " 年 " + month + " 月数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private void operateProxyIP(String filePath) {
        ProxyRequest.logger.info("********************开始清除无用数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        List<File> files = getFiles(filePath, new ArrayList<File>());
        OperateProxyIPTask operateProxyIPTask = new OperateProxyIPTask();
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        for (File file : files) {
            operateProxyIPTask.start(file, countDownLatch);
        }
        try {
            countDownLatch.await();
            ProxyRequest.logger.info("********************清除无用数据完成********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (InterruptedException e) {
        }
    }

    private List<File> getFiles(String realpath, List<File> files) {
        File fileDir = new File(realpath);
        if (fileDir.isDirectory()) {
            File[] subFiles = fileDir.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    if (file.lastModified() < getTimeMillis("00:00:00")) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    private List<File> getPrefixFiles(String realpath, String prefix, List<File> files) {
        File fileDir = new File(realpath);
        if (fileDir.isDirectory()) {
            File[] subFiles = fileDir.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    getPrefixFiles(file.getAbsolutePath(), prefix, files);
                } else {
                    if (file.getName().startsWith(prefix)) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }
}