package com.example.bakalauradarbalietotne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.composables.ExerciseInterface


class ExerciseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraPreview()
            Text("${DigitalSkeleton.currentPose}")

            DigitalSkeleton.currentPose?.let { currentPose ->
                DigitalSkeleton().DrawDigitalSkeleton(currentPose)
            }

            ExerciseInterface()

        }
    }
}




