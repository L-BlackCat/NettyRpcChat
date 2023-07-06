package org.example.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.logger.Debug;
import org.example.serializer.Serializer;


public class RpcEncode extends MessageToByteEncoder {
    private Class<?> genericClass;
    private Serializer serializer;

    public RpcEncode(Class<?> genericClass, Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
        Debug.debug("开始进行编码----");
        //  obj是否能够强转成geneticClass
        if(genericClass.isInstance(obj)){
            try{
                byte[] data = serializer.serialize(obj);
                //  LFH 简化数据传输，正常来说，需要加上安全校验数据（魔数，序列化方式，版本等）
                out.writeInt(data.length);
                out.writeBytes(data);
            }catch (Exception ex){
                Debug.err("Encode err",ex);
            }
        }
    }
}
