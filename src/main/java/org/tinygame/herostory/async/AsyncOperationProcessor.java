package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void process(Runnable r) {
        if (r == null) {
            return;
        }
        es.submit(r);
    }
}
