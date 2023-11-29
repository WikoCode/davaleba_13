package com.example.myapplication

data class Fields(
    val fieldId: Int,
    val hint: String,
    val fieldType: String,
    val keyboard: String?,
    val required: Boolean,
    val isActive: Boolean,
    val icon: String,
    var value: String = ""
)
