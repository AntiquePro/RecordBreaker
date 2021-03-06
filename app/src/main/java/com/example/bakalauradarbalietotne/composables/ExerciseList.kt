package com.example.bakalauradarbalietotne.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bakalauradarbalietotne.Exercises

@Composable
fun ExerciseList(navController: NavController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        ExerciseCard(
            Exercises.exerciseSquat
        ) {
            navController.navigate("exercise_info_screen/squat")
        }
        ExerciseCard(
            Exercises.exercisePushUp
        ) {
            navController.navigate("exercise_info_screen/pushup")
        }
        ExerciseCard(
            Exercises.exercisePlank
        ) {
            navController.navigate("exercise_info_screen/plank")
        }
    }
}

