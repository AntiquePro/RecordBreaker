package com.example.bakalauradarbalietotne.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bakalauradarbalietotne.R

@Preview
@Composable
fun MainMenuTooblar() {
    Box (
        modifier = Modifier.background(Color.Transparent)
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(R.drawable.main_menu_top_toolbar),
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = stringResource(R.string.choose_exercise),

            modifier = Modifier.padding(
                start = 15.dp,
                top = 60.dp
            ),
            color = Color.White,
            fontSize = 34.sp,
            fontFamily = FontFamily(Font(R.font.quicksand_semibold))
        )
    }
}

