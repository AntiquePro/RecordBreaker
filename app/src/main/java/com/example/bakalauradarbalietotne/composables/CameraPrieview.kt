package com.example.bakalauradarbalietotne.composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
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
// Block producer gaida kad kadra analīze beigsies un tad analizē nākamo kadru, keep only latest - atmet tos kadrus
// kas ilgi lādējas lkm
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                    .build()

                imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { image ->


                    val rotationDegrees = image.imageInfo.rotationDegrees
                    Log.d("IMAGE_ANALYSIS", "Preview rotation degree is $rotationDegrees")

                    val mediaImage = InputImage.fromMediaImage(image.image!!, rotationDegrees)

                    poseDetector.process(mediaImage)
                        .addOnSuccessListener { pose ->
                            image.close()
                            Log.d("PoseDetection", "Successful detection $pose")
                            pose.getPoseLandmark(PoseLandmark.LEFT_EAR)?.let {
                                Log.d("PoseDetection", "Left ear: ${it.position3D}")
                            }

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


@Composable
fun makeText(text: String) {
    Text(text, fontSize = 20.sp)
}
