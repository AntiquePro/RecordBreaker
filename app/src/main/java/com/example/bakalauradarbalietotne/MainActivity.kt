package com.example.bakalauradarbalietotne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.bakalauradarbalietotne.composables.ExerciseList
import com.example.bakalauradarbalietotne.composables.MainMenuTooblar


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "exercise_choice_screen"
            ) {
                composable("exercise_choice_screen") {
                    ExerciseChoiceScreen(navController)
                }
                composable(
                    route = "exercise_info_screen/{exercise}",
                    arguments = listOf(
                        navArgument("exercise") {
                            type = NavType.StringType
                        }
                    )) {
                    val exercise = remember {
                        it.arguments?.getString("exercise")
                    }
                    ExerciseInfoScreen(navController, exercise ?: "bomba")
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
fun ExerciseInfoScreen(navController: NavController, exercise: String) {
    Text(text = exercise)
}