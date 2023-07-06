package org.example.register;

import com.netty.rpc.zookeeper.CuratorClient;

public class RpcServerRegistry {

    public RpcServerRegistry(String registryAddress) {
        CuratorClient client = new CuratorClient(registryAddress);
    }
}
