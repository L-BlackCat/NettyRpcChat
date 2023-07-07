package org.example;

import org.example.core.NettyServer;

public class RpcServer extends NettyServer {

    public RpcServer(String serverAddress,String registryAddress) {
        super(serverAddress,registryAddress);
    }

}