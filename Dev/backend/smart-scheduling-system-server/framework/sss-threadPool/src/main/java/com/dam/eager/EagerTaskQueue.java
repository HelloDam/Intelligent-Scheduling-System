/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dam.eager;

import lombok.Data;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 快速消费任务队列
 */
@Data
public class EagerTaskQueue<R extends Runnable> extends LinkedBlockingQueue<Runnable> {

    private EagerThreadPoolExecutor executor;

    /**
     * 构造函数，传入队列容量参数，用于初始化LinkedBlockingQueue。
     *
     * @param capacity 队列的最大容量
     */
    public EagerTaskQueue(int capacity) {
        super(capacity);
    }

    /**
     * 重写父类LinkedBlockingQueue的offer方法，实现自定义的任务入队逻辑
     * 当没有到达最大线程时，返回false，让其创建非核心线程
     *
     * @param runnable 待添加的任务对象
     * @return 如果任务成功加入队列或触发线程池创建非核心线程，则返回true；否则返回false
     */
    @Override
    public boolean offer(Runnable runnable) {
        // 获取当前线程池的线程数量
        int currentPoolThreadSize = executor.getPoolSize();

        // 检查是否有核心线程处于空闲状态（已提交任务数小于当前线程数）
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
            // 如果有核心线程正在空闲，将任务加入阻塞队列，由核心线程进行处理任务
            return super.offer(runnable);
        }

        // 检查当前线程池线程数量是否小于最大线程数
        if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
//            System.out.println("线程池线程数量小于最大线程数，返回 False，线程池会创建非核心线程");
            // 当前线程池线程数量小于最大线程数，返回false，触发线程池创建非核心线程处理任务
            return false;
        }
        // 如果当前线程池数量大于最大线程数，任务加入阻塞队列，等待线程池中的已有线程处理
        return super.offer(runnable);
    }

    /**
     *
     * @param runnable      待添加的任务对象
     * @param timeout       等待加入队列的超时时间
     * @param timeUnit      超时时间单位
     * @return              如果任务成功加入队列或触发线程池创建非核心线程，则返回true；否则返回false
     * @throws InterruptedException 如果在等待过程中线程被中断
     * @throws RejectedExecutionException 如果线程池已关闭
     */
    public boolean retryOffer(Runnable runnable, long timeout, TimeUnit timeUnit) throws InterruptedException {
        // 如果线程池已关闭，则抛出RejectedExecutionException异常。
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor is shutdown!");
        }
        return super.offer(runnable, timeout, timeUnit);
    }
}
