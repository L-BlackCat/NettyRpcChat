package org.example.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.codec.RpcDecoder;
import org.example.codec.RpcEncoder;
import org.example.codec.RpcRequest;
import org.example.codec.RpcResponse;
import org.example.serializer.Serializer;
import org.example.serializer.protostuff.ProtostuffSerializer;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcChannelInitializer extends ChannelInitializer<SocketChannel> {
    ThreadPoolExecutor threadPoolExecutor;
    Map<String,Object> serviceMap;

    public RpcChannelInitializer(ThreadPoolExecutor threadPoolExecutor, Map<String, Object> serviceMap) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.serviceMap = serviceMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        Serializer serializer = new ProtostuffSerializer();
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
        pipeline.addLast(new RpcServerLengthFieldSpliter());
        pipeline.addLast(new RpcDecoder(RpcRequest.class,serializer));
        pipeline.addLast(new RpcEncoder(RpcResponse.class,serializer));
        pipeline.addLast(new RpcServerHandler(threadPoolExecutor,serviceMap));
    }
}
