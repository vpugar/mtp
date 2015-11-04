package com.vedri.mtp.consumption.http.akka;

import static akka.http.javadsl.model.HttpResponse.create;
import static akka.http.javadsl.model.StatusCodes.*;
import static akka.http.javadsl.server.RequestVals.entityAs;
import static akka.http.javadsl.server.values.PathMatchers.uuid;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.CustomJackson;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.model.headers.Location;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.scaladsl.Http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.consumption.transaction.ValidationFailedException;
import com.vedri.mtp.core.transaction.Transaction;

@Slf4j
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

	// TODO Unsupported Media Type
	@Override
	public Route createRoute() {

		PathMatcher<UUID> uuidExtractor = uuid();

		final Route getRoute = get(path(uuidExtractor)
				.route(handleWith1(uuidExtractor,
						(ctx, uuid) -> ctx.completeAs(CustomJackson.json(objectMapper),
								akkaHttpServer.doGetTransaction(uuid)))));

		final Route postRoute = post(handleWith1(entityAs(CustomJackson.jsonAs(objectMapper, Transaction.class)),
				(ctx, transaction) -> {
					try {
						Transaction added = akkaHttpServer.doAddTransaction(transaction);
						return ctx.complete(HttpResponse
								.create()
								.withStatus(CREATED)
								.addHeader(
										Location.create(
												Uri.create(defaultPublicUri + "transactions/"
														+ added.getTransactionId()))));
					}
					catch (Exception e) {
						throw new IllegalArgumentException(e.getMessage(), e);
					}
				}));

		final ExceptionHandler exceptionHandler = e -> {
			if (e instanceof IllegalArgumentException || e instanceof ValidationFailedException) {
				log.info("Parsing failed {}", e.getMessage());
				return complete(HttpResponse
						.create()
						.withStatus(BAD_REQUEST)
						.withEntity(e.getMessage()));
			}
			else {
				log.error("Request failed" + e.getMessage(), e);
				return complete(create().withStatus(INTERNAL_SERVER_ERROR));
			}
		};

		return handleExceptions(exceptionHandler, pathPrefix("transactions").route(getRoute, postRoute));
	}
}
