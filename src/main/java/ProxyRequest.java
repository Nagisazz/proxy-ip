import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ProxyRequest {

    public static final Logger logger = LoggerFactory.getLogger(ProxyRequest.class);

    public static void main(String[] args) {
        //创建目录
        File file = new File(Constant.filePath);
        if (!file.exists()){
            file.mkdirs();
        }

        //开启定时任务
        ClearTimeTask task = new ClearTimeTask(Constant.filePath);
        task.clear();

        visit(Constant.url, Constant.proxyUrl,Constant.filePath);
    }

    public static void visit(String url, String proxyUrl,String filePath) {
        String oriProxyUrl = proxyUrl;
        List<ProxyIP> ipList = ProxyGetUtil.getProxyIP(proxyUrl);
        RecordValidProxy recordValidProxy = new RecordValidProxy();

        //代理IP访问次数
        int loop = 500;
        //当前访问总次数
        int now = 0;
        //访问成功次数
        int count = 0;

        for (int i = 1; ; i++) {

            logger.info("--------第" + i + "批代理IP访问开始--------\n");

            for (ProxyIP proxyIP : ipList) {
                now++;
                logger.info("现在是第 " + now + " 次访问");
                logger.info("使用的代理为------" + proxyIP.getAddress() + ":" + proxyIP.getPort());
                try {
                    String result = HttpRequestUtil.sendProxyGet(proxyIP.getAddress(), Integer.parseInt(proxyIP.getPort()), url, "");
                    if (!result.equals("false")) {
                        count++;
                        logger.info("成功访问次数: " + count);
                        logger.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                        recordValidProxy.record(proxyIP,filePath);
                    }else {
                        logger.info("代理GET请求发送异常！");
                        logger.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                    }
                }catch (Exception e){

                }
                logger.info("--------本次访问结束--------\n");

            }

            logger.info("--------第" + i + "批代理IP访问结束--------\n");
            ipList = ProxyGetUtil.getProxyIP(proxyUrl);
        }
    }

}
