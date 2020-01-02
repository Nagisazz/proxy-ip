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

public class MergeFiles {

    private void removePrefix(String name) {
        File fileDir = new File(Constant.filePath);
        File[] subFiles = fileDir.listFiles();
        for (File file : subFiles) {
            if (file.getName().startsWith(name) && file.getName().length() > 6) {
                file.delete();
            }
        }
    }

    private static Set<String> removeDup(List<File> files) {
        Set<String> ipList = new HashSet<String>();
        for (File file : files) {
            BufferedReader reader = null;
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
        }
        return ipList;
    }

    public void handle(List<File> files, String name) {
        Set<String> ipList = testValid(removeDup(files));
        ProxyRequest.logger.info("********************" + name + "验证完成，共有" + ipList.size() + "个ip通过验证********************");
        ProxyRequest.logger.info("********************开始写" + name + "********************");
        RecordValidProxy recordValidProxy = new RecordValidProxy();
        recordValidProxy.write(ipList, Constant.filePath + "/" + name);
        ProxyRequest.logger.info("********************结束写" + name + "********************");
        removePrefix(name);
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
            e.printStackTrace();
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
}
