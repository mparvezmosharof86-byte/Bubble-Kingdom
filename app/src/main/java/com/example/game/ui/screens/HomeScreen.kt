package com.example.game.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun HomeScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    val freeSpinReady = viewModel.canSpinForFree()
    val todayEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    val dailyGiftReady = todayEpoch > saveState.lastDailyClaimDay

    val transition = rememberInfiniteTransition(label = "bubble_pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF2E1065)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: Coins + Total Stars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinPill(
                    coins = saveState.playerCoins,
                    onClick = { viewModel.navigateTo(GameScreen.SHOP) }
                )

                Row(
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF312E81).copy(alpha = 0.85f))
                        .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${saveState.levelStars.values.sum()} Stars",
                        color = Color(0xFFFDE68A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Main Scrollable Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Hero Castle Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .shadow(12.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF4338CA),
                                    Color(0xFF312E81),
                                    Color(0xFF1E1B4B)
                                )
                            )
                        )
                        .border(2.dp, Color(0xFF818CF8).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                        .padding(vertical = 18.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "👑 BUBBLE KINGDOM 👑",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFDE047),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE68A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🐶", fontSize = 46.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Level ${saveState.unlockedLevel} / 100",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "🐾 ${saveState.rescuedPets.size} Pets Rescued",
                            fontSize = 13.sp,
                            color = Color(0xFFC7D2FE),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Left Side Action Bubbles (Spin + Gift) + Big Play Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SideActionBubble(
                            emoji = "🎡",
                            label = "Wheel",
                            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                            hasNotification = freeSpinReady,
                            notificationText = "FREE",
                            scale = if (freeSpinReady) pulseScale else 1.0f,
                            onClick = { viewModel.navigateTo(GameScreen.LUCKY_SPIN) },
                            testTag = "side_bubble_spin"
                        )

                        SideActionBubble(
                            emoji = "🎁",
                            label = "Gift",
                            gradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                            hasNotification = dailyGiftReady,
                            notificationText = "NEW",
                            scale = if (dailyGiftReady) pulseScale else 1.0f,
                            onClick = { viewModel.navigateTo(GameScreen.DAILY_REWARDS) },
                            testTag = "side_bubble_daily"
                        )
                    }

                    GlossyButton(
                        text = "PLAY LEVEL ${saveState.unlockedLevel}",
                        icon = "▶",
                        onClick = { viewModel.startLevel(saveState.unlockedLevel) },
                        colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp),
                        testTag = "play_level_btn"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Featured Royal Events Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFD946EF),
                                    Color(0xFF9333EA),
                                    Color(0xFF6B21A8)
                                )
                            )
                        )
                        .border(2.dp, Color(0xFFFDE047), RoundedCornerShape(24.dp))
                        .clickable { viewModel.navigateTo(GameScreen.EVENTS) }
                        .padding(18.dp)
                        .testTag("home_events_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👑", fontSize = 28.sp)
                            }

                            Column {
                                Text(
                                    text = "ROYAL EVENTS • ইভেন্ট",
                                    color = Color(0xFFFEF08A),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "🐾 Pet Sanctuary • 🏆 Achievements",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFDE047))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ENTER",
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF1E1B4B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 3. Modern Bottom Navigation Bar
            HomeBottomNavBar(
                onMapClick = { viewModel.navigateTo(GameScreen.WORLD_MAP) },
                onShopClick = { viewModel.navigateTo(GameScreen.SHOP) },
                onSettingsClick = { viewModel.navigateTo(GameScreen.SETTINGS) }
            )
        }
    }
}

@Composable
fun SideActionBubble(
    emoji: String,
    label: String,
    gradient: List<Color>,
    hasNotification: Boolean,
    notificationText: String,
    scale: Float,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.radialGradient(gradient))
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 26.sp)

            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444))
                        .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = notificationText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun HomeBottomNavBar(
    onMapClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp)
            .background(Color(0xFF1E1B4B).copy(alpha = 0.96f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF818CF8).copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavButton(
                icon = Icons.Default.Map,
                label = "World Map",
                badgeColor = Color(0xFF3B82F6),
                onClick = onMapClick,
                testTag = "nav_world_map"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                BottomNavButton(
                    icon = Icons.Default.ShoppingBag,
                    label = "Shop",
                    badgeColor = Color(0xFF8B5CF6),
                    onClick = onShopClick,
                    testTag = "nav_booster_shop"
                )

                BottomNavButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    badgeColor = Color(0xFF64748B),
                    onClick = onSettingsClick,
                    testTag = "nav_settings"
                )
            }
        }
    }
}

@Composable
fun BottomNavButton(
    icon: ImageVector,
    label: String,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.25f))
                .border(1.5.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
