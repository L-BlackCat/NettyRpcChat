package org.example.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * LFH  不使用也并不会粘包和拆包
 */
public class RpcServerLengthFieldSpliter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 14;
    private static final int LENGTH_FIELD_LENGTH = 4;
    public RpcServerLengthFieldSpliter(){
        /**
         *
         * @param maxFrameLength  帧的最大长度
         * @param lengthFieldOffset length字段偏移的地址
         * @param lengthFieldLength length字段所占的字节长
         * @param lengthAdjustment 修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
         * @param initialBytesToStrip 解析时候跳过多少个长度
         * @param failFast 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异
         */
        super(Integer.MAX_VALUE,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,0,0,false);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 屏蔽非本协议的客户端
        /**
         * netty的自定义包逐个解析的方式确实不会发生粘包和拆包的问题，但是LengthFieldBasedFrameDecoder的使用是可以
         * 提高的可靠性和效率。
         * <p>
         * LengthFieldBasedFrameDecoder的使用可以解决在网络传输中数据帧的长度字段被攻击者篡改的情况，从而导致恶意攻击或者数据损坏。
         * 这个解码器会根据帧的长度字段来检查帧是否完整，如果发现帧长度错误，它会将该帧舍弃，从而保证后续解码的正确性。
         * <p>
         * LengthFieldBasedFrameDecoder可以在解析数据帧时自动进行帧边界识别，从而避免了手动处理数据帧时需要逐个字节地进行判断和分析，提高了处理效率和准确性。
         * <p>
         * LengthFieldBasedFrameDecoder的使用可以提高网络传输的可靠性和效率，尤其是在处理一些关键数据和应用场景时更为必要。
         */
        if (in.readableBytes() < 4) {
            ctx.channel().close();
            return null;
        }

        return super.decode(ctx, in);
    }
}
