package com.riteny.task

import com.riteny.config.ScheduledTaskPool
import com.riteny.exception.ScheduleTaskException
import java.util.*
import java.util.concurrent.TimeUnit

open class FixedDelayScheduleTask(
    name: String, isCancel: Boolean, createdTime: Date, val fixedRate: Long,
    val timeUnit: TimeUnit, val execute: () -> Unit
) : ScheduleTask(name, isCancel, createdTime), Runnable {

    constructor(name: String, fixedRate: Long, timeUnit: TimeUnit, execute: () -> Unit)
            : this(name, false, Date(), fixedRate, timeUnit, execute)

    override fun run() = execute()

    override fun restartScheduleTask() {
        ScheduledTaskPool.scheduleTaskMap[name] ?: throw ScheduleTaskException("Task [$name] not found .")
        ScheduledTaskPool.scheduleTaskMap.remove(name)
        scheduleWithFixedDelay(this, fixedRate, timeUnit)
    }

    /**
     * 將當前任務添加到任務調度列表
     */
    fun addFixedDelayScheduleTask() = scheduleWithFixedDelay(this, fixedRate, timeUnit)
}