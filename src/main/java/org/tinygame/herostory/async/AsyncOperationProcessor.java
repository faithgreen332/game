package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: ljf
 * CreatedAt: 2021/4/15 上午12:51
 */
public final class AsyncOperationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    private static final AsyncOperationProcessor instance = new AsyncOperationProcessor();

    private AsyncOperationProcessor() {
    }

    public static AsyncOperationProcessor getInstance() {
        return instance;
    }

    private final ExecutorService es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread thread = new Thread(newRunnable);
        thread.setName("AsyncOperationProcessor");
        return thread;
    });

    public void process(IAsyncOperation op) {
        if (op == null) {
            return;
        }
        es.submit(() -> {
            // 执行异步操作
            op.doAsync();
            // 执行完成后,回到主线程的逻辑
            MainThreadProcessor.getInstance().process(op::doFinish);
        });
    }
}
