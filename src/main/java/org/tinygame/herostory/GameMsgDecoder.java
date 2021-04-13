package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 上午11:28
 * 自定义的消息解码器，字节数组的前2个字节是消息长度，紧接着的2个字节是消息类型
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        try {
            // 粘包 netty 已经解决了
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();

            byteBuf.readShort(); // 占两个字节，读出消息长度
            short msgCode = byteBuf.readShort();// 占两个字节，读取消息编号

            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            assert msgBuilder != null;
            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);
            Message cmd = msgBuilder.build();

            if (null != cmd) {
                // 把修改号的东西又放回流水线，让接下来的 handler 继续走
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
