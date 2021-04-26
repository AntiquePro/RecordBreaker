package com.example.bakalauradarbalietotne


import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource


data class Exercise(
    val title: String,
    val currentRecord: Int? = 0,
    val timeCounter: Boolean = false,
    val video: String? = "" ,
    val exerciseSteps: String = "",
    val painterID: Int?
) {

    companion object {
        val exercisePushUp: Exercise = Exercise(
            title = "Atspiešanās",
            currentRecord = null,
            timeCounter = false,
            video = "",
            exerciseSteps = R.string.steps_pushup.toString(),
            painterID = R.drawable.image_stickman_pushups
        )

        val exerciseSquat: Exercise = Exercise(
            title = "Pietupieni",
            currentRecord = 30,
            timeCounter = false,
            video = "",
            exerciseSteps = "",
            painterID = R.drawable.image_stickman_squats
        )

        val exercisePlank: Exercise = Exercise(
            title = "Planka",
            currentRecord = 50,
            timeCounter = true,
            video = "",
            exerciseSteps = "",
            painterID = R.drawable.image_stickman_plank
        )
    }
}


