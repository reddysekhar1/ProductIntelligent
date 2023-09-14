package com.intelliservice.agentanalytics.config;



import javax.annotation.Resource;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.intelliservice.agentanalytics.*")
public class ElasticsearchConfig {
	
	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;
	
	@Value("${elasticsearch.host}")
	private String elasticsearchHost;
	
	@Value("${elasticsearch.port}")
	private int elasticsearchPort;
	
	@Value("${elasticsearch.connection-timout}")
	private int connectionTimeout; 
	
	@Value("${elasticsearch.iothread_count}")
	private int elasticsearchIoThreadCount;
	
	@Value("${elasticsearch.socket_timeout}")
	private int elasticsearchSocketTimeout;
	
	@Value("${elasticsearch.connectionrequest_timeout}")
	private int elasticsearchConnectionRequestTimeout;
	
	@Value("${elasticsearch.max_retry_timeout}")
	private int elasticsearchMaxRetryTimeoutMillis;
	

	@Resource
	ElasticsearchConverter elasticsearchConverter;

	@Bean
	RestHighLevelClient client() {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
													.connectedTo(elasticsearchUrl)
													.withConnectTimeout(connectionTimeout)
													.withSocketTimeout(connectionTimeout)
													.build();
		
		return RestClients.create(clientConfiguration).rest();
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchRestTemplate(client(), elasticsearchConverter);
	}
	@Bean(destroyMethod = "close")
	public RestClient esClient() {
		return RestClient.builder(new HttpHost   (elasticsearchHost, elasticsearchPort))./*setPathPrefix("/es").*/setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom().setIoThreadCount(elasticsearchIoThreadCount).build());
            }
        }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(connectionTimeout)
                		.setSocketTimeout(elasticsearchSocketTimeout)
                		.setConnectionRequestTimeout(elasticsearchConnectionRequestTimeout); 
            }
        })//.(elasticsearchMaxRetryTimeoutMillis)
		.build();
	}

}