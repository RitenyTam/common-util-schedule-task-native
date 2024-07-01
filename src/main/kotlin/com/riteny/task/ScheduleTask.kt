package com.riteny.task

import com.riteny.config.ScheduledTaskPool
import com.riteny.config.ScheduledTaskPool.scheduleAgentService
import com.riteny.exception.ScheduleTaskException
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class ScheduleTask(
    val name: String,
    var isCancel: Boolean,
    val createdTime: Date,
    var scheduledFuture: ScheduledFuture<*>? = null
) {

    /**
     * 在{delay}毫秒之後執行任務{task}
     *
     * @param task 定时任务
     * @param delay 延迟多少毫秒执行
     */
    protected fun schedule(task: Runnable, delay: Long) {
        scheduleAgentService.corePoolSize += 1
        scheduledFuture = scheduleAgentService.schedule(task, delay, TimeUnit.MILLISECONDS)
        ScheduledTaskPool.addTask(this)
    }

    /**
     * 每隔{delay} {timeUnit} 執行一次定時任務{task}
     *
     * @param task 定时任务
     * @param delay 延迟多少（timeUnit）时间单位执行
     * @param timeUnit 时间单位
     */
    protected fun scheduleAtFixedRate(task: Runnable, delay: Long, timeUnit: TimeUnit) {
        scheduleAgentService.corePoolSize += 1
        scheduledFuture = scheduleAgentService.scheduleAtFixedRate(task, 0, delay, timeUnit)
        ScheduledTaskPool.addTask(this)
    }

    /**
     * 每隔{delay} {timeUnit} 執行一次定時任務{task}
     * 需要等待上一次任務執行完成后，再過{delay}時間后，才會開始第二次任務
     *
     * @param task 定时任务
     * @param delay 延迟多少（timeUnit）时间单位执行
     * @param timeUnit 时间单位
     */
    protected fun scheduleWithFixedDelay(task: Runnable, delay: Long, timeUnit: TimeUnit) {
        scheduleAgentService.corePoolSize += 1
        scheduledFuture = scheduleAgentService.scheduleWithFixedDelay(task, 0, delay, timeUnit)
        ScheduledTaskPool.addTask(this)
    }

    abstract fun restartScheduleTask()

    /**
     * 取消任務
     */
    fun cancel() {
        val scheduledFuture = scheduledFuture ?: throw ScheduleTaskException("Incorrect task $name configuration .")
        isCancel = scheduledFuture.cancel(false)
        if (isCancel) {
            scheduleAgentService.corePoolSize -= 1
        }
    }

    /**
     * 從任務列表中移除該任務
     * 任務需要在取消執行狀態
     */
    fun remove() {
        if (isCancel) {
            ScheduledTaskPool.scheduleTaskMap.remove(name)
        } else {
            throw throw ScheduleTaskException("Task $name is still active .")
        }
    }
}