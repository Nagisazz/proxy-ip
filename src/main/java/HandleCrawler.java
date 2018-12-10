import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleCrawler {

    public static void setCookie() throws IOException, ScriptException {
        CloseableHttpResponse response = ApacheHttpUtil.sendGet(Constant.proxyUrl);
        if(response.getStatusLine().getStatusCode()==521) {
            String yd_cookie = getYdCookie(response.getAllHeaders());
            ProxyRequest.logger.info("yd_cookie is :"+yd_cookie);

            HttpEntity entity = response.getEntity();
            String html=EntityUtils.toString(entity,"utf-8");
            String runString = getRunString(html);
            String fuction = html.substring(html.indexOf("function")).replace("</script> </body></html>",runString+";").replace("eval(\"qo=eval;qo(po);\")","return po");
            ProxyRequest.logger.info("fuction is :"+fuction);

            ScriptEngineManager m = new ScriptEngineManager(); //获取JavaScript执行引擎
            ScriptEngine engine = m.getEngineByName("JavaScript"); //执行JavaScript代码
            String origin = (String) engine.eval(fuction);
            ProxyRequest.logger.info("origin ydclearance is :"+origin);
            String ydclearance = getYdclearance(origin);
            ProxyRequest.logger.info("ydclearance is :"+ydclearance);

            Constant.COOKIE = "yd_cookie="+yd_cookie+"; _ydclearance="+ydclearance;
        }
    }

    private static String getYdCookie(Header[] headers){
        String yd_cookie = null;
        for(Header header:headers){
            if (header.getName().equals("Set-Cookie")){
                yd_cookie = header.getValue();
            }
        }
        Pattern pattern = Pattern.compile("(?<=yd_cookie=).+?(?=; Expires=)");
        Matcher matcher = pattern.matcher(yd_cookie);
        while (matcher.find()){
            yd_cookie = matcher.group(0);
        }
        return yd_cookie;
    }

    private static String getYdclearance(String origin){
        String ydclearance = null;
        Pattern pattern = Pattern.compile("(?<=_ydclearance=).+?(?=; expires=)");
        Matcher matcher = pattern.matcher(origin);
        while (matcher.find()){
            ydclearance = matcher.group(0);
        }
        return ydclearance;
    }

    private static String getRunString(String html){
        Pattern pattern = Pattern.compile("(?<=window.onload=setTimeout\\(\").+?(?=\", 200\\))");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()){
            return matcher.group(0);
        }
        return null;
    }
}
