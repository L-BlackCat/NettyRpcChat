package org.example;

import org.example.core.RpcServiceDiscovery;
import org.example.zookeeper.CuratorClient;

public class RpcClient {
    RpcServiceDiscovery serviceDiscovery;

    public RpcClient(String registerAddress) {
        serviceDiscovery = new RpcServiceDiscovery(registerAddress);
    }


}