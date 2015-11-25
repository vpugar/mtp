package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import akka.actor.AbstractActor;

@AllArgsConstructor
@Getter
@ToString
public class TopicActorInfo {

	private final String topicDestinationPath;
	private final String name;
	private final String allName;

	public boolean supportsDestination(String destination) {
		return destination.startsWith(topicDestinationPath);
	}

	public String destinationSuffix(String destination) {
		return destination.replace(topicDestinationPath + "/", "");
	}
}
