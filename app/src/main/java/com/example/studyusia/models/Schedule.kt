package com.example.studyusia.models

data class Schedule(
    val scheduleId: String,
    val roomId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val isAvailable: Boolean = true
)