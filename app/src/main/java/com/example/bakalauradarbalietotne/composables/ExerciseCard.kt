package com.example.bakalauradarbalietotne.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bakalauradarbalietotne.Exercise
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain


@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = exercise.painterID!!),
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = exercise.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeMain,
                        modifier = Modifier.padding(end = 20.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Pašreizējs rekords:\n " +
                                when {
                                    exercise.currentRecord == 0 -> "Nav uzstādīts"
                                    exercise.timeCounter -> "${exercise.currentRecord} sekundes"
                                    !exercise.timeCounter -> "${exercise.currentRecord} reizes"
                                    else -> ""
                                },
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.ExtraLight,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            }
        }
    }
}
