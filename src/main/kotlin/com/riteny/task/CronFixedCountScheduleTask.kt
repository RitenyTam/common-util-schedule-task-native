package com.riteny.task

import java.time.ZonedDateTime
import java.util.*

class CronFixedCountScheduleTask(
    name: String,
    val fixedCount: Int,
    isCancel: Boolean,
    createdTime: Date,
    cron: String,
    preExecTime: ZonedDateTime,
    execute: () -> Unit
) : CronScheduleTask(name, isCancel, createdTime, cron, preExecTime, execute), Runnable {

    constructor(name: String, cron: String, fixedCount: Int, preExecTime: ZonedDateTime, execute: () -> Unit)
            : this(name, fixedCount, false, Date(), cron, preExecTime, execute)

    private var execCount: Int = 1

    /**
     * 先將具體的定時任務執行完成
     * 計算下次任務啓動距現在相差的時間
     * 根據相差的時間啓動新的一次延時執行任務
     */
    override fun run() {

        execute()
        execCount++

        if (execCount > fixedCount) {
            cancel()
        } else {
            schedule(this, timeToNextExecution())
        }
    }
}