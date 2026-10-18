package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

data class DailyGiftItem(
    val day: Int,
    val rewardText: String,
    val iconEmoji: String,
    val bonusBooster: String? = null
)

@Composable
fun DailyRewardsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    val gifts = listOf(
        DailyGiftItem(1, "100 Coins", "🪙"),
        DailyGiftItem(2, "200 Coins", "🪙"),
        DailyGiftItem(3, "300 Coins + Bomb", "💣", "Bomb Bubble"),
        DailyGiftItem(4, "400 Coins", "🪙"),
        DailyGiftItem(5, "500 Coins + Rainbow", "🌈", "Rainbow Magic"),
        DailyGiftItem(6, "600 Coins", "🪙"),
        DailyGiftItem(7, "1000 Coins + Zap", "⚡", "Lightning Zap")
    )

    val todayEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    val canClaimToday = todayEpoch > saveState.lastDailyClaimDay
    val streak = saveState.dailyRewardStreak

    // Accurate Claim Status Calculation (Fixed Off-By-One Bug)
    val activeClaimIdx = if (canClaimToday) (streak % 7) else -1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF431407)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("daily_rewards_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("daily_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "DAILY REWARDS",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Badge
            Text(
                text = "🔥 $streak-Day Streak!",
                color = Color(0xFFFB923C),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Log in daily to claim coins and exclusive boosters!",
                color = Color(0xFFFED7AA),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // 7 Days Grid with Fixed Single-Day Selection
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(gifts) { idx, gift ->
                    // Exact mathematical claim status:
                    val isClaimed = if (canClaimToday) {
                        idx < (streak % 7)
                    } else {
                        // If already claimed today, only days up to streak have checkmarks!
                        idx < (((streak - 1).coerceAtLeast(0) % 7) + 1)
                    }
                    val isCurrent = (idx == activeClaimIdx)

                    DailyCard(
                        gift = gift,
                        isClaimed = isClaimed,
                        isCurrent = isCurrent
                    )
                }
            }

            // Claim Action Button
            val nextDayNum = (streak % 7) + 1
            GlossyButton(
                text = if (canClaimToday) "CLAIM DAY $nextDayNum GIFT! 🎁" else "COME BACK TOMORROW ✨",
                onClick = {
                    if (canClaimToday) {
                        viewModel.claimDailyReward()
                    }
                },
                colors = if (canClaimToday) listOf(Color(0xFFF59E0B), Color(0xFFD97706)) else listOf(Color(0xFF475569), Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                testTag = "claim_daily_btn"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DailyCard(
    gift: DailyGiftItem,
    isClaimed: Boolean,
    isCurrent: Boolean
) {
    Box(
        modifier = Modifier
            .shadow(if (isCurrent) 10.dp else 3.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                when {
                    isCurrent -> Brush.verticalGradient(
                        listOf(Color(0xFFF59E0B), Color(0xFFB45309))
                    )
                    isClaimed -> Brush.verticalGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                    else -> Brush.verticalGradient(
                        listOf(Color(0xFF1E1B4B).copy(alpha = 0.85f), Color(0xFF0F172A))
                    )
                }
            )
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = when {
                    isCurrent -> Color(0xFFFDE047)
                    isClaimed -> Color(0xFF10B981).copy(alpha = 0.6f)
                    else -> Color.White.copy(alpha = 0.15f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
            .testTag("daily_card_${gift.day}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Day ${gift.day}",
                color = if (isCurrent) Color(0xFFFEF3C7) else Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = gift.iconEmoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = gift.rewardText,
                color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isClaimed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Claimed",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " Done",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (!isCurrent) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
