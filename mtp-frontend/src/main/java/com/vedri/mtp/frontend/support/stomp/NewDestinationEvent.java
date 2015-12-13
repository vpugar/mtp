package com.vedri.mtp.frontend.support.stomp;


import lombok.ToString;

@ToString(callSuper = true)
public class NewDestinationEvent extends StompDestinationEvent {

    public NewDestinationEvent(final String destination, final String user) {
        super(destination, user);
    }
}
