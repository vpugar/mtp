package com.vedri.mtp.consumption.http;

import com.vedri.mtp.consumption.ConsumptionProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import akka.actor.ActorRef;

import com.vedri.mtp.consumption.MtpConsumptionConstants;

@Getter
@Slf4j
public abstract class AbstractHttpServer implements HttpServer {

	protected final ConsumptionProperties.HttpServer httpServer;

	protected AbstractHttpServer(ConsumptionProperties.HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	@Override
	public void start(ActorRef consumerActorRef) throws Exception {
		log.info("Starting http server");
		doStart(consumerActorRef);
	}

	@Override
	public void stop() throws Exception {
		log.info("Stopping http server");
		doStop();
	}

	protected abstract void doStart(ActorRef consumerActorRef) throws Exception;

	protected abstract void doStop() throws Exception;

}
