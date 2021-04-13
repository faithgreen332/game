package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午8:25
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = GameMsgRecognizer.getMsgCodeByClazz(msg.getClass());
        if (msgCode <= -1) {
            LOGGER.error("无法识别的消息类型，msgClazz = ", msg.getClass().getSimpleName());
        }

        // 把消息写回去
        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeShort((short) 0); // 消息的长度，目前写出 0 只用来占位
        buffer.writeShort((short) msgCode); // 消息的编号
        buffer.writeBytes(msgBody); // 写消息体

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        super.write(ctx, frame, promise);
    }
}
