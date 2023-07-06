package org.example.service;

import org.example.annotation.RpcService;

@RpcService(value = HelloService.class,version = "1.0")
public class RpcServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
