package com.example.game.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.BoosterType
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun BoosterShopScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    val cooldownMs = 24 * 60 * 60 * 1000L
    var remainingMs by remember { mutableStateOf(0L) }

    // ২৪ ঘণ্টার লাইভ কাউন্টডাউন টাইমার
    LaunchedEffect(saveState.lastChestClaimTimeMs) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = now - saveState.lastChestClaimTimeMs
            remainingMs = (cooldownMs - elapsed).coerceAtLeast(0L)
            delay(1000L)
        }
    }

    val canClaimChest = remainingMs <= 0L

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

            // 3D Royal Coin Chest Banner with 24-Hour Timer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(10.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            if (canClaimChest) {
                                listOf(Color(0xFF065F46), Color(0xFF047857), Color(0xFF059669))
                            } else {
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            }
                        )
                    )
                    .border(
                        2.dp,
                        if (canClaimChest) Color(0xFF34D399) else Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // 3D Canvas Royal Treasure Chest (ইমোজি নয়)
                        CanvasRoyalChest(
                            isReady = canClaimChest,
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Royal Coin Chest",
                                color = if (canClaimChest) Color(0xFFFDE047) else Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (canClaimChest) {
                                    "Claim +200 free coins for power-ups!"
                                } else {
                                    "Next chest in: ${formatTimer(remainingMs)}"
                                },
                                color = if (canClaimChest) Color(0xFFD1FAE5) else Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (canClaimChest) {
                        GlossyButton(
                            text = "CLAIM",
                            onClick = {
                                viewModel.claimFreeChest()
                            },
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                            testTag = "free_coins_btn"
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF334155))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "⏳ ${formatTimer(remainingMs)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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

/**
 * 3D ক্যানভাসে ড্র করা রয়্যাল ট্রেজার চেস্ট (Royal Treasure Chest)
 */
@Composable
fun CanvasRoyalChest(
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Drop shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(4f, h - 8f),
            size = Size(w - 8f, 8f)
        )

        val chestW = w - 8f
        val chestH = h - 18f
        val chestLeft = 4f
        val chestTop = 14f

        // 3D Chest Body
        drawRoundRect(
            brush = Brush.verticalGradient(
                if (isReady) {
                    listOf(Color(0xFFD97706), Color(0xFF92400E), Color(0xFF451A03))
                } else {
                    listOf(Color(0xFF475569), Color(0xFF334155), Color(0xFF1E293B))
                }
            ),
            topLeft = Offset(chestLeft, chestTop + 8f),
            size = Size(chestW, chestH - 8f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Arched Chest Lid
        val lidPath = Path().apply {
            moveTo(chestLeft - 2f, chestTop + 8f)
            cubicTo(
                chestLeft - 2f, chestTop - 4f,
                chestLeft + chestW + 2f, chestTop - 4f,
                chestLeft + chestW + 2f, chestTop + 8f
            )
            close()
        }
        drawPath(
            lidPath,
            brush = Brush.verticalGradient(
                if (isReady) {
                    listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFF78350F))
                } else {
                    listOf(Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF334155))
                }
            )
        )

        // Metallic Gold Straps
        val strapW = 6f
        val strap1X = chestLeft + chestW * 0.22f
        val strap2X = chestLeft + chestW * 0.72f

        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFF9C4), Color(0xFFB45309))),
            topLeft = Offset(strap1X, chestTop),
            size = Size(strapW, chestH)
        )
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFF9C4), Color(0xFFB45309))),
            topLeft = Offset(strap2X, chestTop),
            size = Size(strapW, chestH)
        )

        // Center Gold Keyhole Lock Clasp
        val lockCenter = Offset(w / 2f, chestTop + 10f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFF9C4), Color(0xFFF59E0B), Color(0xFF78350F)),
                center = lockCenter,
                radius = 7f
            ),
            radius = 7f,
            center = lockCenter
        )
        drawCircle(color = Color(0xFF1E1B4B), radius = 2f, center = lockCenter)
    }
}

fun formatTimer(ms: Long): String {
    val totalSec = ms / 1000
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF1E293B))
            .border(1.5.dp, Color(0xFF4C1D95), RoundedCornerShape(22.dp))
            .padding(16.dp)
            .testTag("shop_item_${booster.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            GlossyButton(
                text = "🪙 ${booster.costCoins}",
                onClick = onBuy,
                colors = if (canAfford) listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) else listOf(Color(0xFF475569), Color(0xFF334155)),
                testTag = "buy_${booster.name.lowercase()}"
            )
        }
    }
}
