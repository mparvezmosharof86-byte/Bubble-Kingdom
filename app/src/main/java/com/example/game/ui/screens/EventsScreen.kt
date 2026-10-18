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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
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

// ১৫টি আকর্ষণীয় ইভেন্ট মাইলস্টোন টাস্কের মডেল
data class StarMilestone(
    val id: Int,
    val requiredStars: Int,
    val title: String,
    val rewardCoins: Int,
    val rewardBooster: BoosterType? = null,
    val boosterName: String = "",
    val iconEmoji: String
)

val STAR_MILESTONES = listOf(
    StarMilestone(1, 3, "Bronze Scout", 100, null, "", "🪙"),
    StarMilestone(2, 6, "Bomb Cadet", 150, BoosterType.BOMB_BUBBLE, "Bomb", "💣"),
    StarMilestone(3, 10, "Super Aimer", 250, BoosterType.SUPER_AIM, "Super Aim", "🎯"),
    StarMilestone(4, 15, "Rainbow Seeker", 350, BoosterType.RAINBOW_BUBBLE, "Rainbow", "🌈"),
    StarMilestone(5, 20, "Silver Knight", 500, BoosterType.BOMB_BUBBLE, "Bomb", "🪙"),
    StarMilestone(6, 26, "Zap Striker", 600, BoosterType.LIGHTNING_BUBBLE, "Lightning", "⚡"),
    StarMilestone(7, 32, "Extra Ammo Pack", 750, BoosterType.EXTRA_SHOTS, "+5 Shots", "🏹"),
    StarMilestone(8, 40, "Fire Breather", 900, BoosterType.FIREBALL, "Fireball", "🔥"),
    StarMilestone(9, 50, "Gold Champion", 1200, BoosterType.BOMB_BUBBLE, "Bomb", "👑"),
    StarMilestone(10, 60, "Prismatic Master", 1500, BoosterType.RAINBOW_BUBBLE, "Rainbow", "🌈"),
    StarMilestone(11, 70, "Thunder Baron", 1800, BoosterType.LIGHTNING_BUBBLE, "Lightning", "⚡"),
    StarMilestone(12, 80, "Dragon Flame Pack", 2200, BoosterType.FIREBALL, "Fireball", "🔥"),
    StarMilestone(13, 90, "Royal Guardian", 3000, BoosterType.EXTRA_SHOTS, "+5 Shots", "🛡️"),
    StarMilestone(14, 95, "Crown Sovereign", 4000, BoosterType.BOMB_BUBBLE, "Bomb", "👑"),
    StarMilestone(15, 100, "Grand Kingdom Jackpot", 5000, BoosterType.RAINBOW_BUBBLE, "Rainbow", "🏆")
)

@Composable
fun EventsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    var showStarMilestonesModal by remember { mutableStateOf(false) }
    var showRainbowBlitzModal by remember { mutableStateOf(false) }

    val totalStars = saveState.levelStars.values.sum()
    val nextMilestone = STAR_MILESTONES.firstOrNull { !saveState.claimedEventMilestones.contains(it.id) } ?: STAR_MILESTONES.last()
    val starProgress = (totalStars.toFloat() / nextMilestone.requiredStars).coerceIn(0f, 1f)

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

                // EVENT 3: ROYAL STAR PASS (১৫টি মাইলস্টোন টাস্ক)
                item {
                    ThreeDStarMilestoneCard(
                        currentStars = totalStars,
                        nextMilestone = nextMilestone,
                        claimedCount = saveState.claimedEventMilestones.size,
                        totalMilestones = STAR_MILESTONES.size,
                        progress = starProgress,
                        onClick = { showStarMilestonesModal = true }
                    )
                }

                // EVENT 4: RAINBOW CARNIVAL RUSH
                item {
                    ThreeDRainbowBlitzCard(
                        onClick = { showRainbowBlitzModal = true }
                    )
                }
            }
        }

        // 3. ১৫টি মাইলস্টোন টাস্কের পূর্ণাঙ্গ ৩ডি মোডাল
        if (showStarMilestonesModal) {
            StarMilestonesDialog(
                currentStars = totalStars,
                claimedMilestones = saveState.claimedEventMilestones,
                onClaim = { milestone ->
                    viewModel.claimStarMilestone(
                        milestone.id,
                        milestone.rewardCoins,
                        milestone.rewardBooster
                    )
                },
                onDismiss = { showStarMilestonesModal = false }
            )
        }

        // 4. রেইনবো কার্নিভ্যাল কোয়েস্ট মোডাল
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

/**
 * ১৫টি মাইলস্টোনের রয়্যাল স্টার পাস কার্ড
 */
@Composable
fun ThreeDStarMilestoneCard(
    currentStars: Int,
    nextMilestone: StarMilestone,
    claimedCount: Int,
    totalMilestones: Int,
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
                            text = "Royal Star Pass",
                            color = Color(0xFFFDE047),
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
                                text = "$claimedCount / $totalMilestones TASKS",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = "Next Target: ${nextMilestone.requiredStars} Stars for 🪙 ${nextMilestone.rewardCoins} ${if (nextMilestone.boosterName.isNotEmpty()) "+ ${nextMilestone.boosterName}" else ""}!",
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
                                text = "Total Stars: $currentStars",
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
                    Text(
                        text = "VIEW 15 EVENT TASKS & CLAIM ▶",
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

/**
 * ১৫টি টাস্কের পূর্ণাঙ্গ ৩ডি মোডাল ডায়ালগ
 */
@Composable
fun StarMilestonesDialog(
    currentStars: Int,
    claimedMilestones: Set<Int>,
    onClaim: (StarMilestone) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxSize(0.85f)
                .shadow(24.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF1E1B4B))
                    )
                )
                .border(2.5.dp, Color(0xFFFDE047), RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "⭐ ROYAL STAR PASS ⭐",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFDE047),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "You Have: $currentStars Stars • Complete Tasks to Claim Prizes!",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp, bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(STAR_MILESTONES) { milestone ->
                        val isReached = currentStars >= milestone.requiredStars
                        val isClaimed = claimedMilestones.contains(milestone.id)
                        val canClaim = isReached && !isClaimed

                        MilestoneTaskRow(
                            milestone = milestone,
                            canClaim = canClaim,
                            isClaimed = isClaimed,
                            onClaim = { onClaim(milestone) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
fun MilestoneTaskRow(
    milestone: StarMilestone,
    canClaim: Boolean,
    isClaimed: Boolean,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    canClaim -> Brush.horizontalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8)))
                    isClaimed -> Brush.horizontalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))
                    else -> Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                }
            )
            .border(
                1.5.dp,
                if (canClaim) Color(0xFFFDE047) else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF312E81)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = milestone.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Task ${milestone.id}: ${milestone.requiredStars} Stars",
                        color = Color(0xFFFEF3C7),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "🪙 ${milestone.rewardCoins} ${if (milestone.boosterName.isNotEmpty()) "+ ${milestone.boosterName}" else ""}",
                    color = Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box {
            when {
                isClaimed -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF065F46))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CLAIMED",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                canClaim -> {
                    Box(
                        modifier = Modifier
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            )
                            .clickable { onClaim() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "CLAIM 🎁",
                            color = Color(0xFF1E1B4B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF334155))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LOCKED",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * রেইনবো কার্নিভ্যাল কোয়েস্ট কার্ড
 */
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
                            text = "• Claim +2 Free Rainbow Bubbles instantly\n• All rainbow matches grant +250 extra combo score\n• Complete special combo quests to win Mega Prizes!",
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
