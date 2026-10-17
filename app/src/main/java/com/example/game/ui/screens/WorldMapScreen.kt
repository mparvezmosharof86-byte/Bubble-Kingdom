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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.game.levels.LevelDatabase
import com.example.game.levels.LevelObjective
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.StarRatingBar
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun WorldMapScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    var selectedBiome by remember {
        mutableIntStateOf(
            when {
                saveState.unlockedLevel <= 35 -> 0
                saveState.unlockedLevel <= 70 -> 1
                else -> 2
            }
        )
    }

    val biomes = listOf(
        Triple("Emerald Forest", 1..35, Color(0xFF10B981)),
        Triple("Candy Meadows", 36..70, Color(0xFFEC4899)),
        Triple("Celestial Islands", 71..100, Color(0xFF38BDF8))
    )

    val currentBiome = biomes[selectedBiome]
    val levelsForBiome = currentBiome.second.toList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("world_map_screen")
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
                        .testTag("back_to_home")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "WORLD MAP",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            // Biome Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedBiome,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                edgePadding = 16.dp,
                divider = {}
            ) {
                biomes.forEachIndexed { index, (name, range, color) ->
                    val isSelected = selectedBiome == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedBiome = index },
                        text = {
                            Text(
                                text = "$name (${range.first}-${range.last})",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) color else Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level Nodes Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(levelsForBiome) { levelNum ->
                    val isUnlocked = levelNum <= saveState.unlockedLevel
                    val isCurrent = levelNum == saveState.unlockedLevel
                    val stars = saveState.levelStars[levelNum] ?: 0
                    val levelDef = remember(levelNum) { LevelDatabase.getLevel(levelNum) }
                    val isPetLevel = levelDef.objective == LevelObjective.RESCUE_PETS

                    LevelNodeItem(
                        levelNum = levelNum,
                        isUnlocked = isUnlocked,
                        isCurrent = isCurrent,
                        stars = stars,
                        isPetLevel = isPetLevel,
                        biomeColor = currentBiome.third,
                        onClick = {
                            if (isUnlocked) {
                                viewModel.startLevel(levelNum)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelNodeItem(
    levelNum: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    stars: Int,
    isPetLevel: Boolean,
    biomeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_node_$levelNum")
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(if (isCurrent) 10.dp else 4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> Brush.radialGradient(
                            listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFD97706))
                        )
                        isUnlocked -> Brush.radialGradient(
                            listOf(biomeColor, biomeColor.copy(alpha = 0.7f))
                        )
                        else -> Brush.radialGradient(
                            listOf(Color(0xFF374151), Color(0xFF1F2937))
                        )
                    }
                )
                .border(
                    width = if (isCurrent) 3.dp else 1.5.dp,
                    color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isPetLevel) {
                        Text(text = "🐾", fontSize = 10.sp)
                    }
                    Text(
                        text = "$levelNum",
                        color = if (isCurrent) Color(0xFF1E1B4B) else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Stars earned
        if (isUnlocked) {
            StarRatingBar(stars = stars, starSize = 12)
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
