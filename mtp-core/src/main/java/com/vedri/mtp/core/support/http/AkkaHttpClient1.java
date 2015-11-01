package com.vedri.mtp.core.support.http;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import akka.http.impl.model.JavaUri;
import akka.http.javadsl.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class AkkaHttpClient1 {

	private final ActorSystem actorSystem;
	private ActorMaterializer materializer;

	@Autowired
	public AkkaHttpClient1(ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}

	@PostConstruct
	public void init() {
		materializer = ActorMaterializer.create(actorSystem);
	}

	@PreDestroy
	public void destroy() {
		if (materializer != null) {
			materializer.shutdown();
		}
	}

	public Future<HttpResponse> get(String url, Map<String, String> params, HttpHeader... httpHeader) {

		final Uri uri = Uri.create(url);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			uri.addParameter(entry.getKey(), entry.getValue());
		}
		return Http.get(actorSystem)
				.singleRequest(HttpRequest.create(uri.toString()).withMethod(HttpMethods.GET)
						.addHeaders(Arrays.asList(httpHeader)), materializer);
	}

	public Future<HttpResponse> post(String url, ContentType type, String data, HttpHeader... httpHeader) {
		return Http.get(actorSystem)
				.singleRequest(HttpRequest.POST(url)
						.addHeaders(Arrays.asList(httpHeader))
						.withEntity(type, data), materializer);
	}
}
