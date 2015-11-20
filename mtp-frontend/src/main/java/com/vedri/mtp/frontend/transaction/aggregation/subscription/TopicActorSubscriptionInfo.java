package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopicActorSubscriptionInfo<F> {
	private F filter;
}
