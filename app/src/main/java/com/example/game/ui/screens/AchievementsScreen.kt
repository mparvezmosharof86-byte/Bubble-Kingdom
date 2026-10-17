package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.CoinPill
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rewardCoins: Int
)

object AchievementRegistry {
    val ALL = listOf(
        AchievementItem(
            id = "first_win",
            title = "First Victory",
            description = "Complete your very first bubble puzzle level",
            iconEmoji = "🎯",
            rewardCoins = 100
        ),
        AchievementItem(
            id = "star_collector",
            title = "Star Collector",
            description = "Earn a perfect 3-star rating on at least 10 levels",
            iconEmoji = "⭐",
            rewardCoins = 250
        ),
        AchievementItem(
            id = "pet_whisperer",
            title = "Pet Whisperer",
            description = "Rescue 4 different trapped animal friends",
            iconEmoji = "🐾",
            rewardCoins = 300
        ),
        AchievementItem(
            id = "forest_master",
            title = "Forest Master",
            description = "Conquer all 35 levels of the Emerald Forest biome",
            iconEmoji = "🌲",
            rewardCoins = 500
        ),
        AchievementItem(
            id = "candy_king",
            title = "Candy Monarch",
            description = "Complete all 70 levels of Candy Meadows",
            iconEmoji = "🍬",
            rewardCoins = 750
        ),
        AchievementItem(
            id = "kingdom_champion",
            title = "Kingdom Champion",
            description = "Beat all 100 levels and bring peace to Bubble Kingdom!",
            iconEmoji = "👑",
            rewardCoins = 1500
        ),
        AchievementItem(
            id = "daily_devotion",
            title = "Royal Devotion",
            description = "Maintain a 7-day daily login streak",
            iconEmoji = "🔥",
            rewardCoins = 400
        )
    )
}

@Composable
fun AchievementsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF042F2E)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("achievements_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("achievements_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "ACHIEVEMENTS",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Completed Counter
            val completedCount = saveState.completedAchievements.size
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color(0xFF14B8A6).copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF14B8A6), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "🏆 Unlocked $completedCount / ${AchievementRegistry.ALL.size} Trophies",
                    color = Color(0xFF99F6E4),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AchievementRegistry.ALL) { ach ->
                    val isCompleted = saveState.completedAchievements.contains(ach.id)
                    AchievementCard(achievement = ach, isCompleted = isCompleted)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: AchievementItem,
    isCompleted: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isCompleted) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.8f)
            )
            .border(
                1.5.dp,
                if (isCompleted) Color(0xFF10B981) else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(20.dp)
            )
            .padding(14.dp)
            .testTag("ach_card_${achievement.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Color(0xFF10B981).copy(alpha = 0.25f) else Color.DarkGray
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = achievement.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    color = if (isCompleted) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "🪙 +${achievement.rewardCoins} Coins",
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
