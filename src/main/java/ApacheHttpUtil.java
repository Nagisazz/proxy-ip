import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class ApacheHttpUtil {

    public static CloseableHttpResponse sendGet(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get=new HttpGet(url);
        get.setHeader("Accept","*/*");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
        return client.execute(get);
    }
}
