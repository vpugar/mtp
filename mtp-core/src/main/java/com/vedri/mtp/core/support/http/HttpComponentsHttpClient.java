package com.vedri.mtp.core.support.http;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class HttpComponentsHttpClient {

	private CloseableHttpClient httpclient;

	@PostConstruct
	public void init() {

		SSLContext sslContext = SSLContexts.createSystemDefault();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext,
				NoopHostnameVerifier.INSTANCE);
		httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
	}

	@PreDestroy
	public void destroy() throws Exception {
		httpclient.close();
	}

	public String get(String url, Map<String, String> params) throws Exception {
		HttpGet httpget = new HttpGet(url);

		final URIBuilder uriBuilder = new URIBuilder(url);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			uriBuilder.addParameter(entry.getKey(), entry.getValue());
		}

		try (CloseableHttpResponse response = httpclient.execute(httpget)) {
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		}
	}
}
