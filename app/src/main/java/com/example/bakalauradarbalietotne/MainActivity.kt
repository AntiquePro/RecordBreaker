package com.example.bakalauradarbalietotne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.bakalauradarbalietotne.composables.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set current records with sharedPreferences
        Exercises.exerciseSquat.currentRecord =
            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt("squatRecord", 0)
        Exercises.exercisePushUp.currentRecord =
            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt("pushupRecord", 0)
        Exercises.exercisePlank.currentRecord =
            getSharedPreferences("ExerciseRecords", MODE_PRIVATE).getInt("plankRecord", 0)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "exercise_choice_screen"
            ) {
                composable(route = "exercise_choice_screen") {
                    ExerciseChoiceScreen(navController)
                }
                composable(
                    route = "exercise_info_screen/{exerciseID}",
                    arguments = listOf(
                        navArgument("exerciseID") {
                            type = NavType.StringType
                        }
                    )) {
                    val exerciseID = it.arguments?.getString("exerciseID") ?: "Error"
                    ExerciseInfoScreen(navController, exerciseID)
                }
            }
        }
    }
}

@Composable
fun ExerciseChoiceScreen(navController: NavController) {
    Column {
        MainMenuTooblar()
        ExerciseList(navController)
    }
}

@Composable
fun ExerciseInfoScreen(navController: NavController, exerciseID: String) {
    Column(Modifier.fillMaxSize()) {
        CustomTopAppBar(navController, exerciseID)
        VideoPlayer(exerciseID)
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            ExerciseInfo(exerciseID)
            FloatingStartButtons(exerciseID)
        }
    }
}

