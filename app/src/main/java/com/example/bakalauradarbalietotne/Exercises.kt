package com.example.bakalauradarbalietotne

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Exercises {

    fun getExerciseByID(exerciseID: String): Exercise? {
        when (exerciseID) {
            "squat" -> return exerciseSquat
            "pushup" -> return exercisePushUp
            "plank" -> return exercisePlank
        }
        return null
    }

    val exercisePushUp: Exercise = Exercise(
        title = "Atspiešanās",
        currentRecord = 0,
        timeCounter = false,
        exerciseSteps = R.string.steps_pushup,
        painterID = R.drawable.image_stickman_pushups
    )

    val exerciseSquat: Exercise = Exercise(
        title = "Pietupieni",
        currentRecord = 0,
        timeCounter = false,
        exerciseSteps = R.string.steps_squats,
        painterID = R.drawable.image_stickman_squats
    )

    val exercisePlank: Exercise = Exercise(
        title = "Planka",
        currentRecord = 0,
        timeCounter = true,
        exerciseSteps = R.string.steps_plank,
        painterID = R.drawable.image_stickman_plank
    )
}