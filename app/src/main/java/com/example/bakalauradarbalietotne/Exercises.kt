package com.example.bakalauradarbalietotne

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
        video = null,
        exerciseSteps = R.string.steps_pushup,
        painterID = R.drawable.image_stickman_pushups
    )

    val exerciseSquat: Exercise = Exercise(
        title = "Pietupieni",
        currentRecord = 30,
        timeCounter = false,
        video = null,
        exerciseSteps = R.string.steps_squats,
        painterID = R.drawable.image_stickman_squats
    )

    val exercisePlank: Exercise = Exercise(
        title = "Planka",
        currentRecord = 50,
        timeCounter = true,
        video = null,
        exerciseSteps = R.string.steps_plank,
        painterID = R.drawable.image_stickman_plank
    )
}