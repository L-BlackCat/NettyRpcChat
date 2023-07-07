package org.example.utils;

import org.example.annotation.RpcService;

public enum LServiceKeyUtils {
    Instance;
    private final String SERVICE_CONCAT_CHAT = "#";

    public String makeServiceKey(RpcService rpcService){
        Class<?> value = rpcService.value();
        String version = rpcService.version();

       return makeServiceKey(value.getName(),version);
    }


    public String makeServiceKey(String className,String version ){
        String serviceKey = className;
        if(!LStringUtils.Instance.isNullOrEmpty(version)){
            serviceKey += SERVICE_CONCAT_CHAT.concat(version);
        }
        return serviceKey;
    }
}
