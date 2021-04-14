package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 上午9:28
 */
public class GameMessageHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMessageHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }
        try {
            super.handlerRemoved(ctx);
            Broadcaster.removeChannel(ctx.channel());

            // 不拥护移除掉
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }
            UserManager.removeUser(userId);

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
            Broadcaster.broadcast(newResult);
        } catch (Exception e) {
            LOGGER.error("handlerRemoved error: ", e);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }
        LOGGER.info("收到客户端消息,msg= {}", msg);

        MainThreadProcessor.getInstance().process(ctx, (GeneratedMessageV3) msg);

    }


}
