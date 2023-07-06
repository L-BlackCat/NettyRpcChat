package org.example.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import org.example.codec.RpcRequest;
import org.example.codec.RpcResponse;
import org.example.serializer.Serializer;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.omg.CORBA.INTERNAL;

import java.io.ByteArrayOutputStream;
import java.util.Queue;

public class KryoSerializer extends Serializer {
    /**
     * 不支持增删，
     * 线程不安全
     */


    /**
     * 1. 参数详解：池构造函数参数：线程安全、软引用、最大容量 ：
     * public Pool(boolean threadSafe, boolean softReferences, final int maximumCapacity)
     *  threadSafe: 这个参数是制定是否需要再POOL内部同步，如果设置为true，则可以被多个线程并发访问。
     * softReferences： 这个参数是是否使用softReferences进行存储对象，如果设置为true，则Kryo 池将会使用 java.lang.ref.SoftReference 来存储对象。这允许池中的对象在 JVM 的内存压力大时被垃圾回收。
     */
    private Pool<Kryo> kryoPool = new Pool<Kryo>(true,false,8){

        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            DefaultInstantiatorStrategy strategy = (DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private static final int KRYO_OUTPUT_BUFFER_SIZE = 1024 * 1024;
    private static final int KRYO_OUTPUT_MAX_BUFFER_SIZE = Integer.MAX_VALUE;


    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = null;
        Output out = null;
        try{
            kryo = kryoPool.obtain();
            out = new Output(KRYO_OUTPUT_BUFFER_SIZE,KRYO_OUTPUT_MAX_BUFFER_SIZE);
            kryo.writeObject(out,obj);
            out.flush();
            return out.toBytes();
        }finally {
            kryoPool.free(kryo);
            if(out != null){
                out.close();
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = null;
        Input in = null;
        try{
            kryo = kryoPool.obtain();
            in = new Input(bytes);
            T result = kryo.readObject(in, clazz);
            return result;
        }finally {
            kryoPool.free(kryo);
            if(in != null){
                in.close();
            }
        }
    }
}
