package com.example.bakalauradarbalietotne.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakalauradarbalietotne.Exercises
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain


@Composable
fun CustomTopAppBar(navController: NavController, exerciseID: String) {
    TopAppBar(
        title = {
            Text(Exercises.getExerciseByID(exerciseID)?.title ?: "unknown exercise")
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        },
        backgroundColor = OrangeMain,
        contentColor = Color.White,
        elevation = 12.dp
    )
}

