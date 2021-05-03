package com.example.bakalauradarbalietotne.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bakalauradarbalietotne.R
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain

@Composable
fun FloatingStartButtons(exerciseID: String) {
    Row(
        modifier = Modifier
            .padding(5.dp, 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
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
            onClick = { }
        )

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
            onClick = { }
        )
    }
}