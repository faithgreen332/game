package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午11:26
 * 命令处理接口
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    /**
     * 处理命令
     *
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
