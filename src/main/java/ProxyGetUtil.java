import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyGetUtil {

    public static List<ProxyIP> getProxyIP(String url) {
        List<ProxyIP> ipList = null;
        try {
            //向ip代理地址发起get请求，获得Document对象
            Document doc = getDocument(url);

            //将得到的Document对象解析成字符串
            String ipStr = doc.body().text().trim().toString();

            //正则匹配拿到IP地址集合
            ipList = new ArrayList<ProxyIP>();
            List<String> ips = new ArrayList<String>();
            String lines[] = ipStr.split(" ");
            for (int i = 0; i < lines.length; i++) {
                Pattern ipreg = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}");

                Matcher ip = ipreg.matcher(lines[i]);
                while (ip.find()) {
                    System.out.println(ip.group());
                    ips.add(ip.group());
                }
            }

            //循环遍历得到的IP地址集合
            for (final String ip : ips) {
                ProxyRequest.logger.info(ip);
                ProxyIP proxyIP = new ProxyIP();
                String[] temp = ip.split(":");
                proxyIP.setAddress(temp[0].trim());
                proxyIP.setPort(temp[1].trim());
                ipList.add(proxyIP);
            }
        } catch (IOException e) {
            ProxyRequest.logger.info("URL加载失败！");
        }

        ProxyRequest.logger.info("一共有："+ipList.size()+"个IP");
        return ipList;
    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .header("accept", "*/*")
                .header("connection", "Keep-Alive")
                .header("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                .timeout(5000)
                .get();
    }
}
