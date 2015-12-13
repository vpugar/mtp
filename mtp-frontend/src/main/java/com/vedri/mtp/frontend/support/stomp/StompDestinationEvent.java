package com.vedri.mtp.frontend.support.stomp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class StompDestinationEvent {

	private final String destination;
	private final String user;

}
