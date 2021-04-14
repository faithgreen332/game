package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午11:23
 */
public class UserMoveToHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }
        MoveState mvState = UserManager.getUserById(userId).moveState;
        mvState.fromPosX = cmd.getMoveFromPosX();
        mvState.fromPoxY = cmd.getMoveFromPosY();
        mvState.toPosX = cmd.getMoveToPosX();
        mvState.toPosY = cmd.getMoveToPosY();
        mvState.startTime = System.currentTimeMillis();

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(mvState.fromPosX);
        resultBuilder.setMoveFromPosY(mvState.fromPoxY);
        resultBuilder.setMoveStartTime(mvState.startTime);
        resultBuilder.setMoveToPosX(mvState.toPosX);
        resultBuilder.setMoveToPosY(mvState.toPosY);

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();

        Broadcaster.broadcast(newResult);
    }
}
