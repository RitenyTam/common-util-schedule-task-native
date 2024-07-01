package com.riteny.entity

data class CronScheduleTaskViewEntity(
    val name: String,
    val cron: String,
    val isCancel: Boolean,
    val createdTime: Long
) {
    override fun toString(): String {
        return "CronScheduleTaskViewEntity(name='$name', cron='$cron', isCancel=$isCancel, createdTime=$createdTime)"
    }
}