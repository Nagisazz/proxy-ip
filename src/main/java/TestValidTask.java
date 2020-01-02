import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestValidTask {

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    public void start(ProxyIP proxyIP, String url, String params, Set<String> validIP, CountDownLatch countDownLatch) {
        pool.execute(new TestValidThread(proxyIP, url, params, validIP, countDownLatch));
    }

    public void stop() {
        pool.shutdown();
    }

    class TestValidThread implements Runnable {

        private ProxyIP proxyIP;
        private String url;
        private String params;
        private Set<String> validIP;
        private CountDownLatch countDownLatch;

        public TestValidThread(ProxyIP proxyIP, String url, String params, Set<String> validIP, CountDownLatch countDownLatch) {
            this.proxyIP = proxyIP;
            this.url = url;
            this.params = params;
            this.validIP = validIP;
            this.countDownLatch = countDownLatch;
        }

        public void run() {
//            System.out.println("当前线程为："+Thread.currentThread().getName());
            String result = HttpRequestUtil.sendProxyGet(proxyIP.getAddress(), Integer.parseInt(proxyIP.getPort()), url, "");
            if (!result.equals("false")) {
                validIP.add(proxyIP.getAddress() + ":" + proxyIP.getPort());
                ProxyRequest.logger.info("********************" + proxyIP.getAddress() + ":" + proxyIP.getPort() + "验证有效********************");
            }else {
                ProxyRequest.logger.info("********************" + proxyIP.getAddress() + ":" + proxyIP.getPort() + "验证！！！无效！！！********************");
            }
            countDownLatch.countDown();
        }
    }
}
