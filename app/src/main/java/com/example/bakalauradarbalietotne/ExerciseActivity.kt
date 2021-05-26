package com.example.bakalauradarbalietotne

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.composables.ExerciseInterface
import com.example.bakalauradarbalietotne.composables.getWorkoutProgram
import com.example.bakalauradarbalietotne.composables.workoutInterface
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import kotlinx.coroutines.*
import kotlin.properties.Delegates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


class ExerciseActivity : ComponentActivity() {

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exercise = intent.getStringExtra("exerciseID")
        val workoutMode = intent.getIntExtra("workoutMode", 0)
        var userReady = false
        lifecycleScope.launch {
            setContent {
                CameraPreview()
                Text("${DigitalSkeleton.currentPose}")
                DigitalSkeleton.currentPose?.let { currentPose ->
                    DigitalSkeleton().DrawDigitalSkeleton(currentPose)
                }
                ExerciseInterface(workoutMode, exercise!!)
            }
            if (exercise == "pushup" || exercise == "plank") {
                delay(100)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }

            //delay(3000L)
            tts = TextToSpeech(applicationContext, null)
            val workout = Workout(exercise!!, workoutMode, applicationContext, tts)
            //var userIsOnScreen = true
            var startingPositionGood = false
            var doingReps = false
            var setFinished = false
            var newRecord = 0
            workoutInterface.workoutProcess = true
            // asynchronous time counter function if exercise is time based
            if (!Exercises.getExerciseByID(exercise)!!.timeCounter) this.async { workoutInterface.countTime() }

            this.async { userOnScreenAsync(workout) }
            while (workoutInterface.workoutProcess) {
                // userIsOnScreen = true
                Log.d("chyck", "im in workout process while loop before delay")
                delay(100)
                Log.d("chyck", "im in workout process while loop after delay")
                //workout.ttsSpeak("Please, get in the $exercise starting position!")
                while (workoutInterface.userOnScreen && workoutInterface.workoutProcess) {
                    Log.d("chyck", "im in userIsOnScreen while loop before delay")
                    if (!doingReps) delay(3000)
                    else delay(1000)
                    Log.d("chyck", "im in userIsOnScreen while loop after delay")
                    // if user is not on screen -> break this loop
                    //userIsOnScreen = workout.checkIfUserIsOnScreen()
                    //if (!workoutInterface.userOnScreen) break
                    var startingPose = workout.checkStartingPosition(this)
                    Log.d("chyck", "startingPose: $startingPose")
                    if (startingPose != null) {
                        doingReps = true
                        val workoutProgram = getWorkoutProgram(exercise)
                        while (!setFinished) {
                            delay(100)
                            Log.d("chyck", "im in starting pose letsggo")
                            // start timer? i guess if exercise is not plank??
                            workout.startExercise(startingPose, this).await()
                            //doingReps = true
                            if (workoutMode == 2 && workoutInterface.repsDone >= workoutProgram[workoutInterface.currentSet]) {
                                workoutInterface.repsDone = 0
                                workoutInterface.currentSet++
                                workout.ttsSpeak("Set done, you have 1 minute to rest!")
                                delay(10000L)
                                workout.ttsSpeak("You can continue!")
                                delay(1000L)
                                setFinished = true
                            }

                            if (workoutMode == 1) {
                                newRecord = 5
                                setFinished = true
                                workoutInterface.workoutProcess = false
                            }
                            delay(100)
                        }
                    }
                }
            }

            // put record in shared preferences if it is better

            if (newRecord > Exercises.getExerciseByID(exercise)!!.currentRecord) {
                when (exercise) {
                    "squat" -> {
                        getSharedPreferences("ExerciseRecords", MODE_PRIVATE).edit().apply {
                            putInt("squatRecord", newRecord)
                            apply()
                        }
                        Exercises.exerciseSquat.currentRecord =
                            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt(
                                "squatRecord",
                                0
                            )
                    }
                    "pushup" -> {
                        getSharedPreferences("ExerciseRecords", MODE_PRIVATE).edit().apply {
                            putInt("pushupRecord", newRecord)
                            apply()
                        }
                        Exercises.exercisePushUp.currentRecord =
                            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt(
                                "pushupRecord",
                                0
                            )
                    }
                    "plank" -> {
                        getSharedPreferences("ExerciseRecords", MODE_PRIVATE).edit().apply {
                            putInt("plankRecord", newRecord)
                            apply()
                        }
                        Exercises.exercisePlank.currentRecord =
                            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt(
                                "plankRecord",
                                0
                            )
                    }
                }
            }

            setContent {
                Text("Workouto overetto")
            }
        }
    }

    suspend fun userOnScreenAsync(workout: Workout) {
        while (true) {
            workoutInterface.userOnScreen = workout.checkIfUserIsOnScreen()
            delay(5000)
            //if (!workoutInterface.userOnScreen) tts.stop()
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
        var job = lifecycleScope.async {
            delay(1000)
            counterText.counter = "3"
            delay(1000)
            counterText.counter = "2"
            delay(1000)
            counterText.counter = "1"
            delay(1000)
            counterText.counter = "STARTS"
            delay(1000)
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
                text = "Lūdzu, ieslēdziet skaņu un novietojiet ierīci tā, lai pilns ķermenis ietilptu kadrā treniņa laikā",
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

    // šito var pie test
    override fun onStop() {
        super.onStop()
        // šo 100% vajag debug sadaļā!!
        if (this::tts.isInitialized) {
            if (tts.isSpeaking) {
                tts.stop()
                tts.shutdown()
            }
        }
        workoutInterface.timeCounter = 0
    }

    override fun onPause() {
        super.onPause()
        // šo 100% vajag debug sadaļā!!
        if (this::tts.isInitialized) {
            if (tts.isSpeaking) {
                tts.stop()
                tts.shutdown()
            }
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


