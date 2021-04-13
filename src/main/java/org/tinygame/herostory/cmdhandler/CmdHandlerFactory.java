package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午11:44
 */
public final class CmdHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private static ConcurrentHashMap<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap =
            new ConcurrentHashMap<>();

    /**
     * 初始化 handlerMap
     */
    public static void init() {
        String packageName = CmdHandlerFactory.class.getPackage().getName();
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);
        for (Class<?> clazz : clazzSet) {
            // 抽象类跳过
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }

            // 获取类实例
            try {
                ICmdHandler<?> clazzInstance = (ICmdHandler<?>) clazz.newInstance();

                Class<?> msgType = null;
                for (Method currMethod : clazz.getDeclaredMethods()) {
                    if (!currMethod.getName().equals("handle")) {
                        continue;
                    }
                    Class<?>[] parameterTypes = currMethod.getParameterTypes();
                    if (parameterTypes.length < 2 || parameterTypes[1] == GeneratedMessageV3.class || !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
                        continue;
                    }
                    msgType = parameterTypes[1];
                    break;
                }
                _handlerMap.put(msgType, clazzInstance);
                LOGGER.info("{} ==== {}", clazzInstance, msgType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private CmdHandlerFactory() {
    }

    /**
     * 创建处理器
     *
     * @param msgClazz
     * @return
     */
    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (msgClazz == null) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
