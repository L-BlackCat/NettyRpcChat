package org.example.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.example.config.GlobalZKConfig;

import java.util.List;

public class CuratorClient {

    CuratorFramework client;
    /**
     * Curator包含几个包：
     *  curator-framework：对zookeeper的底层api的一些封装
     *  curator-client：提供一些客户端的操作，例如重试策略等
     *  curator-recipes：封装了一些高级特性，如：Cache事件监听、选举、分布式锁、分布式计数器、分布式Barrier等
     *
     */

    public CuratorClient(String registerAddress){
        client = CuratorFrameworkFactory.builder().connectString(registerAddress)
                .sessionTimeoutMs(GlobalZKConfig.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(GlobalZKConfig.ZK_CONNECTION_TIMEOUT)
                .namespace(GlobalZKConfig.ZK_NAMESPACE) //用来实现不同的zookeeper业务之间的隔离,指定了独立空间/netty-rpc-chat，之后的访问都是基于该目录进行
                .retryPolicy(new ExponentialBackoffRetry(1000,5))
                .build();
        client.start();
    }


    public CuratorFramework getClient() {
        return client;
    }

    public void createPath(String path,byte[] bytes) throws Exception {
        client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,bytes);
    }

    public void updateData(String path,byte[] bytes) throws Exception {
        client.setData().forPath(path,bytes);
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public void close(){
        client.close();
    }
}
