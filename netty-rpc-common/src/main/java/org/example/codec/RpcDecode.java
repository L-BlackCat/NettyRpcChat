package org.example.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.example.logger.Debug;
import org.example.serializer.Serializer;

import java.util.List;

public class RpcDecode extends ReplayingDecoder {

    private Class<?> genericClass;
    private Serializer serializer;


    public RpcDecode(Class<?> genericClass, Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            Debug.err("数据长度不符合规定,不进行接受");
            return;
        }
        //  标记current_read_index,可以使用resetReadIndex，重新将read_index设置成标记current_read_index
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = null;
        try{
            obj = serializer.deserialize(data,genericClass);
            out.add(obj);
        }catch (Exception e){
            Debug.err("decode err,",e);
        }

    }

}
