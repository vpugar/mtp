package com.vedri.mtp.core.rate;

public class NoRateException extends Exception {

	private final Rate.Key key;

	public NoRateException(Rate.Key key) {
		this.key = key;
	}

	public NoRateException(Rate.Key key, String message) {
		super(message);
		this.key = key;
	}
}
