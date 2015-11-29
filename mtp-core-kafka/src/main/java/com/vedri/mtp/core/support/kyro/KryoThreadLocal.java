package com.vedri.mtp.core.support.kyro;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.esotericsoftware.kryo.Kryo;

public class KryoThreadLocal extends ThreadLocal<Kryo> implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	protected Kryo initialValue() {
		return applicationContext.getBean(Kryo.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
