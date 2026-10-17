package com.example.game.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun HomeScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

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
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinPill(
                    coins = saveState.playerCoins,
                    onClick = { viewModel.navigateTo(GameScreen.SHOP) }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Lucky Spin Wheel quick button
                    val freeSpin = viewModel.canSpinForFree()
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                                )
                            )
                            .clickable { viewModel.navigateTo(GameScreen.LUCKY_SPIN) }
                            .testTag("top_lucky_spin_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎡", fontSize = 22.sp)
                        if (freeSpin) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    // Daily Gift quick button
                    IconButton(
                        onClick = { viewModel.navigateTo(GameScreen.DAILY_REWARDS) },
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B))
                            .testTag("daily_gift_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Daily Gift",
                            tint = Color.White
                        )
                    }

                    // Settings button
                    IconButton(
                        onClick = { viewModel.navigateTo(GameScreen.SETTINGS) },
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFF4C1D95))
                            .testTag("settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kingdom Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "👑 BUBBLE KINGDOM 👑",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFDE047),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mascot display
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFFDE68A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐶", fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Level ${saveState.unlockedLevel} / 100",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "🐾 ${saveState.rescuedPets.size} Pets Rescued  •  ⭐ ${saveState.levelStars.values.sum()} Stars",
                        fontSize = 13.sp,
                        color = Color(0xFFC7D2FE),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big PLAY Button
            GlossyButton(
                text = "PLAY LEVEL ${saveState.unlockedLevel}",
                icon = "▶",
                onClick = { viewModel.startLevel(saveState.unlockedLevel) },
                colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                testTag = "play_level_btn"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Featured Lucky Spin Banner Card
            val freeSpinReady = viewModel.canSpinForFree()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFD97706),
                                Color(0xFFB45309),
                                Color(0xFF78350F)
                            )
                        )
                    )
                    .border(2.dp, Color(0xFFFDE047), RoundedCornerShape(24.dp))
                    .clickable { viewModel.navigateTo(GameScreen.LUCKY_SPIN) }
                    .padding(16.dp)
                    .testTag("home_lucky_spin_banner")
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
                        // Wheel icon circle
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎡", fontSize = 30.sp)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LUCKY WHEEL",
                                    color = Color(0xFFFEF08A),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (freeSpinReady) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF10B981))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "FREE SPIN",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Win Coins, Bombs & Jackpots!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFDE047))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SPIN ▶",
                            color = Color(0xFF1E1B4B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menu Hub Tiles (2x4 Grid)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MenuTile(
                        title = "World Map",
                        subtitle = "100 Levels",
                        icon = Icons.Default.Map,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.WORLD_MAP) },
                        testTag = "menu_world_map"
                    )
                    MenuTile(
                        title = "Pet Sanctuary",
                        subtitle = "${saveState.rescuedPets.size} Friends",
                        icon = Icons.Default.Pets,
                        color = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.PET_SANCTUARY) },
                        testTag = "menu_pet_sanctuary"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MenuTile(
                        title = "Lucky Wheel",
                        subtitle = if (freeSpinReady) "Free Spin Ready!" else "Win Coins & Boosts",
                        emoji = "🎡",
                        color = Color(0xFFEAB308),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.LUCKY_SPIN) },
                        testTag = "menu_lucky_spin"
                    )
                    MenuTile(
                        title = "Daily Gift",
                        subtitle = "Day ${saveState.dailyRewardStreak % 7 + 1}",
                        icon = Icons.Default.CardGiftcard,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.DAILY_REWARDS) },
                        testTag = "menu_daily_gift"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MenuTile(
                        title = "Booster Shop",
                        subtitle = "Power-ups",
                        icon = Icons.Default.ShoppingBag,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.SHOP) },
                        testTag = "menu_shop"
                    )
                    MenuTile(
                        title = "Achievements",
                        subtitle = "${saveState.completedAchievements.size} Badges",
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFF14B8A6),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.ACHIEVEMENTS) },
                        testTag = "menu_achievements"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MenuTile(
                        title = "Settings",
                        subtitle = "Audio & Options",
                        icon = Icons.Default.Settings,
                        color = Color(0xFF64748B),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(GameScreen.SETTINGS) },
                        testTag = "menu_settings"
                    )
                    Box(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun MenuTile(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    emoji: String? = null,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        color.copy(alpha = 0.85f),
                        color.copy(alpha = 0.55f)
                    )
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            } else if (emoji != null) {
                Text(text = emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}
