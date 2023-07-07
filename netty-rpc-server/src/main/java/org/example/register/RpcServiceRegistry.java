package org.example.register;

import org.example.config.GlobalZKConfig;
import org.example.logger.Debug;
import org.example.service.RpcServiceBean;
import org.example.service.RpcProtocol;
import org.example.utils.LStringUtils;
import org.example.zookeeper.CuratorClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RpcServiceRegistry {
    CuratorClient client;
    List<String> pathList = new ArrayList<>();

    public RpcServiceRegistry(String registryAddress) {
        client = new CuratorClient(registryAddress);
    }


    public void registerService(String ip,int port,Map<String,Object> serviceMap) {
        Debug.info("rpc service register start...");
        List<RpcServiceBean> serviceBeanList = new ArrayList<>();
        for (String serviceKey : serviceMap.keySet()) {
            String[] strArray = LStringUtils.Instance.strSplit(serviceKey, "#");
            if(strArray.length > 0){
                RpcServiceBean bean = new RpcServiceBean();
                bean.setServiceName(strArray[0]);
                String version = "";
                if(strArray.length == 2){
                    version = strArray[1];
                }
                bean.setVersion(version);
                Debug.info("Register new service: " + serviceKey);
                serviceBeanList.add(bean);
            }else {
                Debug.warn("can not get serviceName and version: " + serviceKey );
            }
        }

        try{
            RpcProtocol protocol = new RpcProtocol();
            protocol.setIp(ip);
            protocol.setPort(port);
            protocol.setServiceBeanList(serviceBeanList);

            byte[] bytes = protocol.objToBytes();
            String path = GlobalZKConfig.ZK_DATA_PATH + "-" + protocol.hashCode();
            pathList.add(path);
            client.createPath(path,bytes);
            Debug.info(String.format("rpc protocol register is success, ip:%s port:%d ",ip,port));
        } catch (Exception e) {
            Debug.err("rpc protocol register to zookeeper is err",e);
        }

        Debug.info("rpc service register end...");
    }

    public void unregisterService(){
        Debug.info("unregister service!");
        for (String path : pathList) {
            try{
                client.deletePath(path);
            } catch (Exception e) {
                Debug.err("delete path err,path:" + path,e);
            }
        }
        client.close();
    }
}
