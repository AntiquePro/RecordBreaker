package com.example.bakalauradarbalietotne

import androidx.compose.ui.res.stringResource
import com.example.bakalauradarbalietotne.utils.ExerciseStrings

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
        currentRecord = null,
        timeCounter = false,
        video = "",
        exerciseSteps = R.string.steps_pushup,
        painterID = R.drawable.image_stickman_pushups
    )

    val exerciseSquat: Exercise = Exercise(
        title = "Pietupieni",
        currentRecord = 30,
        timeCounter = false,
        video = "",
        exerciseSteps = R.string.steps_squats,
        painterID = R.drawable.image_stickman_squats
    )

    val exercisePlank: Exercise = Exercise(
        title = "Planka",
        currentRecord = 50,
        timeCounter = true,
        video = "",
        exerciseSteps = R.string.steps_plank,
        painterID = R.drawable.image_stickman_plank
    )
}