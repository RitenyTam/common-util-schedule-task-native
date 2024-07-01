package com.riteny.entity

import java.util.*

data class FixedDelayScheduleTaskViewEntity(
    val name: String,
    val delayTime: Long,
    val isCancel: Boolean,
    val createdTime: Long
) {
    override fun toString(): String {
        return "FixedDelayScheduleTaskViewEntity(name='$name', delayTime='$delayTime', isCancel=$isCancel, createdTime=$createdTime)"
    }
}