package com.example.bakalauradarbalietotne.composables


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.example.bakalauradarbalietotne.ExerciseActivity
import com.example.bakalauradarbalietotne.Exercises
import com.example.bakalauradarbalietotne.R
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain

@Composable
fun FloatingStartButtons(exerciseID: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(5.dp, 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // new record button
        ExtendedFloatingActionButton(
            text = { Text("Jauns\nrekords") },
            backgroundColor = OrangeMain,
            icon = {
                Icon(
                    painterResource(
                        R.drawable.ic_baseline_emoji_events_24
                    ),
                    ""
                )
            },
            onClick = {
                checkCameraPermissions(context, exerciseID, 1)
            }
        )

        // workout button
        if (Exercises.getExerciseByID(exerciseID)?.currentRecord != 0)
        ExtendedFloatingActionButton(
            text = { Text("Veikt\ntreniņu") },
            backgroundColor = OrangeMain,
            icon = {
                Icon(
                    painterResource(
                        R.drawable.ic_baseline_fitness_center_24
                    ), ""
                )
            },
            onClick = {
                checkCameraPermissions(context, exerciseID, 2)
            }
        )
    }
}

fun checkCameraPermissions(context: Context, exerciseID: String, workoutMode: Int) {
    // Permission is granted
    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        context.startActivity(
            Intent(context, ExerciseActivity::class.java)
                .putExtra(
                    "exerciseID",
                    exerciseID
                ).putExtra(
                    "workoutMode",
                    workoutMode
                )
        )
    }
    // Permission is not granted
    else {
        requestPermissions(
            context as Activity, arrayOf(
                Manifest.permission.CAMERA
            ), 1
        )
    }
}
