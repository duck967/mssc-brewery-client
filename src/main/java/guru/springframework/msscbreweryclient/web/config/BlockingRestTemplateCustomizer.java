package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jt on 2019-08-08.
 */
@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {

    private final Integer maxTotalConn;
    private final Integer maxConnPerRoute;
    private final Integer reqTimeout;
    private final Integer socketTimeout;

    

    public BlockingRestTemplateCustomizer(
                    @Value("${sfg.maxtotalconn}") Integer maxTotalConn, 
                    @Value("${sfg.maxconnperroute}") Integer maxConnPerRoute, 
                    @Value("${sfg.reqtimeout}") Integer reqTimeout,
                    @Value("${sfg.sockettimeout}") Integer socketTimeout) 
    {
        this.maxTotalConn = maxTotalConn;
        this.maxConnPerRoute = maxConnPerRoute;
        this.reqTimeout = reqTimeout;
        this.socketTimeout = socketTimeout;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConn);
        connectionManager.setDefaultMaxPerRoute(maxConnPerRoute);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(reqTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        System.out.println("*****************************************************************************************");
        System.out.println("*****************************************************************************************");
        System.out.println("****************   D U C K   C U S T O M   R E S T   T E M P L A T E   ******************");
        System.out.println("*****************************************************************************************");
        System.out.println("*****************************************************************************************");
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());
    }
}