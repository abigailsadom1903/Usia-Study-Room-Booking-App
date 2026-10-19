package com.example.studyusia.models

data class Booking(
    val bookingId: String,
    val userId: String,
    val roomId: String,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val status: String = "confirmed"
)