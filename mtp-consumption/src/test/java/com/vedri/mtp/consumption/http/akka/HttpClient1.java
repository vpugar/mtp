package com.vedri.mtp.consumption.http.akka;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import akka.http.javadsl.model.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;

public class HttpClient1 {

	private final ActorSystem actorSystem;
	private ActorMaterializer materializer;

	@Autowired
	public HttpClient1(ActorSystem actorSystem) {
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

	public Future<HttpResponse> get(String url, HttpHeader... httpHeader) {
		return Http.get(actorSystem)
				.singleRequest(HttpRequest.GET(url)
						.addHeaders(Arrays.asList(httpHeader)), materializer);
	}

	public Future<HttpResponse> post(String url, ContentType type, String data, HttpHeader... httpHeader) {
		return Http.get(actorSystem)
				.singleRequest(HttpRequest.POST(url)
						.addHeaders(Arrays.asList(httpHeader))
						.withEntity(type, data), materializer);
	}
}
