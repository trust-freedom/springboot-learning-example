package com.freedom.springboot.elasticsearch.jest.config;

import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 定制Jest底层的httpclient的config
 * 部分配置可以通过配置文件的spring.elasticsearch.jes.xxx配置，如连接URL、connection timeout、read timeout
 * 部分需要通过实现HttpClientConfigBuilderCustomizer定制
 * 具体见：JestAutoConfiguration、JestClientFactory#getObjetc()
 */
@Component
public class JestHttpClientConfigBuilderCustomizer implements HttpClientConfigBuilderCustomizer {

    //最大连接数，默认值：30
    @Value("${spring.elasticsearch.jest.maxTotalConnection:30}")
    private int maxTotalConnection;

    //默认每个路由的最大连接数，默认值：10
    @Value("${spring.elasticsearch.jest.defaultMaxTotalConnectionPerRoute:10}")
    private int defaultMaxTotalConnectionPerRoute;

    //连接空闲多少秒会被认定为空闲连接并关闭，默认值：0，不开启空闲连接检测
    @Value("${spring.elasticsearch.jest.maxConnectionIdleTimeInSecond:0}")
    private long maxConnectionIdleTimeInSecond;

    //是否启用自动发现ES Node，默认值：false
    @Value("${spring.elasticsearch.jest.discoveryEnabled:false}")
    private boolean discoveryEnabled;

    //自动发现的频率，单位秒，默认60s
    @Value("${spring.elasticsearch.jest.discoveryFrequencyInSecond:60}")
    private long discoveryFrequencyInSecond;

    @Override
    public void customize(HttpClientConfig.Builder builder) {
        builder.maxTotalConnection(maxTotalConnection);  //最大连接数
        builder.defaultMaxTotalConnectionPerRoute(defaultMaxTotalConnectionPerRoute);   //每个路由的最大连接数
        builder.maxConnectionIdleTime(maxConnectionIdleTimeInSecond, TimeUnit.SECONDS); //连接空闲时间

        //自动发现ES Node
        builder.discoveryEnabled(discoveryEnabled);
        if(discoveryEnabled){
            builder.discoveryFrequency(discoveryFrequencyInSecond, TimeUnit.SECONDS);
        }
    }

}
