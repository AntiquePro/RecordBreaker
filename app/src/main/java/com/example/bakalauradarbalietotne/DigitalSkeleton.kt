package com.example.bakalauradarbalietotne


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class DigitalSkeleton () {

@Composable
fun drawDigitalSkeleton() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            start = Offset(x=canvasWidth, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = OrangeMain,
            strokeWidth = 5f
        )
    }
}

    fun getAllLandmarks(pose: Pose):MutableList<PoseLandmark> {
        return pose.allPoseLandmarks
    }
}