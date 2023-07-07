package org.example.server;

import org.example.RpcServer;

public class ServerBoot {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1:1314", "127.0.0.1:2081");
    }
}
