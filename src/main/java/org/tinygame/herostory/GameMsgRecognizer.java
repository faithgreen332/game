package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午9:59
 */
public final class GameMsgRecognizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    private GameMsgRecognizer() {
    }

    public static ConcurrentHashMap<Integer, GeneratedMessageV3> _msgClazzAndMsgCodeMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Class<?>, Integer> _msgCodeAndClazzMap = new ConcurrentHashMap<>();

    /**
     * 初始化 msg 构建器 map
     */
    public static void init() {
        LOGGER.info("---------- 完成消息类与消息号的映射 ---------");

        Class<?>[] innerClass = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> clz : innerClass) {
            if (!(GeneratedMessageV3.class.isAssignableFrom(clz))) {
                continue;
            }

            String clzSimpleName = clz.getSimpleName().toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String msgCodeName = msgCode.name().replace("_", "").toLowerCase();

                if (!msgCodeName.startsWith(clzSimpleName)) {
                    continue;
                }

                int number = msgCode.getNumber();
                try {
                    Object obj = clz.getDeclaredMethod("getDefaultInstance").invoke(clz);
                    _msgCodeAndClazzMap.put(clz, number);
                    _msgClazzAndMsgCodeMap.put(number, (GeneratedMessageV3) obj);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 根据消息编号获取消息构建器
     *
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 msg = _msgClazzAndMsgCodeMap.get(msgCode);
        if (msg == null) {
            return null;
        }
        return msg.newBuilderForType();
    }

    public static int getMsgCodeByClazz(Class<?> aClass) {

        if (aClass == null) {
            return -1;
        }
        Integer msgCode = _msgCodeAndClazzMap.get(aClass);
        if (msgCode == null) {
            return -1;
        }
        return msgCode.intValue();
    }
}