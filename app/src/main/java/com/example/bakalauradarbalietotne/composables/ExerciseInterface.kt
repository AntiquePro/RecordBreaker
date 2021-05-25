package com.example.bakalauradarbalietotne.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bakalauradarbalietotne.Exercises
import com.example.bakalauradarbalietotne.R
import com.example.bakalauradarbalietotne.composables.workoutInterface.currentSet
import com.example.bakalauradarbalietotne.composables.workoutInterface.repsDone
import com.example.bakalauradarbalietotne.composables.workoutInterface.timeCounter
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain
import kotlinx.coroutines.delay

@Composable
fun ExerciseInterface(mode: Int, exercise: String) {

    val currentExercise = Exercises.getExerciseByID(exercise)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_help_24),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (mode == 2)
                LinearProgressIndicator(
                    color = OrangeMain,
                    progress = currentSet * 0.2f,
                )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(0.dp, 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (mode == 2 && !currentExercise!!.timeCounter)
                Text(
                    textAlign = TextAlign.Center,
                    text = "${repsDone}/${
                        getWorkoutProgram(
                            exercise
                        )[currentSet]
                    }\nREIZES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )
            else if (mode == 1 && !currentExercise!!.timeCounter)
                Text(
                    textAlign = TextAlign.Center,
                    text = "${repsDone}(${currentExercise.currentRecord})\nREIZES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )
            if (mode == 2 && currentExercise!!.timeCounter)
                Text(
                    textAlign = TextAlign.Center,
                    text = "${timeString()}/${
                        getWorkoutProgram(
                            exercise
                        )[currentSet]
                    }sec\nLAIKS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )
            else if (mode == 1 && currentExercise!!.timeCounter)
                Text(
                    textAlign = TextAlign.Center,
                    text = "${timeString()}(${currentExercise.currentRecord / 60}:${
                        if (currentExercise.currentRecord % 60 < 10) "0${currentExercise.currentRecord % 60}"
                        else currentExercise.currentRecord % 60
                    })\nLAIKS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )
            else Text(
                textAlign = TextAlign.Center,
                text = "${timeString()}\nLAIKS",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Button pause
            Button(
                modifier = Modifier.size(65.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(OrangeMain),
                onClick = { }) {
                Icon(
                    painter = painterResource(R.drawable.exo_controls_pause),
                    contentDescription = ""
                )
            }
            // Button next
            Button(
                modifier = Modifier.size(65.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(OrangeMain),
                onClick = { }) {
                Icon(
                    painter = painterResource(R.drawable.exo_controls_next),
                    contentDescription = ""
                )
            }
            // Button end
            Button(
                modifier = Modifier.size(65.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(OrangeMain),
                onClick = { }) {
                Text(text = "END")
            }
        }
    }
}


fun getWorkoutProgram(exercise: String): List<Int> {
    val currentRecord = Exercises.getExerciseByID(exercise)!!.currentRecord
    if (exercise != "plank") {
        when {
            currentRecord == 1 -> return listOf(
                currentRecord,
                currentRecord,
                currentRecord,
                currentRecord,
                currentRecord
            )
            currentRecord > 4 -> return listOf(
                currentRecord - 2,
                currentRecord - 1,
                currentRecord,
                currentRecord - 2,
                currentRecord
            )
            else -> return listOf(
                currentRecord - 1,
                currentRecord - 1,
                currentRecord,
                currentRecord - 1,
                currentRecord,
            )
        }
    } else
        when {
            currentRecord <= 10 -> return listOf(
                currentRecord,
                currentRecord,
                currentRecord,
                currentRecord,
                currentRecord
            )
            else -> return listOf(
                currentRecord - 5,
                currentRecord - 5,
                currentRecord,
                currentRecord - 5,
                currentRecord,
            )
        }
}

fun timeString(): String {
    val seconds = timeCounter % 60
    val minutes = timeCounter / 60
    return "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
}


object workoutInterface {

    suspend fun countTime() {
        while (true) {
            delay(1000)
            timeCounter++
        }
    }

    var timeCounter by mutableStateOf(0)

    var repsDone by mutableStateOf(0)
    var repsToDo by mutableStateOf(0)
    var currentSet by mutableStateOf(0)

    var currentProgress by mutableStateOf(0)

    var workoutProcess = true

    var userOnScreen by mutableStateOf(false)

    var infoSelected by mutableStateOf(false)


}


