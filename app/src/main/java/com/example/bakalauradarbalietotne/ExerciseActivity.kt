package com.example.bakalauradarbalietotne

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.composables.ExerciseInterface
import com.example.bakalauradarbalietotne.composables.getWorkoutProgram
import com.example.bakalauradarbalietotne.composables.workoutInterface
import kotlinx.coroutines.*

class ExerciseActivity : ComponentActivity() {

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exerciseID = intent.getStringExtra("exerciseID")
        val workoutMode = intent.getIntExtra("workoutMode", 0)
        val exercise = Exercises.getExerciseByID(exerciseID!!)

        lifecycleScope.launch {
            setContent {
                CameraPreview()
                DigitalSkeleton.currentPose?.let { currentPose ->
                    DigitalSkeleton().DrawDigitalSkeleton(currentPose)
                }
                ExerciseInterface(workoutMode, exerciseID)
            }
            if (exerciseID == "pushup" || exerciseID == "plank") {
                delay(100)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }

            delay(2000L)
            tts = TextToSpeech(applicationContext, null)
            val workout = Workout(exerciseID, workoutMode, applicationContext, tts)

            // set variables to defaults
            if (workoutMode == 2) workoutInterface.currentSet = 0
            workoutInterface.timeCounter = 0
            workoutInterface.repsDone = 0
            workoutInterface.workoutProcess = true
            var doingReps = false
            var setFinished = false
            var newRecord = 0

            workout.ttsSpeak(
                "Please position your body perpendicular to the camera, " +
                        "get in the correct starting position and start performing exercise after the signal!"
            )
            delay(10000)

            // asynchronous time counter function if exercise is time based
            if (!exercise!!.timeCounter)
                this.async { workoutInterface.countTime() }

            this.async { userOnScreenAsync(workout) }
            while (workoutInterface.workoutProcess) {
                delay(100)
                while (workoutInterface.userOnScreen && workoutInterface.workoutProcess) {
                    if (!doingReps) delay(3000) else delay(1000)
                    val startingPose = workout.checkStartingPosition(this)
                    if (startingPose != null && workoutInterface.workoutProcess) {
                        setFinished = false
                        doingReps = true
                        val workoutProgram = getWorkoutProgram(exerciseID)
                        while (!setFinished && workoutInterface.workoutProcess) {
                            delay(100)
                            workout.startExercise(startingPose, this).await()
                            if (workoutMode == 2 && ((!exercise.timeCounter && workoutInterface.repsDone >= workoutProgram[workoutInterface.currentSet]) || exercise.timeCounter)) {
                                workoutInterface.repsDone = 0
                                workoutInterface.currentSet++
                                if (workoutInterface.currentSet < 4) {
                                    workout.ttsSpeak("Set done, you have 1 minute to rest!")
                                    for (i in 1..60) {
                                        if (workoutInterface.workoutProcess)
                                            delay(1000)
                                        else {
                                            workout.ttsSpeak("Workout canceled!")
                                            delay(2000)
                                            finish()
                                        }
                                    }
                                    workout.ttsSpeak("You can continue!")
                                    delay(1000L)
                                    setFinished = true
                                    doingReps = false
                                } else {
                                    workout.ttsSpeak("Workout finished! Good job!")
                                    delay(2000)
                                    finish()
                                }
                            }
                        }
                        delay(100)
                    }
                }
            }

            if (workoutMode == 1 && !exercise.timeCounter && workoutInterface.repsDone > exercise.currentRecord)
                newRecord = workoutInterface.repsDone

            if (workoutMode == 1 && exercise.timeCounter && workoutInterface.timeCounter > exercise.currentRecord)
                newRecord = workoutInterface.timeCounter

            // put record in shared preferences if it is better
            if (newRecord > exercise.currentRecord) {
                workout.ttsSpeak("Congratulations! You have beaten your $exerciseID record!")
                when (exerciseID) {
                    "squat" -> {
                        Toast.makeText(
                            applicationContext,
                            "Congratulations! You have set new squat record $newRecord!",
                            Toast.LENGTH_LONG
                        ).show()
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
                        Toast.makeText(
                            applicationContext,
                            "Congratulations! You have set new pushup record $newRecord!",
                            Toast.LENGTH_LONG
                        ).show()
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
                        Toast.makeText(
                            applicationContext,
                            "Congratulations! You have set new plank record $newRecord sekonds!",
                            Toast.LENGTH_LONG
                        ).show()
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
            finish()
        }
    }

    suspend fun userOnScreenAsync(workout: Workout) {
        while (true) {
            workoutInterface.userOnScreen = workout.checkIfUserIsOnScreen()
            delay(5000)
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::tts.isInitialized) {
            if (tts.isSpeaking) {
                tts.stop()
                tts.shutdown()
            }
        }
        workoutInterface.workoutProcess = false
    }

    override fun onPause() {
        super.onPause()
        if (this::tts.isInitialized) {
            if (tts.isSpeaking) {
                tts.stop()
                tts.shutdown()
            }
        }
        workoutInterface.workoutProcess = false
    }
}