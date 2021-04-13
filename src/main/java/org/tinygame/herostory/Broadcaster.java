package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午10:53
 */
public final class Broadcaster {

    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster() {
    }

    /**
     * 添加信道
     *
     * @param channel
     */
    public static void addChannel(Channel channel) {
        _channelGroup.add(channel);
    }

    /**
     * 移除信道
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        _channelGroup.remove(channel);
    }

    /**
     * 广播
     *
     * @param msg
     */
    public static void broadcast(Object msg) {
        if (msg == null) {
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
}
