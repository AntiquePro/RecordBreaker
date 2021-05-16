package com.example.bakalauradarbalietotne.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bakalauradarbalietotne.Exercises
import com.example.bakalauradarbalietotne.R
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain


@Composable
fun ExerciseInfo(exerciseID: String) {
    val exercise = remember {
        Exercises.getExerciseByID(exerciseID)
    }
    Column() {
        Text(
            text = "Pašreizējs rekords: ${
                when {
                    exercise?.currentRecord == null -> " nav uzstādīts"
                    exercise.timeCounter -> "${exercise.currentRecord} sekundes"
                    !exercise.timeCounter -> "${exercise.currentRecord} reizes"
                    else -> ""
                }
            }",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.padding(10.dp),
            fontFamily = FontFamily(Font(R.font.opensans_light)),
        )
        Text(
            text = "Soļi",
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OrangeMain,
            fontFamily = FontFamily(Font(R.font.quicksand_semibold)),
        )
        Text(
            text = stringResource(exercise?.exerciseSteps!!),
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.opensans_light)),
            modifier = Modifier.padding(10.dp)
        )
    }
}