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
                operateProxyIP(filePath);
            }
        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

//    public static void main(String[] args) {
//
////        File file = new File("F:\\me\\proxy-ip\\proxyip\\ip");
////        System.out.println(file.getPath());
////        System.out.println(file.getAbsolutePath());
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

    private void operateProxyIP(String filePath) {
        System.out.println("********************开始清除无用数据********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        List<File> files = getFiles(filePath, new ArrayList<File>());
        OperateProxyIPTask operateProxyIPTask = new OperateProxyIPTask();
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        for (File file : files) {
            operateProxyIPTask.start(file,countDownLatch);
        }
        try {
            countDownLatch.await();
            System.out.println("********************清除无用数据完成********************   现在时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (InterruptedException e) {
        }
    }

    private List<File> getFiles(String realpath, List<File> files) {
        File fileDir = new File(realpath);
        if (fileDir.isDirectory()) {
            File[] subfiles = fileDir.listFiles();
            for (File file : subfiles) {
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
}