package com.vedri.mtp.consumption.http.akka;

import static akka.http.javadsl.model.HttpResponse.create;
import static akka.http.javadsl.model.StatusCodes.CREATED;
import static akka.http.javadsl.model.StatusCodes.INTERNAL_SERVER_ERROR;
import static akka.http.javadsl.server.RequestVals.entityAs;
import static akka.http.javadsl.server.values.PathMatchers.uuid;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.model.headers.Location;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.scaladsl.Http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.core.transaction.Transaction;

class MtpHttpApp extends HttpApp {

	private final AkkaHttpServer akkaHttpServer;
	private final ActorSystem akkaSystem;
	private final ObjectMapper objectMapper;
	private String defaultPublicUri;
	private Http.ServerBinding serverBinding;

	public MtpHttpApp(final AkkaHttpServer akkaHttpServer) {
		this.akkaSystem = akkaHttpServer.getAkkaSystem();
		this.objectMapper = akkaHttpServer.getTransactionObjectMapper();
		this.akkaHttpServer = akkaHttpServer;
	}

	public synchronized void start(String bindHost, int bindPort,
			final String publicProtocol, final String publicHost, final int publicPort)
					throws Exception {
		if (serverBinding == null) {
			final Future<Http.ServerBinding> serverBindingFuture = bindRoute(bindHost, bindPort, akkaSystem);
			serverBinding = Await.result(serverBindingFuture, Duration.apply(30, TimeUnit.SECONDS));
			defaultPublicUri = publicProtocol + "://" + publicHost + ":" + publicPort + "/";
		}
		else {
			throw new IllegalStateException("Cannot start http app because already started");
		}
	}

	public synchronized void stop() throws Exception {
		if (serverBinding != null) {
			final Future<BoxedUnit> unbindFuture = serverBinding.unbind();
			Await.result(unbindFuture, Duration.apply(30, TimeUnit.SECONDS));
			serverBinding = null;
		}
	}

	@Override
	public Route createRoute() {

		PathMatcher<UUID> uuidExtractor = uuid();

		Route getRoute = get(path(uuidExtractor)
				.route(handleWith1(uuidExtractor,
						(ctx, uuid) -> ctx.completeAs(Jackson.json(objectMapper),
								akkaHttpServer.doGetTransaction(uuid)))));

		Route postRoute = post(handleWith1(entityAs(Jackson.jsonAs(objectMapper, Transaction.class)),
				(ctx, transaction) -> {
					Transaction added = akkaHttpServer.doAddTransaction(transaction);
					return ctx.complete(HttpResponse.create()
							.withStatus(CREATED)
							.addHeader(
									Location.create(
											Uri.create(defaultPublicUri + "transactions/"
													+ added.getTransactionId()))));
				}));

		final ExceptionHandler exceptionHandler = e -> {
			e.printStackTrace();
			return complete(create().withStatus(INTERNAL_SERVER_ERROR));
		};

		return handleExceptions(exceptionHandler, pathPrefix("transactions").route(getRoute, postRoute));
	}
}
