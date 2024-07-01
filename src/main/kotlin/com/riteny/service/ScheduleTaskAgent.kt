package com.riteny.service

import com.riteny.config.ScheduledTaskPool
import com.riteny.entity.CronScheduleTaskViewEntity
import com.riteny.entity.FixedDelayScheduleTaskViewEntity
import com.riteny.entity.FixedRateScheduleTaskViewEntity
import com.riteny.exception.ScheduleTaskException
import com.riteny.task.*
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit


class ScheduleTaskAgent {

    /**
     * 查詢所有cron表達式的定時任務視圖對象
     *
     * @return 定時任務視圖對象
     */
    fun searchCronScheduleTask(): ArrayList<CronScheduleTaskViewEntity> {

        val viewEntities = ArrayList<CronScheduleTaskViewEntity>()

        val scheduleTaskMap = ScheduledTaskPool.scheduleTaskMap.filter { (_, v) -> v is CronScheduleTask }

        scheduleTaskMap.forEach { (_, task) ->

            val cronTask = task as CronScheduleTask

            viewEntities.add(
                CronScheduleTaskViewEntity(
                    cronTask.name,
                    cronTask.cron,
                    cronTask.isCancel,
                    cronTask.createdTime.time
                )
            )
        }

        return viewEntities
    }

    /**
     * 查詢所有固定延遲執行的定時任務視圖對象
     *
     * @return 定時任務視圖對象
     */
    fun searchFixedDelayScheduleTask(): ArrayList<FixedDelayScheduleTaskViewEntity> {

        val viewEntities = ArrayList<FixedDelayScheduleTaskViewEntity>()

        val scheduleTaskMap = ScheduledTaskPool.scheduleTaskMap.filter { (_, v) -> v is FixedDelayScheduleTask }

        scheduleTaskMap.forEach { (_, task) ->

            val fixedDelayTask = task as FixedDelayScheduleTask

            viewEntities.add(
                FixedDelayScheduleTaskViewEntity(
                    fixedDelayTask.name,
                    fixedDelayTask.fixedRate,
                    fixedDelayTask.isCancel,
                    fixedDelayTask.createdTime.time
                )
            )
        }

        return viewEntities
    }

    /**
     * 查詢所有定時循環執行的定時任務視圖對象
     *
     * @return 定時任務視圖對象
     */
    fun searchFixedRateScheduleTask(): ArrayList<FixedRateScheduleTaskViewEntity> {

        val viewEntities = ArrayList<FixedRateScheduleTaskViewEntity>()

        val scheduleTaskMap = ScheduledTaskPool.scheduleTaskMap.filter { (_, v) -> v is FixedRateScheduleTask }

        scheduleTaskMap.forEach { (_, task) ->

            val fixedRateTask = task as FixedRateScheduleTask

            viewEntities.add(
                FixedRateScheduleTaskViewEntity(
                    fixedRateTask.name,
                    fixedRateTask.fixedRate,
                    fixedRateTask.isCancel,
                    fixedRateTask.createdTime.time
                )
            )
        }

        return viewEntities
    }

    /**
     * 提交一個根據cron表達式定時執行的任務
     *
     * @param name 任務名稱
     * @param cronExp cron 表達式
     * @param execute 定時任務執行的具體内容
     */
    fun addCronScheduleTask(name: String, cronExp: String, execute: () -> Unit) =
        CronScheduleTask(name, cronExp, ZonedDateTime.now(), execute).addCronScheduleTask()

    /**
     * 提交一個根據cron表達式定時執行的任務
     * 執行{fixedCount}次后，定時任務進入取消狀態
     *
     * @param name 任務名稱
     * @param cronExp cron 表達式
     * @param fixedCount 定時任務執行次數
     * @param execute 定時任務執行的具體内容
     */
    fun addCronScheduleTask(
        name: String, cronExp: String, fixedCount: Int, execute: () -> Unit
    ) = CronFixedCountScheduleTask(name, cronExp, fixedCount, ZonedDateTime.now(), execute).addCronScheduleTask()

    /**
     * 提交一個周期内循環執行的定時任務
     *
     * @param name 任務名稱
     * @param fixedRate 執行的周期
     * @param timeUnit 周期設定數字的時間單位
     * @param execute 定時任務執行的具體内容
     */
    fun addFixedRateScheduleTask(name: String, fixedRate: Long, timeUnit: TimeUnit, execute: () -> Unit) =
        FixedRateScheduleTask(name, fixedRate, timeUnit, execute).addFixedRateScheduleTask()

    /**
     * 提交一個周期内循環執行的定時任務
     * 執行{fixedCount}次后，定時任務進入取消狀態
     *
     * @param name 任務名稱
     * @param fixedRate 執行的周期
     * @param fixedCount 定時任務執行次數
     * @param timeUnit 周期設定數字的時間單位
     * @param execute 定時任務執行的具體内容
     */
    fun addFixedRateScheduleTask(
        name: String, fixedRate: Long, fixedCount: Int, timeUnit: TimeUnit, execute: () -> Unit
    ) = FixedRateFixedCountScheduleTask(name, fixedRate, fixedCount, timeUnit, execute).addFixedRateScheduleTask()

    /**
     * 提交一個周期内循環執行的定時任務
     * 該任務執行完成后的{fixedRate}時間后執行下次任務
     *
     * @param name 任務名稱
     * @param fixedRate 執行的周期
     * @param timeUnit 周期設定數字的時間單位
     * @param execute 定時任務執行的具體内容
     */
    fun addFixedDelayScheduleTask(name: String, fixedRate: Long, timeUnit: TimeUnit, execute: () -> Unit) =
        FixedDelayScheduleTask(name, fixedRate, timeUnit, execute).addFixedDelayScheduleTask()

    /**
     * 提交一個周期内循環執行的定時任務
     * 執行{fixedCount}次后，定時任務進入取消狀態
     *
     * @param name 任務名稱
     * @param fixedRate 執行的周期
     * @param fixedCount 定時任務執行次數
     * @param timeUnit 周期設定數字的時間單位
     * @param execute 定時任務執行的具體内容
     */
    fun addFixedDelayScheduleTask(
        name: String, fixedRate: Long, fixedCount: Int, timeUnit: TimeUnit, execute: () -> Unit
    ) =
        FixedDelayFixedCountScheduleTask(name, fixedRate, fixedCount, timeUnit, execute).addFixedDelayScheduleTask()

    /**
     * 取消任務，任務取消后，留存在任務列表中
     *
     * @param name 任務名稱
     */
    fun cancelTask(name: String) {
        val scheduleTask = ScheduledTaskPool.scheduleTaskMap[name]
            ?: throw ScheduleTaskException("Task [$name] not found .")
        scheduleTask.cancel()
    }

    /**
     * 刪除任務
     * 該任務必須處於已經取消的狀態
     *
     * @param name 任務名稱
     */
    fun removeTask(name: String) {
        val scheduleTask = ScheduledTaskPool.scheduleTaskMap[name]
            ?: throw ScheduleTaskException("Task [$name] not found .")
        scheduleTask.remove()
    }

    /**
     * 按照之前的配置參數重新啓動任務
     * 該任務必須還留存在任務列表
     *
     * @param name 任務名稱
     */
    fun restartTask(name: String) {
        val task = ScheduledTaskPool.scheduleTaskMap[name] ?: throw ScheduleTaskException("Task [$name] not found .")
        task.restartScheduleTask()
    }
}

fun scheduleTaskAgent(block: ScheduleTaskAgent.() -> Unit): ScheduleTaskAgent = ScheduleTaskAgent().apply(block)