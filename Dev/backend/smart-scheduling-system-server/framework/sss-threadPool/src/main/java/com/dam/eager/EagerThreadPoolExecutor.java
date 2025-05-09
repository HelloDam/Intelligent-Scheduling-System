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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 快速消费线程池
 */
public class EagerThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 使用AtomicInteger记录当前正在处理的任务数量，提供线程安全的计数操作。
     */
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    /**
     * 构造函数，接受线程池相关的配置参数，包括核心线程数、最大线程数、线程存活时间、时间单位、工作队列、线程工厂和拒绝策略。
     * 工作队列类型为自定义的EagerTaskQueue，用于实现特殊的任务入队逻辑。
     *
     * @param corePoolSize         核心线程数
     * @param maximumPoolSize      最大线程数
     * @param keepAliveTime        线程空闲后的存活时间
     * @param unit                 时间单位
     * @param workQueue            工作队列，类型为EagerTaskQueue
     * @param threadFactory        线程工厂，用于创建新线程
     * @param handler              拒绝策略，当线程池和队列无法接受新任务时的处理方式
     */
    public  EagerThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   EagerTaskQueue<Runnable> workQueue,
                                   ThreadFactory threadFactory,
                                   RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 创建一个EagerThreadPoolExecutor实例的便捷方法
     * 包括创建EagerTaskQueue并设置其与线程池的关联
     *
     * @param corePoolSize         核心线程数
     * @param maximumPoolSize      最大线程数
     * @param keepAliveTime        线程空闲后的存活时间
     * @param unit                 时间单位
     * @param queueCapacity        队列容量
     * @param threadFactory        线程工厂，用于创建新线程
     * @param handler              拒绝策略，当线程池和队列无法接受新任务时的处理方式
     * @return                     创建的EagerThreadPoolExecutor实例
     */
    public static EagerThreadPoolExecutor createEagerThreadPoolExecutor(int corePoolSize,
                                                                 int maximumPoolSize,
                                                                 long keepAliveTime,
                                                                 TimeUnit unit,
                                                                 int queueCapacity,
                                                                 ThreadFactory threadFactory,
                                                                 RejectedExecutionHandler handler) {
        EagerTaskQueue eagerTaskQueue = new EagerTaskQueue(queueCapacity);
        EagerThreadPoolExecutor eagerThreadPoolExecutor = new EagerThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, eagerTaskQueue, threadFactory, handler);
        eagerTaskQueue.setExecutor(eagerThreadPoolExecutor);
        return eagerThreadPoolExecutor;
    }

    /**
     * 获取当前正在处理的任务数量。
     *
     * @return 当前正在处理的任务数量
     */
    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    /**
     * 重写父类的afterExecute方法，当任务执行完成后，将正在执行的任务数量减一。
     * 这是ThreadPoolExecutor提供的钩子方法，用于在任务执行结束后进行清理或其他操作。
     *
     * @param r       执行完毕的任务
     * @param t       执行过程中抛出的异常（如果有的话）
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // 任务执行完成，将正在执行数量-1
        submittedTaskCount.decrementAndGet();
    }

    /**
     * 重写父类的execute方法，用于提交任务到线程池。
     * 在提交任务之前，先将正在执行的任务数量加一。若提交失败，根据具体情况尝试重新投递任务或使用拒绝策略。
     *
     * @param command 待提交的任务
     * @throws RejectedExecutionException 如果任务无法被接受，且无法重新投递到队列
     */
    @Override
    public void execute(Runnable command) {
//        System.out.println("使用快速消费线程池执行任务");

        // 将正在执行任务数量 + 1
        submittedTaskCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException ex) {
            // 任务被拒绝，间隔一定时间，将任务重新投递到队列
            EagerTaskQueue eagerTaskQueue = (EagerTaskQueue) super.getQueue();
            try {
                // 将任务重新投递到队列
                if (!eagerTaskQueue.retryOffer(command, 10, TimeUnit.MILLISECONDS)) {
                    // 队列已满，使用拒绝策略，并减少计数
                    submittedTaskCount.decrementAndGet();
                    throw new RejectedExecutionException("Queue capacity is full.", ex);
                }
            } catch (InterruptedException iex) {
                // 重试失败，将正在执行任务数量 - 1
                submittedTaskCount.decrementAndGet();
                throw new RejectedExecutionException(iex);
            }
        } catch (Exception ex) {
            // 执行失败，将正在执行任务数量 - 1
            submittedTaskCount.decrementAndGet();
            throw ex;
        }
    }
}
