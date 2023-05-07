package com.example.staffdashboard.messages

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long,
)
