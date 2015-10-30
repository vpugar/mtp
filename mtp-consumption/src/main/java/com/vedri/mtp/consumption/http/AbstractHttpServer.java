package com.vedri.mtp.consumption.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import akka.actor.ActorRef;

import com.vedri.mtp.consumption.MtpConsumptionConstants;

@Getter
@Slf4j
public abstract class AbstractHttpServer implements HttpServer {

	@Value(MtpConsumptionConstants.SERVER_BIND_HOST)
	private String bindHost;
	@Value(MtpConsumptionConstants.SERVER_BIND_PORT)
	private int bindPort;
	@Value(MtpConsumptionConstants.SERVER_PUBLIC_PROTOCOL)
	private String publicProtocol;
	@Value(MtpConsumptionConstants.SERVER_PUBLIC_HOST)
	private String publicHost;
	@Value(MtpConsumptionConstants.SERVER_PUBLIC_PORT)
	private int publicPort;

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
