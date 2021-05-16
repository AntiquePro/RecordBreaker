package com.example.bakalauradarbalietotne.composables

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Canvas
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.bakalauradarbalietotne.DigitalSkeleton
import com.example.bakalauradarbalietotne.PoseViewModel
import com.example.bakalauradarbalietotne.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(options)


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            DigitalSkeleton.currentCameraPreview = previewView
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { image ->
                    val rotationDegrees = image.imageInfo.rotationDegrees
                    val inputImage = InputImage.fromMediaImage(image.image!!, rotationDegrees)
                    poseDetector.process(inputImage)
                        .addOnSuccessListener { pose ->
                            image.close()
                            DigitalSkeleton.currentPose = pose
                            Log.d(
                                "ImageInfo",
                                "Preview view width: ${previewView.width}, preview view height: ${previewView.height}"
                            )
                            Log.d(
                                "ImageInfo",
                                "Image width: ${image.height}, image height: ${image.width}"
                            )
                            Log.d("PoseDetection", "Successful detection $pose")
                        }.addOnFailureListener { e ->
                            Log.d("PoseDetection", "Failed detection: $e")
                        }
                }
                )
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                    preview
                )
            }, executor)
            previewView
        }
    )
}


//                            pose.getPoseLandmark(PoseLandmark.LEFT_EAR)?.let {
//                                Log.d("PoseDetection", "Left ear: ${it.position3D}")
//                            }

//fun sendPose(pose: Pose): androidx.compose.ui.graphics.Canvas {
//    return androidx.compose.ui.graphics.Canvas(Canvas(BitmapFactory.decodeResource( R.drawable.image_stickman_squats)))
//}


/*@Composable
fun drawDigitalSkeleton(pose: Pose) {

    val landmarks = pose.allPoseLandmarks

    if (landmarks.isEmpty()) {
        Log.d("PoseDetection", "No landmarks detected")
        return
    }

    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
    val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

    val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
    val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
    val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
    val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
    val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
    val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
    val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
    val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
    val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
    val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

    for (landmark in landmarks) {
        DrawCanvasPoint(landmark)
    }
}

@Composable
fun DrawCanvasPoint(landmark: PoseLandmark) {
    val point = landmark.position

    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White,
            center = Offset(point.x, point.y),
            radius = 8f
        )
    }

}*/