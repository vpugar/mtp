package com.vedri.mtp.core.support.kyro;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * From example: https://github.com/hisunwei/kafka-kryo-codec/blob/master/src/main/java/kafka/kryo/KryoEncoder.java
 */
public class KryoEncoder<T> implements kafka.serializer.Encoder<T> {

	private final Kryo kyro;

	@SuppressWarnings("unchecked")
	public KryoEncoder(final Kryo kyro) {
		this.kyro = kyro;
		Type type = getClass().getGenericSuperclass();
		Type[] trueType = ((ParameterizedType) type).getActualTypeArguments();
		kyro.register((Class<T>) trueType[0]);
	}

	@Override
	public byte[] toBytes(T object) {
		try (Output out = new Output(new ByteArrayOutputStream())) {
			kyro.writeObject(out, object);
			return out.getBuffer();
		}
	}

}