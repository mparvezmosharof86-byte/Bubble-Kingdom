package com.example.game.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.viewmodel.GameScreen

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

/**
 * পারফেক্ট ৩ডি রয়্যাল বটম নেভিগেশন বার
 */
@Composable
fun KingdomBottomNavBar(
    currentScreen: GameScreen,
    onHomeClick: () -> Unit,
    onLevelsClick: () -> Unit,
    onEventsClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp)
            .background(Color(0xFF0B0F19).copy(alpha = 0.98f))
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF818CF8).copy(alpha = 0.6f), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            )
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Levels",
                icon = Icons.Default.Map,
                isSelected = currentScreen == GameScreen.WORLD_MAP,
                badgeColor = Color(0xFF3B82F6),
                onClick = onLevelsClick
            )

            NavTabItem(
                label = "Events",
                icon = Icons.Default.EmojiEvents,
                isSelected = currentScreen == GameScreen.EVENTS,
                badgeColor = Color(0xFFEC4899),
                onClick = onEventsClick
            )

            BalancedGoldenHomeButton(
                isSelected = currentScreen == GameScreen.HOME,
                onClick = onHomeClick
            )

            NavTabItem(
                label = "Shop",
                icon = Icons.Default.ShoppingBag,
                isSelected = currentScreen == GameScreen.SHOP,
                badgeColor = Color(0xFF8B5CF6),
                onClick = onShopClick
            )

            NavTabItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentScreen == GameScreen.SETTINGS,
                badgeColor = Color(0xFF64748B),
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun BalancedGoldenHomeButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(if (isSelected) 10.dp else 4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF9C4),
                            Color(0xFFFDE047),
                            Color(0xFFF59E0B),
                            Color(0xFFB45309)
                        )
                    )
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFFFFF), Color(0xFFFFD54F), Color(0xFF78350F))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color(0xFF1E1B4B),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Home",
            color = Color(0xFFFDE047),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.radialGradient(
                            listOf(Color(0xFFFDE047), Color(0xFFD97706))
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(badgeColor.copy(alpha = 0.35f), Color(0xFF1E293B))
                        )
                    }
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color.White else badgeColor.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF1E1B4B) else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = if (isSelected) Color(0xFFFDE047) else Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}
