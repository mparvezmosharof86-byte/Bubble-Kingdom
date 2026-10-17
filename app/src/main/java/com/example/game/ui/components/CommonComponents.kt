package com.example.game.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlossyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color(0xFF10B981), Color(0xFF059669)),
    icon: String? = null,
    testTag: String = "glossy_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1.0f, label = "button_scale")

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(colors))
            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CoinPill(
    coins: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1E1B4B).copy(alpha = 0.85f), Color(0xFF312E81).copy(alpha = 0.85f))
                )
            )
            .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🪙", fontSize = 18.sp, modifier = Modifier.padding(end = 6.dp))
        Text(
            text = "$coins",
            color = Color(0xFFFDE68A),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StarRatingBar(
    stars: Int,
    maxStars: Int = 3,
    starSize: Int = 24,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..maxStars) {
            val filled = i <= stars
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $i",
                tint = if (filled) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}
