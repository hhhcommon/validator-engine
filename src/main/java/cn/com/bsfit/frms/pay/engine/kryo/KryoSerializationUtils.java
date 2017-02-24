package cn.com.bsfit.frms.pay.engine.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.com.bsfit.frms.obj.MemCachedItem;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * This implementation uses the <a
 * href="http://code.google.com/p/kryo/">Kryo</a> serialization.
 */
public class KryoSerializationUtils {

    private static KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(MemCachedItem.class, new MemCachedItemSerializer());
            // configure kryo instance, customize settings
            return kryo;
        }
    };
    // Build pool with SoftReferences enabled (optional)
    private static KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public static byte[] serialize(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        Kryo kryo = null;
        try {
            kryo = pool.borrow();
            kryo.writeObject(output, o);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (output != null) {
                output.flush();
                output.close();
            }
            if (kryo != null) {
                pool.release(kryo);
            }
        }
        return baos.toByteArray();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object deserialize(byte[] bytes, Class clazz) {
        Input input = new Input(new ByteArrayInputStream(bytes));
        Kryo kryo = null;
        try {
            kryo = pool.borrow();
            Object o = kryo.readObject(input, clazz);
            return o;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (input != null) {
                input.close();
            }
            if (kryo != null) {
                pool.release(kryo);
            }
        }
        return null;
    }
}
