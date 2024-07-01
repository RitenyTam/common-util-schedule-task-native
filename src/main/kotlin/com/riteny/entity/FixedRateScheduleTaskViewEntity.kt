package com.riteny.entity

data class FixedRateScheduleTaskViewEntity(
    val name: String,
    val delayTime: Long,
    val isCancel: Boolean,
    val createdTime: Long
) {
    override fun toString(): String {
        return "FixedRateScheduleTaskViewEntity(name='$name', delayTime='$delayTime', isCancel=$isCancel, createdTime=$createdTime)"
    }
}