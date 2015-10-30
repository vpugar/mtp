package com.vedri.mtp.core.support.akka;

public interface AkkaTask extends AkkaMessage {

    class QueryTask implements AkkaTask {

    }

    class GracefulShutdown implements AkkaTask {

    }
}
