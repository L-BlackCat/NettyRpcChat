package org.example.service;

import org.example.utils.LJsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RpcProtocol {
    private String ip;
    private int port;
    private List<RpcServiceBean> serviceBeanList = new ArrayList<>();


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RpcServiceBean> getServiceBeanList() {
        return serviceBeanList;
    }

    public void setServiceBeanList(List<RpcServiceBean> serviceBeanList) {
        this.serviceBeanList = serviceBeanList;
    }

    public void addServiceBean(RpcServiceBean bean){
        this.serviceBeanList.add(bean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip,port,this.serviceBeanList);
    }

    public String toJsonString(){
        return LJsonUtils.objectToJson(this);
    }

    public byte[] objToBytes(){
        return LJsonUtils.serialize(this);
    }

    public static RpcProtocol fromJson(byte[] data){
        return LJsonUtils.deserialize(data, RpcProtocol.class);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            RpcProtocol rpcProtocol = new RpcProtocol();
            List<RpcServiceBean> serviceList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                RpcServiceBean bean = new RpcServiceBean();
                bean.setServiceName("org.example.service.HelloService");
                bean.setVersion(j+ ".0");
                serviceList.add(bean);
            }
            rpcProtocol.setPort(1314);
            rpcProtocol.setIp("127.0.0.1");
            rpcProtocol.setServiceBeanList(serviceList);

            System.out.println(i + ":" + rpcProtocol.hashCode());
            System.out.println(rpcProtocol.hashCode());
        }
    }
}
