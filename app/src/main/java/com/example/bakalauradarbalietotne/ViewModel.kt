package com.example.bakalauradarbalietotne

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.pose.Pose

class PoseViewModel : ViewModel() {
    private var pose: Pose? = null
    var currentPose by mutableStateOf(pose)

    fun setCurrentPoseFun(pose: Pose) {
        currentPose = pose
    }
}

