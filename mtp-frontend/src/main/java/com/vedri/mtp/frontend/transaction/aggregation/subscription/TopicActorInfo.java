package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopicActorInfo {

	private final Class<? extends AbstractActor> type;
	private final String name;
}
