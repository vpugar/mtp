package com.vedri.mtp.core.support.http;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.http.scaladsl.model.ContentTypes;
import akka.http.scaladsl.model.HttpEntity;
import akka.stream.ActorMaterializer;
import akka.util.ByteString;

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

		final HttpRequest httpRequest = HttpRequest
				.create(uri.toString())
				.withMethod(HttpMethods.GET)
				// .addHeader(Connection.apply("close", JavaConversions.asScalaBuffer(Collections.emptyList())))
				.addHeaders(Arrays.asList(httpHeader));

		final Http http = Http.get(actorSystem);
		return http.singleRequest(httpRequest, materializer);
	}

	public Future<HttpResponse> post(String url, ContentType type, String data, HttpHeader... httpHeader) {
		return Http.get(actorSystem)
				.singleRequest(HttpRequest.POST(url)
						.addHeaders(Arrays.asList(httpHeader))
						// .addHeader(Connection.apply("close", JavaConversions.asScalaBuffer(Collections.emptyList())))
						.withEntity(
								new HttpEntity.Strict(ContentTypes.application$divjson(), ByteString.fromString(data))),
						materializer);
	}
}
