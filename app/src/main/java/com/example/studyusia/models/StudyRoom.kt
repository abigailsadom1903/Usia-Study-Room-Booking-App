package com.example.studyusia.models

data class StudyRoom(
    val roomId: String,
    val roomName: String,
    val capacity: Int,
    val location: String,
    val facilities: String? = null,
    val details: String? = null,
    val imagePath: String? = null,
    val availableStartDate: String = "",
    val availableEndDate: String = "",
    val availableStartTime: String = "08:00",
    val availableEndTime: String = "22:00"
)
