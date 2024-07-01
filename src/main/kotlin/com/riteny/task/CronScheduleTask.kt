package com.riteny.task

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.riteny.config.ScheduledTaskPool
import com.riteny.exception.ScheduleTaskException
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

open class CronScheduleTask(
    name: String,
    isCancel: Boolean,
    createdTime: Date,
    val cron: String,
    private var preExecTime: ZonedDateTime,
    val execute: () -> Unit
) : ScheduleTask(name, isCancel, createdTime), Runnable {

    constructor(name: String, cron: String, preExecTime: ZonedDateTime, execute: () -> Unit)
            : this(name, false, Date(), cron, preExecTime, execute)

    /**
     * 先將具體的定時任務執行完成
     * 計算下次任務啓動距現在相差的時間
     * 根據相差的時間啓動新的一次延時執行任務
     */
    override fun run() {
        execute()
        schedule(this, timeToNextExecution())
    }

    override fun restartScheduleTask() {
        ScheduledTaskPool.scheduleTaskMap[name] ?: throw ScheduleTaskException("Task [$name] not found .")
        ScheduledTaskPool.scheduleTaskMap.remove(name)
        schedule(this, timeToNextExecution())
    }

    /**
     * 將當前任務添加到任務調度列表
     */
    fun addCronScheduleTask() = schedule(this, timeToNextExecution())

    /**
     * 根據設定的cron表達式，計算下一次任務啓動距今相差的時間
     */
    protected fun timeToNextExecution(): Long {

        val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
        val quartzCron = parser.parse(cron)
        val executionTime = ExecutionTime.forCron(quartzCron)

        val currentTime = preExecTime

        val nextExecution = executionTime.nextExecution(currentTime).get()

        preExecTime = nextExecution

        return ChronoUnit.MILLIS.between(currentTime, nextExecution)
    }
}