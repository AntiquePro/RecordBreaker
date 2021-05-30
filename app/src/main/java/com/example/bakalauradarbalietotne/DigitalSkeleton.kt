package com.example.bakalauradarbalietotne

import android.content.res.Configuration
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.properties.Delegates

class DigitalSkeleton {

    companion object {
        private var pose: Pose? = null
        var currentPose by mutableStateOf(pose)
        var currentCameraPreview by Delegates.notNull<PreviewView>()
        const val ML_KIT_IMAGE_DEFAULT_WIDTH = 480
        const val ML_KIT_IMAGE_DEFAULT_HEIGHT = 640
    }

    @Composable
    fun DrawDigitalSkeleton(pose: Pose) {
        val landmarks = pose.allPoseLandmarks

        if (landmarks.isEmpty()) {
            Log.d("PoseDetection", "No landmarks detected")
            return
        }

        val leftShoulderLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulderLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbowLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbowLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWristLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWristLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHipLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHipLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKneeLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKneeLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkleLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkleLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        val leftPinkyLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val rightPinkyLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val leftIndexLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val rightIndexLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val leftThumbLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val rightThumbLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val leftHeelLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHeelLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftFootIndexLandmark = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val rightFootIndexLandmark = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

        // Draw lines
        DrawCanvasLine(leftShoulderLandmark, rightShoulderLandmark)
        DrawCanvasLine(leftHipLandmark, rightHipLandmark)
        DrawCanvasLine(leftShoulderLandmark, leftElbowLandmark)
        DrawCanvasLine(leftElbowLandmark, leftWristLandmark)
        DrawCanvasLine(leftShoulderLandmark, leftHipLandmark)
        DrawCanvasLine(leftHipLandmark, leftKneeLandmark)
        DrawCanvasLine(leftKneeLandmark, leftAnkleLandmark)
        DrawCanvasLine(leftWristLandmark, leftThumbLandmark)
        DrawCanvasLine(leftWristLandmark, leftPinkyLandmark)
        DrawCanvasLine(leftWristLandmark, leftIndexLandmark)
        DrawCanvasLine(leftIndexLandmark, leftPinkyLandmark)
        DrawCanvasLine(leftAnkleLandmark, leftHeelLandmark)
        DrawCanvasLine(leftHeelLandmark, leftFootIndexLandmark)
        DrawCanvasLine(rightShoulderLandmark, rightElbowLandmark)
        DrawCanvasLine(rightElbowLandmark, rightWristLandmark)
        DrawCanvasLine(rightShoulderLandmark, rightHipLandmark)
        DrawCanvasLine(rightHipLandmark, rightKneeLandmark)
        DrawCanvasLine(rightKneeLandmark, rightAnkleLandmark)
        DrawCanvasLine(rightWristLandmark, rightThumbLandmark)
        DrawCanvasLine(rightWristLandmark, rightPinkyLandmark)
        DrawCanvasLine(rightWristLandmark, rightIndexLandmark)
        DrawCanvasLine(rightIndexLandmark, rightPinkyLandmark)
        DrawCanvasLine(rightAnkleLandmark, rightHeelLandmark)
        DrawCanvasLine(rightHeelLandmark, rightFootIndexLandmark)

        // Draw points
        for (landmark in landmarks) {
            DrawCanvasPoint(landmark)
        }
    }

    @Composable
    private fun DrawCanvasPoint(landmark: PoseLandmark) {
        val point = landmark.position
        val configuration = LocalConfiguration.current
        Canvas(
            Modifier
                .fillMaxSize()
                .scale(
                    scaleX = adjustScaleX(checkIfDeviceIsRotated(configuration)),
                    scaleY = adjustScaleY(checkIfDeviceIsRotated(configuration))
                )
        ) {
            drawCircle(
                color = OrangeMain,
                center = adjustCoordinates(point.x, point.y, checkIfDeviceIsRotated(configuration)),
                radius = 8f
            )
        }
    }

    @Composable
    private fun DrawCanvasLine(startLandmark: PoseLandmark, endLandmark: PoseLandmark) {
        val startPoint = startLandmark.position
        val endPoint = endLandmark.position
        val configuration = LocalConfiguration.current
        Canvas(
            Modifier.fillMaxSize()
                .scale(
                    scaleX = adjustScaleX(checkIfDeviceIsRotated(configuration)),
                    scaleY = adjustScaleY(checkIfDeviceIsRotated(configuration))
                )
        ) {
            drawLine(
                color = Color.White,
                start = adjustCoordinates(
                    startPoint.x,
                    startPoint.y,
                    checkIfDeviceIsRotated(configuration)
                ),
                end = adjustCoordinates(
                    endPoint.x,
                    endPoint.y,
                    checkIfDeviceIsRotated(configuration)
                ),
                strokeWidth = 8f
            )
        }
    }

    private fun adjustCoordinates(x: Float, y: Float, isRotated: Boolean): Offset {
        // If device is not rotated (portrait mode)
        return if (!isRotated) {
            Offset(
                x * currentCameraPreview.width / ML_KIT_IMAGE_DEFAULT_WIDTH,
                y * currentCameraPreview.height / ML_KIT_IMAGE_DEFAULT_HEIGHT
            )
        }
        // If device is rotated (landscape mode)
        else {
            Offset(
                x * currentCameraPreview.width / ML_KIT_IMAGE_DEFAULT_HEIGHT,
                y * currentCameraPreview.height / ML_KIT_IMAGE_DEFAULT_WIDTH
            )
        }
    }

    private fun checkIfDeviceIsRotated(configuration: Configuration): Boolean {
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun adjustScaleX(isRotated: Boolean): Float {
        // If device is not rotated (portrait mode)
        return if (!isRotated) -scaleFormula(false)
        // If device is rotated (landscape mode)
        else -1f
    }

    private fun adjustScaleY(isRotated: Boolean): Float {
        // If device is not rotated (portrait mode)
        return if (!isRotated) 1f
        // If device is rotated (landscape mode)
        else scaleFormula(true)
    }

    private fun scaleFormula(isRotated: Boolean): Float {
        // If device is not rotated (portrait mode)
        return if (!isRotated) (currentCameraPreview.height.toFloat() / currentCameraPreview.width.toFloat()) /
                (ML_KIT_IMAGE_DEFAULT_HEIGHT.toFloat() / ML_KIT_IMAGE_DEFAULT_WIDTH.toFloat())
        // If device is rotated (landscape mode)
        else (currentCameraPreview.width.toFloat() / currentCameraPreview.height.toFloat()) /
                (ML_KIT_IMAGE_DEFAULT_HEIGHT.toFloat() / ML_KIT_IMAGE_DEFAULT_WIDTH.toFloat())
    }
}
