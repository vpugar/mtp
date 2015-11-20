package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopicActorInfo {

	private final String topicDestinationPath;
	private final Class<? extends AbstractActor> type;
	private final String name;

	public boolean supportsDestination(String destination) {
		return topicDestinationPath.startsWith(destination);
	}

	public String destinationSuffix(String destination) {
		return destination.replace(topicDestinationPath + "/", "");
	}
}
