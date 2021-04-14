package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午11:22
 */
public class WhoElseIsHereHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (ctx == null) {
            return;
        }
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User currUser : UserManager.listUsers()) {
            if (null == currUser) {
                continue;
            }
            // 构建每一个用户信息
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.userId);
            userInfoBuilder.setHeroAvatar(currUser.heroAvatar);

            // 构建移动状态
            MoveState mvState = currUser.moveState;
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(mvState.fromPosX);
            mvStateBuilder.setFromPosY(mvState.fromPoxY);
            mvStateBuilder.setStartTime(mvState.startTime);
            mvStateBuilder.setToPosX(mvState.toPosX);
            mvStateBuilder.setToPosY(mvState.toPosY);

            // 将移动信息给用户信息
            userInfoBuilder.setMoveState(mvStateBuilder);

            // 将用户信息添加到结果信息
            resultBuilder.addUserInfo(userInfoBuilder);
        }
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
