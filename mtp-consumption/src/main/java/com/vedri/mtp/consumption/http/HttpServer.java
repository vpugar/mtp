package com.vedri.mtp.consumption.http;

import akka.actor.ActorRef;

public interface HttpServer {

    void start(ActorRef consumerActorRef) throws Exception;

    void stop() throws Exception;

}
