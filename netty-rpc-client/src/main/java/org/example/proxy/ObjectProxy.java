package org.example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ObjectProxy<T> implements InvocationHandler {
    private T target;

    public ObjectProxy(T target) {
        this.target = target;
    }

    public static <T> T createProxy(T target){
        return (T)Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new ObjectProxy<>(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行命令前");
        /**
         *  向远程服务器请求方法执行结果
         *  客户端根据类和类的方法进行远程调用服务器的方法，就像是调用本地的方法一样
         *  两点：
         *      1.已知类和类的方法
         *      2.已知远程服务器的地址
         *  问题：
         *      如果有多个服务器，分别部署不同的服务，客户端拿取到服务之后，如何能够得知每个服务类的接口信息呢?
         *      答：客户端需要再本地定义与服务端接口类相同的接口类，并通过RPC框架提供的代理对象进行远程调用。
         */
        System.out.println("执行命令后");
        return null;
    }
}
