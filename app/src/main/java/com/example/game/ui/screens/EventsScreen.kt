package com.example.game.ui.screens

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.CoinPill
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun EventsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF3B0764)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("events_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                CoinPill(coins = saveState.playerCoins)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    EventHubCard(
                        title = "Pet Sanctuary",
                        subtitle = "Rescue cute furry companions and boost their magical perks!",
                        statusText = "🐾 ${saveState.rescuedPets.size} Rescued",
                        icon = Icons.Default.Pets,
                        gradient = listOf(Color(0xFFEC4899), Color(0xFFBE185D)),
                        badgeColor = Color(0xFFF472B6),
                        onClick = { viewModel.navigateTo(GameScreen.PET_SANCTUARY) },
                        testTag = "event_pet_sanctuary"
                    )
                }

                item {
                    EventHubCard(
                        title = "Royal Achievements",
                        subtitle = "Conquer levels, unlock trophies, and collect bonus coins!",
                        statusText = "🏆 ${saveState.completedAchievements.size} / ${AchievementRegistry.ALL.size} Badges",
                        icon = Icons.Default.EmojiEvents,
                        gradient = listOf(Color(0xFF0D9488), Color(0xFF0F766E)),
                        badgeColor = Color(0xFF2DD4BF),
                        onClick = { viewModel.navigateTo(GameScreen.ACHIEVEMENTS) },
                        testTag = "event_achievements"
                    )
                }

                item {
                    UpcomingEventCard(
                        title = "Star Tournament",
                        subtitle = "Climb the leaderboard by scoring 3-stars across all worlds!",
                        iconEmoji = "⭐",
                        gradient = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                        unlockNote = "Starting Soon • Next Event"
                    )
                }

                item {
                    UpcomingEventCard(
                        title = "Rainbow Blitz Rush",
                        subtitle = "Timed combo challenge with endless wild bubbles and jackpot rewards!",
                        iconEmoji = "🌈",
                        gradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                        unlockNote = "Coming Soon • Special Quest"
                    )
                }
            }
        }
    }
}

@Composable
fun EventHubCard(
    title: String,
    subtitle: String,
    statusText: String,
    icon: ImageVector,
    gradient: List<Color>,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(gradient))
            .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(18.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color(0xFFFEF08A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun UpcomingEventCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    gradient: List<Color>,
    unlockNote: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(gradient.map { it.copy(alpha = 0.75f) }))
            .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Upcoming",
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                )
                Text(
                    text = "🔒 $unlockNote",
                    color = Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
