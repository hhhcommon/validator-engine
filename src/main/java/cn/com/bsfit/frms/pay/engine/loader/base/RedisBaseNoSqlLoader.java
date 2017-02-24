package cn.com.bsfit.frms.pay.engine.loader.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import cn.com.bsfit.frms.base.load.DataLoader;
import cn.com.bsfit.frms.obj.MemCachedItem;
import cn.com.bsfit.frms.pay.engine.kryo.KryoRedisSerializer;

public abstract class RedisBaseNoSqlLoader implements DataLoader {
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	protected RedisSerializer<String> keySerializer = new StringRedisSerializer();
    protected RedisSerializer<String> stringSerializer = new StringRedisSerializer();
	protected RedisSerializer<MemCachedItem> valueSerializer = new KryoRedisSerializer<MemCachedItem>(MemCachedItem.class);

	public byte[] getMemCachedItem(final String keyId) {
		return getMemCachedItemWithKryo(keyId);
	//	return getMemCachedItemWithFastjson(keyId);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public byte[] getMemCachedItemWithFastjson(final String keyId) {
		return (byte[]) this.redisTemplate.execute(new RedisCallback() {
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer serializer = redisTemplate.getStringSerializer();
				byte[] key = serializer.serialize(keyId);
				byte[] value = connection.get(key);

				if (value == null) {
					return null;
				}

//				logger.info("redis-key:[{}]", keyId);
//				MemCachedItem m = JSON.parseObject(value,MemCachedItem.class, new Feature[0]);
//				logger.info("redis-value:[{}]", JSON.toJSON(m));
				
				return value;
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<byte[]> getFuzzyMemCachedItemWithFastjson(final String keyId) {
		return (List<byte[]>) this.redisTemplate.execute(new RedisCallback() {
			public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				List<byte[]> list = new ArrayList<byte[]>();
				byte[] key = keySerializer.serialize(keyId);
				Set<byte[]> keys = connection.keys(key);
				
				for (byte[] rediskey : keys) {
					byte[] value = connection.get(rediskey);

					if (value == null) {
						return null;
					}
					
//					MemCachedItem m = JSON.parseObject(value,MemCachedItem.class, new Feature[0]);
//					logger.info("redis-key:[{}]", new String(rediskey));
//					logger.info("redis-value:[{}]", JSON.toJSON(m));
					
					list.add(value);
				}

				return list;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<byte[]> getFuzzyMemCachedItemWithKryo(final String keyId) {
		return (List<byte[]>) this.redisTemplate.execute(new RedisCallback() {
			public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				List<byte[]> list = new ArrayList<byte[]>();
				byte[] key = keySerializer.serialize(keyId);
				Set<byte[]> keys = connection.keys(key);
				
				for (byte[] rediskey : keys) {
					byte[] value = connection.get(rediskey);

					if (value == null) {
						return null;
					}
					
				//	logger.info("redis-key:[{}]", new String(rediskey));
//					MemCachedItem m = (MemCachedItem) valueSerializer.deserialize(value);
				//	logger.info("redis-value:[{}]", JSON.toJSON(m));
					
					list.add(value);
				}

				return list;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public byte[] getMemCachedItemWithKryo(final String keyId) {
		return (byte[]) this.redisTemplate.execute(new RedisCallback() {
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] key = keySerializer.serialize(keyId);
				byte[] value = connection.get(key);
				
				if (value == null) {
					return null;
				}
				
				//logger.info("redis-key:[{}]", keyId);
//				MemCachedItem m = (MemCachedItem) valueSerializer.deserialize(value);
				//logger.info("redis-value:[{}]", JSON.toJSON(m));
				
				return value;
			}
		});
	}

}
