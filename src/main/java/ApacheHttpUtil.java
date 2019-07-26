import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ApacheHttpUtil {

    private static final List<String> ipList = new ArrayList<String>();

    static {
        ipList.add("221.122.91.61:80");
        ipList.add("221.122.91.64:80");
        ipList.add("221.122.91.34:80");
        ipList.add("218.60.8.99:3129");
        ipList.add("218.66.253.144:80");
        ipList.add("39.137.69.8:8080");
        ipList.add("221.122.91.59:80");
        ipList.add("218.66.253.144:80");
        ipList.add("153.35.185.71:80");
        ipList.add("221.122.91.61:80");
        ipList.add("221.122.91.64:80");
        ipList.add("39.137.69.9:80");
        ipList.add("221.122.91.60:80");
        ipList.add("221.122.91.34:80");
        ipList.add("165.225.50.98:10000");
        ipList.add("106.14.116.170:80");
        ipList.add("39.137.69.10:80");
        ipList.add("221.122.91.66:80");
        ipList.add("52.163.120.142:8080");
        ipList.add("58.220.95.107:80");
        ipList.add("221.122.91.65:80");
        ipList.add("114.247.160.9:80");
        ipList.add("218.60.8.99:3129");
        ipList.add("39.137.69.8:8080");
        ipList.add("101.4.136.34:81");
        ipList.add("101.4.136.34:80");
        ipList.add("39.137.107.98:80");
        ipList.add("165.225.80.101:10000");
        ipList.add("70.169.70.83:80");
        ipList.add("221.122.91.61:80");
        ipList.add("39.137.69.9:80");
        ipList.add("114.247.160.11:80");
        ipList.add("47.104.86.214:80");
        ipList.add("221.122.91.34:80");
        ipList.add("165.225.72.100:10000");
        ipList.add("153.35.185.69:80");
        ipList.add("221.122.91.65:80");
        ipList.add("114.247.160.9:80");
        ipList.add("218.60.8.99:3129");
        ipList.add("211.147.65.154:80");
        ipList.add("60.29.106.60:18000");
        ipList.add("39.137.69.9:8080");
        ipList.add("202.111.20.41:80");
        ipList.add("221.122.91.59:80");
        ipList.add("218.66.253.144:80");
        ipList.add("153.35.185.71:80");
        ipList.add("165.225.72.101:10000");
        ipList.add("114.247.160.14:80");
        ipList.add("221.122.91.64:80");
        ipList.add("39.137.69.8:80");
        ipList.add("165.225.16.194:10000");
    }

    public static CloseableHttpResponse sendGet(String url) throws IOException {
        Random random = new Random();
        String ip = ipList.get(random.nextInt(50) + 1);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
        CloseableHttpClient client = HttpClients.custom()
                .setProxy(new HttpHost(ip.split(":")[0], Integer.parseInt(ip.split(":")[1])))
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "*/*");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
        return client.execute(get);
    }
}
