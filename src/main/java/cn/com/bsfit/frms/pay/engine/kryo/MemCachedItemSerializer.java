package cn.com.bsfit.frms.pay.engine.kryo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.com.bsfit.frms.obj.MemCachedItem;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;

public class MemCachedItemSerializer extends Serializer<MemCachedItem> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map create(Kryo kryo, Input input, Class type) {
        return (Map) kryo.newInstance(type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public MemCachedItem read(Kryo kryo, Input input, Class<MemCachedItem> class1) {
        MemCachedItem item = new MemCachedItem();
        item.setPrimaryKey(input.readString());
        item.setPrimaryTag(input.readString());
        Map map = create(kryo, input, HashMap.class);
        int length = input.readInt(true);
        Class keyClass = String.class;
        Serializer keySerializer = new StringSerializer();

        kryo.reference(map);
        for (int i = 0; i < length; i++) {
            Object key;
            key = kryo.readObject(input, keyClass, keySerializer);
            Object value;
            value = kryo.readClassAndObject(input);
            map.put(key, value);
        }

        item.putAll(map);
        return item;
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public void write(Kryo kryo, Output output, MemCachedItem item) {
        output.writeString(item.getPrimaryKey());
        output.writeString(item.getPrimaryTag());
        int length = item.size();
        output.writeInt(length, true);
        Serializer keySerializer = new StringSerializer();
        for (Iterator iter = item.entrySet().iterator(); iter.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
            kryo.writeObjectOrNull(output, entry.getKey(), keySerializer);
            kryo.writeClassAndObject(output, entry.getValue());
        }

    }

}