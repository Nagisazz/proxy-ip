import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OperateProxyIPTask {

    private ExecutorService pool = Executors.newFixedThreadPool(3);

    public void start(File file) {
        pool.execute(new OperateThread(file));
    }

    public void doNoTask(File file){
        Set<String> ipList = testValid(removeDup(file));
        String fileName = file.getAbsolutePath();
        if (file.exists()) {
            file.delete();
            RecordValidProxy recordValidProxy = new RecordValidProxy();
            recordValidProxy.write(ipList, fileName);
        }
    }

    public void stop() {
        pool.shutdown();
    }

    private Set<String> removeDup(File file) {
        BufferedReader reader = null;
        Set<String> ipList = new HashSet<String>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String ip = null;
            while ((ip = reader.readLine()) != null) {
                if (!ip.trim().equals("")) {
                    ipList.add(ip);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipList;
    }

    private Set<String> testValid(Set<String> orignList) {
        String url = "http://fanyi.baidu.com/#en/zh/operate";
        List<ProxyIP> ipList = convert(orignList);
        Set<String> validIP = new HashSet<String>();
        CountDownLatch countDownLatch = new CountDownLatch(ipList.size());
        TestValidTask testValidTask = new TestValidTask();
        for (ProxyIP proxyIP : ipList) {
            testValidTask.start(proxyIP, url, "", validIP, countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
        }
        return validIP;
    }

    private List<ProxyIP> convert(Set<String> sets) {
        List<ProxyIP> ipList = new ArrayList<ProxyIP>();
        for (String ip : sets) {
            String ips[] = ip.split(":");
            ProxyIP proxyIP = new ProxyIP();
            proxyIP.setAddress(ips[0]);
            proxyIP.setPort(ips[1]);
            ipList.add(proxyIP);
        }
        return ipList;
    }

    class OperateThread implements Runnable {
        private File file;

        public OperateThread(File file) {
            this.file = file;
        }

        public void run() {
            Set<String> ipList = testValid(removeDup(file));
            String fileName = file.getAbsolutePath();
            if (file.exists()) {
                file.delete();
                RecordValidProxy recordValidProxy = new RecordValidProxy();
                recordValidProxy.write(ipList, fileName);
            }
        }
    }
}
