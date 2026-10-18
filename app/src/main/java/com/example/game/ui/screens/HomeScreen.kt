package com.example.game.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.CoinPill
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun HomeScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    val freeSpinReady = viewModel.canSpinForFree()
    val todayEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    val dailyGiftReady = todayEpoch > saveState.lastDailyClaimDay

    // Pulsing alert animation for red notification dots
    val transition = rememberInfiniteTransition(label = "pulse_anim")
    val dotScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF172554),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. TOP BAR: 3D Glossy Currency & Star Rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinPill(
                    coins = saveState.playerCoins,
                    onClick = { viewModel.navigateTo(GameScreen.SHOP) }
                )

                // 3D Metallic Star Counter Pill
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(
                                listOf(Color(0xFFFFE082), Color(0xFFFFA000), Color(0xFFFFD54F))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${saveState.levelStars.values.sum()} Stars",
                            color = Color(0xFFFEF3C7),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // 2. MAIN CENTER BODY (3D Kingdom Castle & Action Zone)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 3D Kingdom Master Card with Beveled Golden Rim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF3730A3),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            3.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFF59D),
                                    Color(0xFFFFB300),
                                    Color(0xFFD97706),
                                    Color(0xFF78350F)
                                )
                            ),
                            RoundedCornerShape(32.dp)
                        )
                        .padding(vertical = 22.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "👑 BUBBLE KINGDOM 👑",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047),
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3D Mascot Sphere
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(14.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFFFEF9C3),
                                            Color(0xFFFDE047),
                                            Color(0xFFF59E0B)
                                        )
                                    )
                                )
                                .border(3.dp, Color(0xFFFFFBEB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🐶", fontSize = 52.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Level Progress Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E1B4B).copy(alpha = 0.8f))
                                .border(1.5.dp, Color(0xFF818CF8), RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Level ${saveState.unlockedLevel} / 100",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "🐾 ${saveState.rescuedPets.size} Pets Rescued  •  ⭐ 100 Worlds",
                            fontSize = 13.sp,
                            color = Color(0xFFC7D2FE),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 3. ACTION ROW: Left Side 3D Icons (Wheel & Gift) + Big 3D Play Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Pure 3D Canvas Icons (NO EMOJI, NO ENCLOSING BUBBLES)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CanvasLuckyWheel(
                            hasNotification = freeSpinReady,
                            dotScale = dotScale,
                            onClick = { viewModel.navigateTo(GameScreen.LUCKY_SPIN) }
                        )

                        CanvasDailyGift(
                            hasNotification = dailyGiftReady,
                            dotScale = dotScale,
                            onClick = { viewModel.navigateTo(GameScreen.DAILY_REWARDS) }
                        )
                    }

                    // Right: Massive 3D Glossy Arcade PLAY Button
                    ThreeDPlayButton(
                        levelNum = saveState.unlockedLevel,
                        onClick = { viewModel.startLevel(saveState.unlockedLevel) },
                        modifier = Modifier
                            .weight(1f)
                            .height(78.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. BOTTOM NAVIGATION BAR: Levels | Events | [ 🏠 HOME (BIG GOLDEN) ] | Shop | Settings
            KingdomBottomNavBar(
                currentScreen = GameScreen.HOME,
                onHomeClick = { /* Already on Home */ },
                onLevelsClick = { viewModel.navigateTo(GameScreen.WORLD_MAP) },
                onEventsClick = { viewModel.navigateTo(GameScreen.EVENTS) },
                onShopClick = { viewModel.navigateTo(GameScreen.SHOP) },
                onSettingsClick = { viewModel.navigateTo(GameScreen.SETTINGS) }
            )
        }
    }
}

/**
 * PURE 3D VECTOR LUCKY WHEEL
 */
@Composable
fun CanvasLuckyWheel(
    hasNotification: Boolean,
    dotScale: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            drawCircle(
                color = Color.Black.copy(alpha = 0.45f),
                radius = radius,
                center = Offset(center.x, center.y + 4f)
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFFFE082),
                        Color(0xFFFFA000),
                        Color(0xFFFFD54F),
                        Color(0xFFE65100),
                        Color(0xFFFFE082)
                    ),
                    center = center
                ),
                radius = radius,
                center = center
            )

            val innerR = radius - 4.5f
            val slices = listOf(
                Color(0xFFEF4444),
                Color(0xFF3B82F6),
                Color(0xFF10B981),
                Color(0xFFF59E0B),
                Color(0xFF8B5CF6),
                Color(0xFFEC4899)
            )
            val sweep = 360f / slices.size
            slices.forEachIndexed { i, c ->
                drawArc(
                    color = c,
                    startAngle = i * sweep,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(center.x - innerR, center.y - innerR),
                    size = Size(innerR * 2, innerR * 2)
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF9C4), Color(0xFFF57F17)),
                    center = center,
                    radius = 9f
                ),
                radius = 9f,
                center = center
            )
            drawCircle(color = Color.White, radius = 3.5f, center = center)

            val pointer = Path().apply {
                moveTo(center.x, center.y - innerR + 1f)
                lineTo(center.x - 5.5f, center.y - innerR - 6f)
                lineTo(center.x + 5.5f, center.y - innerR - 6f)
                close()
            }
            drawPath(pointer, Color(0xFFEF4444))
            drawPath(pointer, Color.White, style = Stroke(width = 1.5f))
        }

        if (hasNotification) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-1).dp, y = 1.dp)
                    .scale(dotScale)
                    .size(15.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * PURE 3D VECTOR DAILY GIFT BOX
 */
@Composable
fun CanvasDailyGift(
    hasNotification: Boolean,
    dotScale: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(54.dp)) {
            val w = size.width
            val h = size.height

            drawOval(
                color = Color.Black.copy(alpha = 0.4f),
                topLeft = Offset(4f, h - 8f),
                size = Size(w - 8f, 8f)
            )

            val boxTop = 18f
            val boxH = h - 25f
            val boxW = w - 10f
            val boxLeft = 5f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF43F5E), Color(0xFFBE123C), Color(0xFF881337)),
                    startY = boxTop,
                    endY = boxTop + boxH
                ),
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxW, boxH),
                cornerRadius = CornerRadius(8f, 8f)
            )

            val lidTop = 12f
            val lidH = 10f
            val lidW = w - 4f
            val lidLeft = 2f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFB7185), Color(0xFFE11D48)),
                    startY = lidTop,
                    endY = lidTop + lidH
                ),
                topLeft = Offset(lidLeft, lidTop),
                size = Size(lidW, lidH),
                cornerRadius = CornerRadius(6f, 6f)
            )

            val ribbonW = 9f
            val ribbonX = (w - ribbonW) / 2f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFB300), Color(0xFFE65100))
                ),
                topLeft = Offset(ribbonX, lidTop),
                size = Size(ribbonW, boxH + (boxTop - lidTop)),
                cornerRadius = CornerRadius(2f, 2f)
            )

            val hRibbonY = boxTop + (boxH - 7f) / 2f
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFE65100), Color(0xFFFFF59D), Color(0xFFE65100))
                ),
                topLeft = Offset(boxLeft, hRibbonY),
                size = Size(boxW, 7f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            val bowCenter = Offset(w / 2f, lidTop + 1f)
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFB300)),
                    center = Offset(bowCenter.x - 7f, bowCenter.y - 4f),
                    radius = 9f
                ),
                topLeft = Offset(bowCenter.x - 14f, bowCenter.y - 9f),
                size = Size(14f, 10f)
            )
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFB300)),
                    center = Offset(bowCenter.x + 7f, bowCenter.y - 4f),
                    radius = 9f
                ),
                topLeft = Offset(bowCenter.x, bowCenter.y - 9f),
                size = Size(14f, 10f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFFFC107)),
                    center = bowCenter,
                    radius = 4f
                ),
                radius = 4.5f,
                center = bowCenter
            )
        }

        if (hasNotification) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-1).dp, y = 1.dp)
                    .scale(dotScale)
                    .size(15.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * 3D CANDY ARCADE PUSH BUTTON
 */
@Composable
fun ThreeDPlayButton(
    levelNum: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .offset(y = if (isPressed) 4.dp else 0.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("play_level_btn")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF064E3B))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(if (isPressed) 2.dp else 8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF4ADE80),
                            Color(0xFF16A34A),
                            Color(0xFF15803D)
                        )
                    )
                )
                .border(2.dp, Color(0xFFBBF7D0).copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY LEVEL $levelNum",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * 3D BOTTOM NAVIGATION BAR: Levels | Events | [ 🏠 HOME (BIG GOLDEN CENTER) ] | Shop | Settings
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
            // 1. LEVELS (World Map)
            NavTabItem(
                label = "Levels",
                icon = Icons.Default.Map,
                isSelected = currentScreen == GameScreen.WORLD_MAP,
                badgeColor = Color(0xFF3B82F6),
                onClick = onLevelsClick
            )

            // 2. EVENTS (Right beside Levels)
            NavTabItem(
                label = "Events",
                icon = Icons.Default.EmojiEvents,
                isSelected = currentScreen == GameScreen.EVENTS,
                badgeColor = Color(0xFFEC4899),
                onClick = onEventsClick
            )

            // 3. HOME BUTTON IN THE MIDDLE (BIGGER, ELEVATED & 3D GOLDEN)
            CenterGoldenHomeButton(
                isSelected = currentScreen == GameScreen.HOME,
                onClick = onHomeClick
            )

            // 4. BOOSTER SHOP
            NavTabItem(
                label = "Shop",
                icon = Icons.Default.ShoppingBag,
                isSelected = currentScreen == GameScreen.SHOP,
                badgeColor = Color(0xFF8B5CF6),
                onClick = onShopClick
            )

            // 5. SETTINGS
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

/**
 * ELEVATED 3D GOLDEN CENTER HOME BUTTON
 */
@Composable
fun CenterGoldenHomeButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(y = (-14).dp) // Elevated 3D Floating Look above bottom bar!
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(14.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF9C4), // Bright highlight
                            Color(0xFFFDE047),
                            Color(0xFFF59E0B),
                            Color(0xFFB45309)  // Deep 3D gold shadow
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
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
                tint = Color(0xFF1E1B4B), // Bold deep royal navy contrast
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Home",
            color = Color(0xFFFDE047),
            fontSize = 12.sp,
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
            .padding(horizontal = 4.dp, vertical = 4.dp)
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
