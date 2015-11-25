package com.vedri.mtp.frontend.support.stomp;


import lombok.ToString;

@ToString(callSuper = true)
public class NewDestinationEvent extends StompDestinationEvent {

    public NewDestinationEvent(String destination) {
        super(destination);
    }
}
