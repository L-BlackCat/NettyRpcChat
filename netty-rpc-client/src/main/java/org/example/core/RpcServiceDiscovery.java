package org.example.core;

import org.example.config.GlobalZKConfig;
import org.example.service.RpcProtocol;
import org.example.utils.LJsonUtils;
import org.example.zookeeper.CuratorClient;

import java.util.List;

public class RpcServiceDiscovery {
    CuratorClient client;

    public RpcServiceDiscovery(String registerAddress){
        this.client = new CuratorClient(registerAddress);
    }

    public void discoveryService() throws Exception {
        List<String> nodeList = client.getChildren(GlobalZKConfig.ZK_REGISTRY_PATH);
        for (String node : nodeList) {
            byte[] data = client.getData(GlobalZKConfig.ZK_REGISTRY_PATH + "/" + node);
            RpcProtocol rpcProtocol = RpcProtocol.fromJson(data);

        }

    }

}
