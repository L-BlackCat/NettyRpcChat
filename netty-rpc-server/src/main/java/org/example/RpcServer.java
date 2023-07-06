package org.example;

import org.example.register.RpcServerRegistry;

public class RpcServer {

    public RpcServer(String serverAddress,String registerAddress) {
        /**
         * 服务器启动：
         * 1.连接zookeeper服务器
         * 2.收集服务集合，封装成对象，序列化后发送到zookeeper服务器中
         * 3.创建并启动netty服务器
         */
        RpcServerRegistry serverRegister = new RpcServerRegistry(registerAddress);
    }
}