package com.vedri.mtp.core.support.akka;

public interface LifecycleMessage extends AkkaMessage {

	class OutputStreamInitialized implements LifecycleMessage {
	}

	class NodeInitialized implements LifecycleMessage {
	}

	class Start implements LifecycleMessage {
	}

	class DataFeedStarted implements LifecycleMessage {
	}

	class Shutdown implements LifecycleMessage {
	}

	class TaskCompleted implements LifecycleMessage {
	}
}
