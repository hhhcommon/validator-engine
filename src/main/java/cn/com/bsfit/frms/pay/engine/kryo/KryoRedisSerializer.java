package cn.com.bsfit.frms.pay.engine.kryo;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class KryoRedisSerializer<T>  implements RedisSerializer<T>{
	private final Class<?> clazz;	
	
	public KryoRedisSerializer(Class<?> clazz) {
		super();
		this.clazz = clazz;
	}
	
	public byte[] serialize(T t) throws SerializationException {
		if (t == null)
			return null;
		return KryoSerializationUtils.serialize(t);
	}

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0)
			return null;
		return (T) KryoSerializationUtils.deserialize(bytes, clazz);
	}

	
}
