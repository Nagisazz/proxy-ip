import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RecordValidProxy {

    private Set<String> proxysSet;
    private Set<String> proxysClear;

    //设定Set的size达到一定值时开始写入文件
    private int length = 5;
    //状态标志
    private int clear = 0;

    public RecordValidProxy() {
        proxysSet = new HashSet<String>();
        proxysClear = new HashSet<String>();
    }

    public void record(ProxyIP ip) {
        String proxyIP = ip.getAddress() + ":" + ip.getPort();
        System.out.println("proxysSet :"+proxysSet.size());
        if (proxysSet.size() >= length) {
            proxysClear.addAll(proxysSet);
            proxysSet.clear();
            proxysSet.add(proxyIP);
            if (clear == 0) {
                clear = 1;
                new Thread() {
                    public void run() {
                        System.out.println("*********************开始写IP*********************");
                        write(proxysClear,"F:\\me\\proxy-ip\\proxyip\\"+new SimpleDateFormat("yyyyMMdd").format(new Date()));
                        proxysClear.clear();
                        clear = 0;
                        System.out.println("*********************结束写IP*********************");
                    }
                }.start();
            }
        } else {
            proxysSet.add(proxyIP);
        }
    }

    public static void write(Set<String> proxys,String fileName) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true)));
            for (String proxy : proxys) {
                out.write(proxy);
                out.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
