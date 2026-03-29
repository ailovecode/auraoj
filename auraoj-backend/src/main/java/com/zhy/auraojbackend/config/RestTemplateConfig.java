package com.zhy.auraojbackend.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类 - 优化 HTTP 连接池参数
 * @author zhy
 * @Date 2026/3/29
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 配置带有连接池的 RestTemplate
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    /**
     * 配置 HTTP 请求工厂，使用连接池
     * @return ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient());
        return factory;
    }

    /**
     * 配置 HttpClient 连接池
     * @return CloseableHttpClient
     */
    private CloseableHttpClient httpClient() {
        // 创建连接池配置
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        
        // 最大连接数
        connectionManager.setMaxTotal(200);
        // 每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(50);
        
        // 配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接超时时间（毫秒）
                .setConnectTimeout(Timeout.ofMilliseconds(5000))
                // 从连接池获取连接的超时时间（毫秒）
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(3000))
                // 数据读取超时时间（毫秒）
                .setResponseTimeout(Timeout.ofMilliseconds(30000))
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // 禁用自动关闭连接，复用连接池
                .disableContentCompression()
                // 保持长连接
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(60))
                // 60 秒
                .build();
    }
}
