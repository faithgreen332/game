package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: ljf
 * CreatedAt: 2021/4/15 上午12:03
 */
public final class MainThreadProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    private static final MainThreadProcessor mainThreadProcessor = new MainThreadProcessor();

    private MainThreadProcessor() {

    }

    private final ExecutorService es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    public static MainThreadProcessor getInstance() {
        return mainThreadProcessor;
    }

    /**
     * 处理客户端消息
     *
     * @param ctx
     * @param msg
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (ctx == null || msg == null) {
            return;
        }

        Class<? extends GeneratedMessageV3> msgClass = msg.getClass();
        LOGGER.info("收到客户端消息,msgClass = {}", msgClass.getName());
        es.submit(() -> {
            try {
                ICmdHandler<?> cmdHandler = CmdHandlerFactory.create(msgClass);
                if (null == cmdHandler) {
                    LOGGER.error("未找到对应的指令处理器，msgClass = {}", msgClass.getName());
                    return;
                }
                cmdHandler.handle(ctx, cast(msg));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

    }

    /**
     * 用一个方法欺骗一下编译器
     *
     * @param msg
     * @param <TCmd>
     * @return
     */
    private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (msg == null) {
            return null;
        }
        return (TCmd) msg;
    }
}

