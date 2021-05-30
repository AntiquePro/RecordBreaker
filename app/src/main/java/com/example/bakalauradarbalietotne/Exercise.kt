package com.example.bakalauradarbalietotne

data class Exercise(
    val title: String,
    var currentRecord: Int = 0,
    val timeCounter: Boolean = false,
    val exerciseSteps: Int?,
    val painterID: Int?
)
