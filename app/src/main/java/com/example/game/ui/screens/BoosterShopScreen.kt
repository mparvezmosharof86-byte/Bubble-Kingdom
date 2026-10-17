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
import com.example.game.model.BoosterType
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun BoosterShopScreen(viewModel: GameViewModel) {
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
            .testTag("booster_shop_screen")
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
                        .testTag("shop_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "BOOSTER SHOP",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            // Free Gift Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF047857), Color(0xFF10B981))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎁 Kingdom Coin Chest",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Claim +200 free coins for power-ups!",
                            color = Color(0xFFD1FAE5),
                            fontSize = 12.sp
                        )
                    }
                    GlossyButton(
                        text = "Claim",
                        onClick = {
                            viewModel.repository.addCoins(200)
                            viewModel.soundManager.playPetRescueChime()
                        },
                        colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                        testTag = "free_coins_btn"
                    )
                }
            }

            // Boosters List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(BoosterType.entries) { booster ->
                    val ownedCount = saveState.boosterInventory[booster] ?: 0
                    BoosterShopItem(
                        booster = booster,
                        ownedCount = ownedCount,
                        canAfford = saveState.playerCoins >= booster.costCoins,
                        onBuy = { viewModel.buyBooster(booster) }
                    )
                }
            }
        }
    }
}

@Composable
fun BoosterShopItem(
    booster: BoosterType,
    ownedCount: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF4C1D95), RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("shop_item_${booster.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF312E81))
                    .border(2.dp, Color(0xFF818CF8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = booster.iconSymbol, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = booster.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Owned: $ownedCount)",
                        color = Color(0xFFA78BFA),
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = booster.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Buy Button
            GlossyButton(
                text = "🪙 ${booster.costCoins}",
                onClick = onBuy,
                colors = if (canAfford) listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) else listOf(Color(0xFF475569), Color(0xFF334155)),
                testTag = "buy_${booster.name.lowercase()}"
            )
        }
    }
}
