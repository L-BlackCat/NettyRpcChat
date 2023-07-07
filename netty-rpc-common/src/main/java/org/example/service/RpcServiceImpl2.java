package org.example.service;

import org.example.annotation.RpcService;

@RpcService(value = HelloService.class,version = "2.0")
public class RpcServiceImpl2 implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello2 " + name;
    }

    @Override
    public String hello() {
        return "Hello2";
    }

}
