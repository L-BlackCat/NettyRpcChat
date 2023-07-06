package org.example.serializer.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.example.serializer.Serializer;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisBase;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer extends Serializer {
    /**
     * 避免每次序列化后都要申请Buffer空间
     */
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * Java使用Class.newInstance()动态实例化类的实例。需要类中必须拥有一个默认构造器的限制。Objenesis通过绕开对象实例构造器来克服这个限制。
     * 典型使用：
     * 实例化一个对象而不调用构造器是一个特殊的任务，然而在一些特定的场合是有用的：
     *  1.序列化，远程调用和持久化 -对象需要实例化并存储为到一个特殊的状态，而没有调用代码。
     *  2.代理，AOP库和Mock对象 -类可以被子类继承而子类不用担心父类的构造器
     *  3.容器框架 -对象可以以非标准的方式被动态实例化。
     *
     *  性能和线程
     *  为了提高性能，最好能够尽可能地重用ObjectInstantiator对象。比如，如果你正在实例化一个类的多个实例，请通过同一个ObjectInstantiator进行。
     *
     *  InstantiatorStrategy和ObjectInstantiator两者都能够在多线程和并发情况下共享。它们是线程安全的。
     */
    private final Objenesis objenesis = new ObjenesisStd(true);

    private  <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if(Objects.isNull(schema)){
            schema = RuntimeSchema.getSchema(clazz);
            if(Objects.nonNull(schema)){
                schemaCache.put(clazz,schema);
            }
        }
        return schema;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try{
            data = ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = objenesis.newInstance(clazz);
        ProtostuffIOUtil.mergeFrom(data,obj,schema);
        return obj;
    }
}
