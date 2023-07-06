package org.example.serializer.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logger.Debug;
import org.example.serializer.Serializer;

import java.io.IOException;

public class JsonSerializer extends Serializer {
    ObjectMapper mapper = new ObjectMapper();


    @Override
    public byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            Debug.err(" json serialize is err",e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        try {
            return mapper.readValue(bytes,clazz);
        } catch (IOException e) {
            Debug.err("json deserialize is err",e);
        }
        return null;
    }
}
