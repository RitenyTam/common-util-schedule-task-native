package com.riteny.config

import com.riteny.task.ScheduleTask
import com.riteny.exception.ScheduleTaskException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy


object ScheduledTaskPool {

    val scheduleAgentService: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1, CallerRunsPolicy())
    val scheduleTaskMap: MutableMap<String, ScheduleTask> = ConcurrentHashMap()

    fun addTask(task: ScheduleTask) {

        if (scheduleTaskMap[task.name] != null) {
            throw ScheduleTaskException("Task [${task.name}] already exists]")
        }

        scheduleTaskMap[task.name] = task
    }
}