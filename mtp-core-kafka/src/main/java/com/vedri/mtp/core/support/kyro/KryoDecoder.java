package com.vedri.mtp.core.support.kyro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * From example: https://github.com/hisunwei/kafka-kryo-codec/blob/master/src/main/java/kafka/kryo/KryoDecoder.java
 */
@Getter(AccessLevel.PACKAGE)
public class KryoDecoder<T> implements kafka.serializer.Decoder<T> {

	private final Kryo kryo;
	private final Class<T> clazz;

	@SuppressWarnings("unchecked")
	public KryoDecoder(final Kryo kyro) {
		this.kryo = kyro;
		Type type = getClass().getGenericSuperclass();
		Type[] trueType = ((ParameterizedType) type).getActualTypeArguments();
		this.clazz = (Class<T>) trueType[0];
		kyro.register(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T fromBytes(byte[] buffer) {
		return fromBytes(buffer, kryo, clazz);
	}

	public static <T> T fromBytes(byte[] buffer, Kryo kryo, Class<T> clazz) {
		try (Input input = new Input(buffer)) {
			return kryo.readObject(input, clazz);
		}
	}
}