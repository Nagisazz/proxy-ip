import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OperateProxyIPTask {

    private ExecutorService pool  = Executors.newFixedThreadPool(3);

    public void start(File file, CountDownLatch countDownLatch){
        pool.execute(new OperateThread(file,countDownLatch));
    }

    public void stop(){
        pool.shutdown();
    }

    class OperateThread implements Runnable{
        private File file;
        private CountDownLatch countDownLatch;

        public OperateThread(File file, CountDownLatch countDownLatch) {
            this.file = file;
            this.countDownLatch = countDownLatch;
        }

        public void run() {
            Set<String> ipList = testValid(removeDup(file));
            String fileName = file.getAbsolutePath();
            if (file.exists()){
                file.delete();
                RecordValidProxy.write(ipList,fileName);
            }
            countDownLatch.countDown();
        }
    }

    private Set<String> removeDup(File file) {
        BufferedReader reader = null;
        Set<String> ipList = new HashSet<String>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String ip = null;
            while ((ip = reader.readLine())!=null){
                if (!ip.trim().equals("")){
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

    private Set<String> testValid(Set<String> orignList){
        String url = "http://fanyi.baidu.com/#en/zh/operate";
        List<ProxyIP> ipList = convert(orignList);
        Set<String> validIP = new HashSet<String>();
        CountDownLatch countDownLatch = new CountDownLatch(ipList.size());
        TestValidTask testValidTask = new TestValidTask();
        for (ProxyIP proxyIP : ipList) {
            testValidTask.start(proxyIP,url,"",validIP,countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
        }
        return validIP;
    }

    private List<ProxyIP> convert(Set<String> sets){
        List<ProxyIP> ipList = new ArrayList<ProxyIP>();
        for (String ip:sets){
            String ips[] = ip.split(":");
            ProxyIP proxyIP = new ProxyIP();
            proxyIP.setAddress(ips[0]);
            proxyIP.setPort(ips[1]);
            ipList.add(proxyIP);
        }
        return ipList;
    }
}
