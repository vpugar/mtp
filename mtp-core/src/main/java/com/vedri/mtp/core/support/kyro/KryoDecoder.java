package com.vedri.mtp.core.support.kyro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * From example: https://github.com/hisunwei/kafka-kryo-codec/blob/master/src/main/java/kafka/kryo/KryoDecoder.java
 */
public class KryoDecoder<T> implements kafka.serializer.Decoder<T> {

	private final Kryo kyro;
	private final Class<T> clazz;

	@SuppressWarnings("unchecked")
	public KryoDecoder(final Kryo kyro) {
		this.kyro = kyro;
		Type type = getClass().getGenericSuperclass();
		Type[] trueType = ((ParameterizedType) type).getActualTypeArguments();
		this.clazz = (Class<T>) trueType[0];
		kyro.register(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T fromBytes(byte[] buffer) {
		try (Input input = new Input(buffer)) {
			return kyro.readObject(input, clazz);
		}
	}
}