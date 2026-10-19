package com.example.studyusia.models

data class Feedback(
    val feedbackId: String,
    val userId: String,
    val roomId: String,
    val rating: Int,
    val comment: String? = null,
    val createdAt: String? = null,
    val userName: String? = null
)