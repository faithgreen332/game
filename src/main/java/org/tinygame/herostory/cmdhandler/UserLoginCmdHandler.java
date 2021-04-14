package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Author: ljf
 * CreatedAt: 2021/4/14 下午11:32
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (userName == null || password == null) {
            return;
        }

        // 获取用户实体

        // 第三个参数就是一个回调函数，即观察者模式
        LoginService.getInstance().userLogin(userName, password,(userEntity) -> {
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            if (null == userEntity) {
                resultBuilder.setUserId(-1);
                resultBuilder.setUserName("");
                resultBuilder.setHeroAvatar("");
            } else {

                // 将用户加入字典
                User newUser = new User();
                newUser.userId = userEntity.userId;
                newUser.userName = userEntity.userName;
                newUser.heroAvatar = userEntity.heroAvatar;
                newUser.currHp = 1000;

                UserManager.addUser(newUser);

                // 将用户 Id 附着到 Channel
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(userEntity.userId);
                resultBuilder.setUserId(userEntity.userId);
                resultBuilder.setUserName(userEntity.userName);
                resultBuilder.setHeroAvatar(userEntity.heroAvatar);
            }
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });
    }
}
