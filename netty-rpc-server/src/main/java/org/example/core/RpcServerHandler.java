package org.example.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.protostuff.Rpc;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.example.codec.RpcRequest;
import org.example.codec.RpcResponse;
import org.example.logger.Debug;
import org.example.utils.LServiceKeyUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    ThreadPoolExecutor threadPoolExecutor;
    Map<String,Object> serviceMap;

    public RpcServerHandler(ThreadPoolExecutor threadPoolExecutor, Map<String, Object> serviceMap) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        /**
         * 接受客户端发送的请求，进行解析，目标类进行方法调用后方法结果，发送回客户端
         * 客户端根据接口类名和version可以找出唯一的方法，调用其对象，获得返回的值
         */
        Channel channel = ctx.channel();
        threadPoolExecutor.execute(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            try{
                response.setResult(handler(request));
                response.setErr("success");
            }catch (Exception e){
                Debug.err("do handler is err, requestId :" + request.getRequestId());
            }

            ChannelFuture channelFuture = channel.writeAndFlush(response);
            channelFuture.addListener(future -> {
                Debug.info("send response for request ，requestId : " + request.getRequestId() );
            });
        });

    }


    private Object handler(RpcRequest request)throws Exception{
        String className = request.getClassName();
        String version = request.getVersion();
        String serviceKey = LServiceKeyUtils.Instance.makeServiceKey(className, version);
        if(!serviceMap.containsKey(serviceKey)){
            Debug.info("cant not find service ,serviceKey:" + serviceKey);
            return null;
        }
        Object serviceBean = serviceMap.get(serviceKey);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //  使用jdk反射,执行相关方法
//        Method method = serviceClass.getMethod(methodName, parameterTypes);
//        method.setAccessible(true);
//        Object result = method.invoke(serviceBean, parameters);
//
//        return result;

        //  使用cglib reflect
        FastClass fastClass = FastClass.create(serviceClass);
//        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
//        return method.invoke(serviceBean,parameters);

        //  更高效的用法
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex,serviceBean,parameters);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel channel = ctx.channel();
        boolean isKill = false;
        String eventType = "";

        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    isKill = true;
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    isKill = true;
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    isKill = true;
                    break;
            }
        }


        Debug.warn(channel.remoteAddress() +  " 超时事件：" + eventType);
        if(isKill){
            ctx.close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Debug.err("Server caught exception:" + cause.getMessage());
        ctx.channel().close();

    }
}
