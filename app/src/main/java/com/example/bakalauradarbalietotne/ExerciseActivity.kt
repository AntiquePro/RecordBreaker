package com.example.bakalauradarbalietotne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bakalauradarbalietotne.composables.CameraPreview
import com.example.bakalauradarbalietotne.ui.theme.OrangeMain

class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraPreview()
            ExerciseInterface()
        }
    }
}

@Preview
@Composable
fun ExerciseInterface() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            LinearProgressIndicator(
                color = OrangeMain,
                progress = 0.2f,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "3/6\nREIZES",
                fontWeight = Bold,
                fontSize = 40.sp,
                color = Color.White
            )
            Text(
                textAlign = TextAlign.Center,
                text = "2:14\nLAIKS",
                fontWeight = Bold,
                fontSize = 40.sp,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
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


