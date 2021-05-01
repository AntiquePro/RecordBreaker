package com.example.bakalauradarbalietotne

import android.content.res.Resources

data class Exercise(
    val title: String,
    val currentRecord: Int? = 0,
    val timeCounter: Boolean = false,
    val video: String? = "",
    val exerciseSteps: Int,
    val painterID: Int?
)
