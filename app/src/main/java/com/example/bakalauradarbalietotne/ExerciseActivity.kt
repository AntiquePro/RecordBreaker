package com.example.bakalauradarbalietotne

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Contacts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.composables.ExerciseInterface
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import kotlinx.coroutines.*


class ExerciseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //GlobalScope.launch(Dispatchers.Main) {
        setContent {
            //val composableScope = rememberCoroutineScope()
            CameraPreview()
            PrepareWorkout {
                GlobalScope.launch {
                    setContent {
                        CameraPreview()
                        CounterView()
                    }
                    startCounter().await()
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
        }
    }
}

@Composable
fun CounterView() {
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
    var job = GlobalScope.async {
        delay(1000L)
        counterText.counter = "3"
        delay(1000L)
        counterText.counter = "2"
        delay(1000L)
        counterText.counter = "1"
        delay(1000L)
        counterText.counter = "STARTS"
        delay(1000L)
        counterText.counter = ""
    }
    return job
}

object counterText {
    var counter by mutableStateOf("")
}

@Composable
fun PrepareWorkout(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Novietojiet ierīci tā, lai pilns Jūsu ķermenis ietilptu kadrā treniņa laikā",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.quicksand_semibold))
        )
        Button(
            modifier = Modifier.padding(20.dp),
            colors = ButtonDefaults.buttonColors(OrangeMain),
            onClick = onClick
        ) {
            Text("SĀKT")
        }
    }
}

/*setContent {
    val composableScope = rememberCoroutineScope()
    CameraPreview()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Novietojiet ierīci tā, lai pilns Jūsu ķermenis ietilptu kadrā treniņa laikā",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.quicksand_semibold))
        )
        Button(
            modifier = Modifier.padding(20.dp),
            onClick = {
                GlobalScope.launch {
                    setContent {
                        CameraPreview()
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
                    StartCounter().await()
                    setContent {
                        CameraPreview()
                        Text("${DigitalSkeleton.currentPose}")
                        DigitalSkeleton.currentPose?.let { currentPose ->
                            DigitalSkeleton().DrawDigitalSkeleton(currentPose)
                        }
                        ExerciseInterface()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(OrangeMain)
        ) {
            Text("SĀKT")
        }
    }
    //}
}*/


