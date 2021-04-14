package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Author: ljf
 * CreatedAt: 2021/4/14 上午9:17
 */
public class UserAttackHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }
        // 获取攻击者 id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attkUserId == null) {
            return;
        }

        // 获取被攻击者 id
        int targetUserId = cmd.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        // 获取被攻击者
        User targetUser = UserManager.getUserById(targetUserId);
        if (null == targetUser) {
            return;
        }

        int subtractHp = 10;
        targetUser.currHp = targetUser.currHp - subtractHp;

        // 广播留学消息
        broadcastSubtractHp(targetUserId, subtractHp);
        if (targetUser.currHp <= 0) {
            // 广播死亡消息
            broadcastDie(targetUserId);
        }
    }

    private void broadcastDie(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播流血信息
     *
     * @param targetUserId 流血者
     * @param subtractHp   流血量
     */
    private void broadcastSubtractHp(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setSubtractHp(subtractHp);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
