package org.tinygame.herostory.async;

/**
 * Author: ljf
 * CreatedAt: 2021/4/15 上午1:26
 * 异步操作接口
 */
public interface IAsyncOperation {

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 完成异步操作逻辑 java8 有默认实现
     */
    default void doFinish() {


    }
}
