package org.example.serializer;

public abstract class Serializer {
    public abstract <T> byte[] serialize(T obj);

    public abstract <T> T deserialize(byte[] bytes,Class<T> clazz);
}
