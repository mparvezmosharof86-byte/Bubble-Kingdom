package com.example.game.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: GameViewModel) {
    // Auto-advance to home after 2.2 seconds if user does not tap
    LaunchedEffect(Unit) {
        delay(2200L)
        viewModel.navigateTo(GameScreen.HOME)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_anim")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_bounce"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4C1D95)
                    )
                )
            )
            .clickable { viewModel.navigateTo(GameScreen.HOME) }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Mascot in Glowing Bubble
            Box(
                modifier = Modifier
                    .offset(y = bounceY.dp)
                    .size(160.dp)
                    .scale(glowPulse)
                    .shadow(24.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFDE68A),
                                Color(0xFFF59E0B),
                                Color(0xFFD97706)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑\n🐶", fontSize = 52.sp, textAlign = TextAlign.Center)
            }

            Text(
                text = "BUBBLE\nKINGDOM",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFDE047),
                textAlign = TextAlign.Center,
                lineHeight = 44.sp,
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "Pet Rescue Puzzle Adventure",
                fontSize = 17.sp,
                color = Color(0xFFE0E7FF),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "✨ TAP ANYWHERE TO START ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 64.dp)
            )
        }
    }
}
