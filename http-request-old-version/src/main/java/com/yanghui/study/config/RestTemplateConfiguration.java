package com.yanghui.study.config;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 请求接收参数为json字符串服务的RestTemplate
 * @author yanghui
 *
 */
@Configuration
public class RestTemplateConfiguration {
	
	@Value("${jsifs.max.total}")
	private int maxTotal;

	@Value("${jsifs.max.perroute}")
	private int maxPerRoute;

	@Value("${jsifs.socket.timeout}")
	private int socketTimeout;

	@Value("${jsifs.connect.timeout}")
	private int connectTimeout;

	@Value("${jsifs.connection.request.timeout}")
	private int connectionRequestTimeout;

	@Value("${jsifs.request.retry}")
	private int requestRetry;

	@Bean
	public RestTemplate restTemplateWithJsonString(){
		RestTemplate restTemplateWithJsonString = new RestTemplate(clientHttpRequestFactory());
		restTemplateWithJsonString.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<>();
		interceptorList.add(new RestTemplateInterceptor());
		restTemplateWithJsonString.setInterceptors(interceptorList);
		return restTemplateWithJsonString;
	}
	
	/**
	 * <p>HttpComponentsClientHttpRequestFactory构造RestTemplate</p>
	 * 配置http连接池:<br/>
	 * <pre>
	 * 最大连接数
	 * 同路由并发数
	 * 开启重试机制，设置重试次数
	 * 连接超时时间
	 * 数据读取超时时间
	 * 从连接池中获取连接的超时时间
	 * 内存受限时可调低最大连接数，路由并发数
	 * </pre>
	 * 兼容http/https
	 * @return HttpComponentsClientHttpRequestFactory实例
	 */
	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        try {
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            httpClientBuilder.setSslcontext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            //最大连接数 
            poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
            //同路由并发数
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            //new DefaultHttpRequestRetryHandler(requestRetry, true)
            httpClientBuilder.setRetryHandler(httpRequestRetryHandler());
            //设置从连接池中获取连接的超时时间,请求超时时间和传输超时时间
            RequestConfig requestConfig = RequestConfig
            		.custom()
            		.setSocketTimeout(socketTimeout)
            		.setConnectTimeout(connectTimeout)
            		.setConnectionRequestTimeout(connectionRequestTimeout)
            		.build();
            httpClientBuilder.setDefaultRequestConfig(requestConfig);
            CloseableHttpClient httpClient = httpClientBuilder.build();
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            return clientHttpRequestFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            //
        }
        return null;
    }

	/**
	 * 自定义重试机制
	 * 根据I/O异常类型、重试次数以及请求是否幂等决定重试与否
	 * @return HttpRequestRetryHandler实现
	 */
	@Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
		HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount > requestRetry) //超过重试次数，就放弃
					return false;
				if (exception instanceof NoHttpResponseException) {//没有响应，重试
					return true;
				} else if (exception instanceof ConnectTimeoutException) {//连接超时，重试
					return true;
				} else if (exception instanceof SocketTimeoutException) {//连接或读取超时，重试
					return true;
				}else if(exception instanceof org.apache.http.conn.HttpHostConnectException){//连接不上服务器，重试
					return true;
				} else if (exception instanceof SSLHandshakeException) {//本地证书异常
					return false;
				} else if (exception instanceof InterruptedIOException) {//被中断
					return false;
				} else if (exception instanceof UnknownHostException) {//找不到服务器
					return false;
				} else if (exception instanceof SSLException) {//SSL异常
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				org.apache.http.HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，则重试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		return requestRetryHandler;
	}

	/**
	 * 请求拦截器，用于规范请求与返回(请求参数以及返回值皆为json字符串)
	 */
	private static class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			HttpHeaders headers = request.getHeaders();
			//json格式
	        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
	        return execution.execute(request, body);
		}
    }
}
