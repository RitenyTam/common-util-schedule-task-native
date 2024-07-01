package com.riteny.task

import java.util.*
import java.util.concurrent.TimeUnit

class FixedRateFixedCountScheduleTask(
    name: String,
    val fixedCount: Int,
    isCancel: Boolean,
    createdTime: Date,
    fixedRate: Long,
    timeUnit: TimeUnit,
    execute: () -> Unit
) : FixedRateScheduleTask(name, isCancel, createdTime, fixedRate, timeUnit, execute) {

    constructor(name: String, fixedRate: Long, fixedCount: Int, timeUnit: TimeUnit, execute: () -> Unit)
            : this(name, fixedCount, false, Date(), fixedRate, timeUnit, execute)

    private var execCount: Int = 1

    override fun run() {

        execute()
        execCount++

        if (execCount > fixedCount) {
            cancel()
        }
    }
}