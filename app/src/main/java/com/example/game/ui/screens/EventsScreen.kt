package com.example.game.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.game.model.BoosterType
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun EventsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    var showStarTournamentModal by remember { mutableStateOf(false) }
    var showRainbowBlitzModal by remember { mutableStateOf(false) }

    val totalStars = saveState.levelStars.values.sum()
    val tournamentTargetStars = 30
    val starProgress = (totalStars.toFloat() / tournamentTargetStars).coerceIn(0f, 1f)

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
            .testTag("events_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("events_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "👑 KINGDOM EVENTS 👑",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                CoinPill(coins = saveState.playerCoins)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2. EVENTS LIST
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // EVENT 1: PET SANCTUARY
                item {
                    ThreeDEventHubCard(
                        title = "Pet Sanctuary",
                        subtitle = "Rescue cute furry companions and boost their magical perks!",
                        statusTag = "🐾 ${saveState.rescuedPets.size} RESCUED",
                        tagColor = Color(0xFFF472B6),
                        icon = Icons.Default.Pets,
                        gradient = listOf(Color(0xFFBE185D), Color(0xFF831843)),
                        borderColor = Color(0xFFF472B6),
                        buttonText = "ENTER SANCTUARY ▶",
                        onClick = { viewModel.navigateTo(GameScreen.PET_SANCTUARY) }
                    )
                }

                // EVENT 2: ROYAL ACHIEVEMENTS
                item {
                    ThreeDEventHubCard(
                        title = "Royal Achievements",
                        subtitle = "Conquer levels, unlock trophies, and collect bonus coins!",
                        statusTag = "🏆 ${saveState.completedAchievements.size} / ${AchievementRegistry.ALL.size} TROPHIES",
                        tagColor = Color(0xFF2DD4BF),
                        icon = Icons.Default.EmojiEvents,
                        gradient = listOf(Color(0xFF0F766E), Color(0xFF134E4A)),
                        borderColor = Color(0xFF2DD4BF),
                        buttonText = "VIEW TROPHIES ▶",
                        onClick = { viewModel.navigateTo(GameScreen.ACHIEVEMENTS) }
                    )
                }

                // EVENT 3: STAR TOURNAMENT (3D READY EVENT)
                item {
                    ThreeDStarTournamentCard(
                        currentStars = totalStars,
                        targetStars = tournamentTargetStars,
                        progress = starProgress,
                        onClick = { showStarTournamentModal = true }
                    )
                }

                // EVENT 4: RAINBOW BLITZ RUSH (3D READY EVENT)
                item {
                    ThreeDRainbowBlitzCard(
                        onClick = { showRainbowBlitzModal = true }
                    )
                }
            }
        }

        // 3. INTERACTIVE 3D STAR TOURNAMENT MODAL
        if (showStarTournamentModal) {
            StarTournamentDialog(
                currentStars = totalStars,
                onDismiss = { showStarTournamentModal = false },
                onPlayNow = {
                    showStarTournamentModal = false
                    viewModel.navigateTo(GameScreen.WORLD_MAP)
                }
            )
        }

        // 4. INTERACTIVE 3D RAINBOW BLITZ MODAL
        if (showRainbowBlitzModal) {
            RainbowBlitzDialog(
                onDismiss = { showRainbowBlitzModal = false },
                onClaimBoost = {
                    viewModel.repository.addBooster(BoosterType.RAINBOW_BUBBLE, 2)
                    viewModel.soundManager.playPetRescueChime()
                    showRainbowBlitzModal = false
                },
                onStartBlitz = {
                    showRainbowBlitzModal = false
                    viewModel.startLevel(saveState.unlockedLevel)
                    viewModel.activateBooster(BoosterType.RAINBOW_BUBBLE)
                }
            )
        }
    }
}

@Composable
fun ThreeDStarTournamentCard(
    currentStars: Int,
    targetStars: Int,
    progress: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF4C1D95),
                        Color(0xFF2E1065),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .border(
                2.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFF78350F))
                ),
                RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CanvasStarTrophy(modifier = Modifier.size(56.dp))

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Star Tournament",
                            color = Color(0xFFFDE047),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE EVENT",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = "Score 3-stars in levels to climb the royal ranks and win 🪙 5,000 Coins!",
                        color = Color(0xFFE0E7FF),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Stars Collected: $currentStars / $targetStars",
                                color = Color(0xFFFEF3C7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                color = Color(0xFFFDE047),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1B4B))
                                .border(1.dp, Color(0xFF6366F1), CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFD97706))
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF59E0B), Color(0xFFB45309))
                        )
                    )
                    .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF1E1B4B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LEADERBOARD & PRIZES ▶",
                        color = Color(0xFF1E1B4B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeDRainbowBlitzCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF9A3412),
                        Color(0xFF7C2D12),
                        Color(0xFF431407)
                    )
                )
            )
            .border(
                2.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFEA580C), Color(0xFF7C2D12))
                ),
                RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CanvasRainbowOrb(modifier = Modifier.size(56.dp))

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rainbow Blitz Rush",
                            color = Color(0xFFFED7AA),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = "Special quest: Play with super rainbow wildcard bubbles and claim explosive rewards!",
                        color = Color(0xFFFFF7ED),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                    )

                    Text(
                        text = "🎁 Reward: 2x Free Rainbow Boosters + 🪙 1,500 Coins",
                        color = Color(0xFFFDE047),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF10B981), Color(0xFF047857))
                        )
                    )
                    .border(1.5.dp, Color(0xFFBBF7D0), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡ CLAIM BOOST & PLAY ▶",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ThreeDEventHubCard(
    title: String,
    subtitle: String,
    statusTag: String,
    tagColor: Color,
    icon: ImageVector,
    gradient: List<Color>,
    borderColor: Color,
    buttonText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.verticalGradient(gradient))
            .border(2.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(26.dp))
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusTag,
                            color = tagColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun CanvasStarTrophy(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawOval(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(4f, h - 8f),
            size = Size(w - 8f, 8f)
        )

        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFFF59D), Color(0xFFFFB300), Color(0xFF78350F))
            ),
            topLeft = Offset(w * 0.25f, h * 0.75f),
            size = Size(w * 0.5f, h * 0.18f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFFD54F), Color(0xFFD97706))
            ),
            topLeft = Offset(w * 0.42f, h * 0.55f),
            size = Size(w * 0.16f, h * 0.22f)
        )

        val cupPath = Path().apply {
            moveTo(w * 0.18f, h * 0.12f)
            lineTo(w * 0.82f, h * 0.12f)
            cubicTo(w * 0.82f, h * 0.48f, w * 0.62f, h * 0.58f, w * 0.5f, h * 0.58f)
            cubicTo(w * 0.38f, h * 0.58f, w * 0.18f, h * 0.48f, w * 0.18f, h * 0.12f)
            close()
        }
        drawPath(
            cupPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFFFDE7), Color(0xFFFFD54F), Color(0xFFF57F17), Color(0xFF78350F))
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFFFFF), Color(0xFFFFD54F)),
                center = Offset(w * 0.5f, h * 0.32f),
                radius = w * 0.16f
            ),
            radius = w * 0.16f,
            center = Offset(w * 0.5f, h * 0.32f)
        )
    }
}

@Composable
fun CanvasRainbowOrb(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f - 4f

        drawCircle(
            color = Color.Black.copy(alpha = 0.45f),
            radius = radius,
            center = Offset(center.x, center.y + 4f)
        )

        val rainbowBrush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFFEF4444),
                Color(0xFFF59E0B),
                Color(0xFF10B981),
                Color(0xFF06B6D4),
                Color(0xFF8B5CF6),
                Color(0xFFEC4899),
                Color(0xFFEF4444)
            ),
            center = center
        )
        drawCircle(brush = rainbowBrush, radius = radius, center = center)

        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = radius * 0.72f,
            center = center,
            style = Stroke(width = 3f)
        )

        drawOval(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(center.x - radius * 0.5f, center.y - radius * 0.6f),
            size = Size(radius * 0.6f, radius * 0.35f)
        )
    }
}

@Composable
fun StarTournamentDialog(
    currentStars: Int,
    onDismiss: () -> Unit,
    onPlayNow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .shadow(20.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFF0F172A))
                    )
                )
                .border(2.5.dp, Color(0xFFFDE047), RoundedCornerShape(32.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🏆 STAR TOURNAMENT 🏆",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFDE047)
                )

                Text(
                    text = "Ends in: 2 Days 14 Hours • Royal League",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LeaderboardRow(rank = "🥇 1st", name = "You (Player)", score = "$currentStars Stars", prize = "🪙 5,000 + 3 Bombs", isYou = true)
                    LeaderboardRow(rank = "🥈 2nd", name = "Princess Pippa", score = "28 Stars", prize = "🪙 2,500 + 2 Zap", isYou = false)
                    LeaderboardRow(rank = "🥉 3rd", name = "Knight Barnaby", score = "24 Stars", prize = "🪙 1,000 + 1 Rainbow", isYou = false)
                    LeaderboardRow(rank = "🎖️ 4th", name = "Sage Oliver", score = "19 Stars", prize = "🪙 500 Coins", isYou = false)
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlossyButton(
                    text = "PLAY LEVELS FOR STARS ▶",
                    onClick = onPlayNow,
                    colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    colors = listOf(Color(0xFF475569), Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: String,
    name: String,
    score: String,
    prize: String,
    isYou: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isYou) Color(0xFF4338CA) else Color(0xFF1E293B).copy(alpha = 0.8f))
            .border(1.dp, if (isYou) Color(0xFFFDE047) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = rank, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                color = if (isYou) Color(0xFFFEF08A) else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isYou) FontWeight.Black else FontWeight.Medium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = score, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = prize, color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RainbowBlitzDialog(
    onDismiss: () -> Unit,
    onClaimBoost: () -> Unit,
    onStartBlitz: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .shadow(20.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF7C2D12), Color(0xFF431407), Color(0xFF0F172A))
                    )
                )
                .border(2.5.dp, Color(0xFFFB923C), RoundedCornerShape(32.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🌈 RAINBOW BLITZ RUSH ⚡",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFED7AA)
                )

                Text(
                    text = "Special Event Quest Activated!",
                    fontSize = 13.sp,
                    color = Color(0xFFFFEDD5),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .border(1.dp, Color(0xFFF97316), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "🎁 Special Event Bonus:",
                            color = Color(0xFFFEF08A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Claim +2 Free Rainbow Bubbles instantly\n• All rainbow matches grant +250 extra combo score\n• Pop 25 rainbow bubbles to unlock Mega Chest",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlossyButton(
                    text = "CLAIM 2x RAINBOW BUBBLES 🎁",
                    onClick = onClaimBoost,
                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton(
                    text = "PLAY LEVEL WITH RAINBOW ▶",
                    onClick = onStartBlitz,
                    colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    colors = listOf(Color(0xFF475569), Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
