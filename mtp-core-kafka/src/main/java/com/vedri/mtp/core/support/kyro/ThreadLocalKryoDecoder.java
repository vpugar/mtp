package com.vedri.mtp.core.support.kyro;

public class ThreadLocalKryoDecoder<T> implements kafka.serializer.Decoder<T> {

	private final KryoThreadLocal kryoThreadLocal;
	private final KryoDecoder<T> kryoDecoder;

	public ThreadLocalKryoDecoder(KryoThreadLocal kryoThreadLocal, KryoDecoder<T> kryoDecoder) {
		this.kryoThreadLocal = kryoThreadLocal;
		this.kryoDecoder = kryoDecoder;
	}

	@Override
	public T fromBytes(byte[] bytes) {
		return KryoDecoder.fromBytes(bytes, kryoThreadLocal.get(), kryoDecoder.getClazz());
	}
}
