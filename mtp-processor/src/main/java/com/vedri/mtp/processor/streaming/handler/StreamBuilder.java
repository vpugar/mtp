package com.vedri.mtp.processor.streaming.handler;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class StreamBuilder<I extends Serializable, O extends Serializable> implements Serializable {

	private StreamBuilder<?, I> prevBuilder;
	private O result;

	protected StreamBuilder(StreamBuilder<?, I> prevBuilder) {
		this.prevBuilder = prevBuilder;
	}

	public O getResult() {
		if (result == null) {
			build();
		}
		return result;
	}

	public void build() {
		if (prevBuilder != null) {
			final I prevBuilderResult = prevBuilder.getResult();
			this.result = doBuild(prevBuilderResult);
		}
		else {
			result = doBuild(null);
		}
	}

	protected abstract O doBuild(I input);

}
