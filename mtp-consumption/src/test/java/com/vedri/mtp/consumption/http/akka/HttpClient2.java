package com.vedri.mtp.consumption.http.akka;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import scala.Tuple2;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;
import scala.util.Try;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class HttpClient2 {

	private final ActorSystem actorSystem;
	private ActorMaterializer materializer;
	private Flow<Tuple2<HttpRequest, Integer>, Tuple2<Try<HttpResponse>, Integer>, BoxedUnit> flow;

	@Autowired
	public HttpClient2(ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}

	@PostConstruct
	public void init() {
		materializer = ActorMaterializer.create(actorSystem);
		flow = Http.get(actorSystem).superPool(materializer);
	}

	public Future<Tuple2<Try<HttpResponse>, Integer>> get(String url, HttpHeader... httpHeader) {
		final HttpRequest httpRequest = HttpRequest
				.create()
				.addHeaders(Arrays.asList(httpHeader))
				.withMethod(HttpMethods.GET)
				.withUri(url);
		return Source
				.single(Pair.create(httpRequest, 30).toScala())
				.via(flow)
				.runWith(Sink.<Tuple2<Try<HttpResponse>, Integer>> head(), materializer);
	}

	public Future<Tuple2<Try<HttpResponse>, Integer>> post(String url, ContentType type, String data,
			HttpHeader... httpHeader) {
		final HttpRequest httpRequest = HttpRequest
				.create()
				.addHeaders(Arrays.asList(httpHeader))
				.withMethod(HttpMethods.POST)
				.withUri(url)
				.withEntity(type, data);
		return Source
				.single(Pair.create(httpRequest, 30).toScala())
				.via(flow)
				.runWith(Sink.<Tuple2<Try<HttpResponse>, Integer>> head(), materializer);
	}
}
