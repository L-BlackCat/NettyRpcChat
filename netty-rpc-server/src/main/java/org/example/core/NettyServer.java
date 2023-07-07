package org.example.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.annotation.RpcService;
import org.example.codec.RpcDecoder;
import org.example.codec.RpcEncoder;
import org.example.codec.RpcRequest;
import org.example.codec.RpcResponse;
import org.example.logger.Debug;
import org.example.register.RpcServiceRegistry;
import org.example.serializer.Serializer;
import org.example.serializer.protostuff.ProtostuffSerializer;
import org.example.utils.LServiceKeyUtils;
import org.example.utils.LThreadUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyServer extends Server{
    String serverAddress;
    String ip;
    int port;
    Thread thread;
    RpcServiceRegistry serviceRegistry;

    Map<String,Object> serviceMap = new HashMap<>();

    ThreadPoolExecutor threadPoolExecutor = LThreadUtils.Instance.createThreadPool(this.getClass().getSimpleName(),
            4,16);

    public NettyServer(String serverAddress, String registerAddress) {
        this.serverAddress = serverAddress;
        String[] strArray = serverAddress.split(":");
        this.ip = strArray[0];
        this.port = Integer.parseInt(strArray[1]);
        this.serviceRegistry = new RpcServiceRegistry(registerAddress);
    }

    @Override
    public void start() {
        thread = new Thread(()->{
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try{
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup,workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG,128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new RpcChannelInitializer(threadPoolExecutor,serviceMap));

                ChannelFuture channelFuture = serverBootstrap.bind(ip, port).sync();

                if(serviceRegistry != null){
                    serviceRegistry.registerService(ip,port,serviceMap);
                }

                channelFuture.addListener(future -> {
                    if(future.isSuccess()){
                        Debug.info("netty服务器启动...");
                    }
                });

                channelFuture.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                Debug.err("启动 netty 服务器失败",e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                if(serviceRegistry != null){
                    serviceRegistry.unregisterService();
                }
            }
        });
        thread.start();
    }

    @Override
    public void stop() {
        if(thread != null && thread.isAlive()){
            thread.interrupt();
        }
    }

    public void addService(Object obj){
        if(obj.getClass().isAnnotationPresent(RpcService.class)){
            Debug.info("add service , name = " + obj.getClass().getSimpleName());
            RpcService rpcService = obj.getClass().getAnnotation(RpcService.class);
            String serviceKey = LServiceKeyUtils.Instance.makeServiceKey(rpcService);
            Debug.info("serviceKey : " + serviceKey);
            serviceMap.put(serviceKey,obj);
        }
    }

    public static void main(String[] args) {
        System.out.println(NettyServer.class.getSimpleName());
    }
}

