package com.example.bakalauradarbalietotne

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import kotlinx.coroutines.*

class TutorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exercise = intent.getStringExtra("exerciseID")
        val workoutMode = intent.getIntExtra("workoutMode", 0)

        lifecycleScope.launch {
            setContent {
                CameraPreview()
                PrepareWorkout(exercise!!, workoutMode)
            }
            if (exercise == "pushup" || exercise == "plank") {
                delay(100)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    @Composable
    fun CounterView() {
        Box(modifier = Modifier.background(OrangeMain).fillMaxSize())
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                counterText.counter,
                fontSize = 60.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

    fun startCounter(): Deferred<Unit> {
        return lifecycleScope.async {
            counterText.counter = ""
            delay(1000)
            counterText.counter = "3"
            delay(1000)
            counterText.counter = "2"
            delay(1000)
            counterText.counter = "1"
            delay(1000)
            counterText.counter = "STARTS"
        }
    }

    object counterText {
        var counter by mutableStateOf("")
    }

    @Composable
    fun PrepareWorkout(exerciseID: String, workoutMode: Int) {
        val context = LocalContext.current
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Lūdzu, ieslēdziet skaņu un novietojiet ierīci tā, lai pilns ķermenis ietilptu kadrā treniņa laikā",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.quicksand_semibold))
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = when (workoutMode) {
                    1 -> "Centieties pārspēt savu pašreizējo rekordu!"
                    2 -> "Veiciet treniņu sekojot norādījumiem!"
                    else -> ""
                },
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.quicksand_semibold))
            )
            Button(
                modifier = Modifier.padding(20.dp),
                colors = ButtonDefaults.buttonColors(OrangeMain),
                onClick = {
                    lifecycleScope.launch {
                        setContent {
                            CounterView()
                        }
                        startCounter().await()
                        delay(1000)
                        context.startActivity(
                            Intent(context, ExerciseActivity::class.java)
                                .putExtra("exerciseID", exerciseID)
                                .putExtra("workoutMode", workoutMode)
                        )
                        finish()
                    }
                }
            ) {
                Text("SĀKT")
            }
        }
    }
}


