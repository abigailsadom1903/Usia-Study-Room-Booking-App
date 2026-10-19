package com.example.studyusia.models

data class User(
    val userId: String,
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val createdAt: String? = null
)