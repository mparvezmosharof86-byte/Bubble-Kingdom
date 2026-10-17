package com.example.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.BoosterType
import com.example.game.ui.components.CoinPill
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SpinReward(
    val title: String,
    val iconEmoji: String,
    val coins: Int,
    val booster: BoosterType?,
    val sliceColor: Color,
    val textColor: Color = Color.White
)

val LUCKY_SPIN_REWARDS = listOf(
    SpinReward("100 Coins", "🪙", 100, null, Color(0xFFF59E0B)),
    SpinReward("Bomb", "💣", 0, BoosterType.BOMB_BUBBLE, Color(0xFF8B5CF6)),
    SpinReward("250 Coins", "🪙", 250, null, Color(0xFF06B6D4)),
    SpinReward("Rainbow", "🌈", 0, BoosterType.RAINBOW_BUBBLE, Color(0xFFEC4899)),
    SpinReward("500 Coins", "🪙", 500, null, Color(0xFF10B981)),
    SpinReward("Lightning", "⚡", 0, BoosterType.LIGHTNING_BUBBLE, Color(0xFF3B82F6)),
    SpinReward("+5 Shots", "🏹", 0, BoosterType.EXTRA_SHOTS, Color(0xFF6366F1)),
    SpinReward("JACKPOT!", "👑", 1000, BoosterType.SUPER_AIM, Color(0xFFEAB308), Color(0xFF1E1B4B))
)

@Composable
fun LuckySpinScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    val scope = rememberCoroutineScope()

    var isSpinning by remember { mutableStateOf(false) }
    var winningReward by remember { mutableStateOf<SpinReward?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }
    val freeSpinAvailable = viewModel.canSpinForFree()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF311042)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("lucky_spin_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        .testTag("spin_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "LUCKY WHEEL",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "✨ Spin & Win Daily Treasures! ✨",
                color = Color(0xFFE0E7FF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wheel Container with Canvas
            Box(
                modifier = Modifier
                    .size(330.dp)
                    .shadow(16.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Rotating Wheel Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2f, h / 2f)
                    val radius = (w / 2f) - 12f
                    val sliceAngle = 360f / LUCKY_SPIN_REWARDS.size

                    rotate(rotationAnim.value, pivot = center) {
                        // 1. Draw outer rim with metallic gold border
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFDE047), Color(0xFFD97706), Color(0xFF78350F)),
                                center = center,
                                radius = radius + 12f
                            ),
                            radius = radius + 10f,
                            center = center
                        )

                        // 2. Draw 8 Slices
                        for (i in LUCKY_SPIN_REWARDS.indices) {
                            val reward = LUCKY_SPIN_REWARDS[i]
                            val startAngle = i * sliceAngle - (sliceAngle / 2f)

                            drawArc(
                                color = reward.sliceColor,
                                startAngle = startAngle,
                                sweepAngle = sliceAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )

                            // Inner slice divider line
                            val lineAngleRad = Math.toRadians(startAngle.toDouble())
                            val endX = center.x + (cos(lineAngleRad) * radius).toFloat()
                            val endY = center.y + (sin(lineAngleRad) * radius).toFloat()
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 2.5f
                            )
                        }

                        // 3. Draw Reward Labels & Emojis on each slice
                        for (i in LUCKY_SPIN_REWARDS.indices) {
                            val reward = LUCKY_SPIN_REWARDS[i]
                            val midAngle = i * sliceAngle
                            val textRadius = radius * 0.65f
                            val rad = Math.toRadians(midAngle.toDouble())
                            val tx = center.x + (cos(rad) * textRadius).toFloat()
                            val ty = center.y + (sin(rad) * textRadius).toFloat()

                            try {
                                val paint = android.graphics.Paint().apply {
                                    this.textSize = 28f
                                    this.textAlign = android.graphics.Paint.Align.CENTER
                                    this.isFakeBoldText = true
                                    this.color = android.graphics.Color.WHITE
                                    this.setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
                                }
                                val label = "${reward.iconEmoji}\n${reward.title}"
                                val lines = label.split("\n")
                                drawContext.canvas.nativeCanvas.save()
                                drawContext.canvas.nativeCanvas.rotate(midAngle + 90f, tx, ty)
                                drawContext.canvas.nativeCanvas.drawText(lines[0], tx, ty - 6f, paint)
                                paint.textSize = 20f
                                if (lines.size > 1) {
                                    drawContext.canvas.nativeCanvas.drawText(lines[1], tx, ty + 20f, paint)
                                }
                                drawContext.canvas.nativeCanvas.restore()
                            } catch (_: Throwable) {}
                        }

                        // Outer Studs / Light bulbs
                        val numStuds = 16
                        for (j in 0 until numStuds) {
                            val studAngleRad = Math.toRadians((j * 360.0 / numStuds))
                            val sx = center.x + (cos(studAngleRad) * (radius + 4f)).toFloat()
                            val sy = center.y + (sin(studAngleRad) * (radius + 4f)).toFloat()
                            drawCircle(
                                color = if (j % 2 == 0) Color.White else Color(0xFFFDE047),
                                radius = 4f,
                                center = Offset(sx, sy)
                            )
                        }
                    }

                    // 4. Draw Center Golden Hub (Fixed / unrotated)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFDE047), Color(0xFFB45309)),
                            center = center,
                            radius = 42f
                        ),
                        radius = 42f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 42f,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    // 5. Golden Pointer Flapper at Top
                    val pointer = Path().apply {
                        moveTo(center.x, 2f)
                        lineTo(center.x - 16f, -18f)
                        lineTo(center.x + 16f, -18f)
                        close()
                    }
                    drawPath(pointer, Color(0xFFEF4444))
                    drawPath(pointer, Color.White, style = Stroke(width = 2.5f))
                }

                // Center Spin Tap Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isSpinning) {
                            if (!isSpinning) {
                                val canSpin = freeSpinAvailable || saveState.playerCoins >= 50
                                if (canSpin) {
                                    if (!freeSpinAvailable) {
                                        viewModel.spendCoins(50)
                                    }
                                    isSpinning = true
                                    val winningIndex = Random.nextInt(LUCKY_SPIN_REWARDS.size)
                                    winningReward = LUCKY_SPIN_REWARDS[winningIndex]

                                    val sliceAngle = 360f / LUCKY_SPIN_REWARDS.size
                                    // Pointer is at -90° (top)
                                    val targetSliceAngle = (LUCKY_SPIN_REWARDS.size - winningIndex) * sliceAngle - 90f
                                    val fullRotations = 360f * 6
                                    val targetTotalRotation = rotationAnim.value + fullRotations + targetSliceAngle

                                    scope.launch {
                                        rotationAnim.animateTo(
                                            targetValue = targetTotalRotation,
                                            animationSpec = tween(
                                                durationMillis = 4200,
                                                easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1.0f)
                                            )
                                        )
                                        isSpinning = false
                                        val reward = LUCKY_SPIN_REWARDS[winningIndex]
                                        viewModel.spinLuckyWheel(reward.coins, reward.booster)
                                        showWinDialog = true
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "👑",
                            fontSize = 20.sp
                        )
                        Text(
                            text = "SPIN",
                            color = Color(0xFF1E1B4B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Spin Action Button
            val spinButtonText = when {
                isSpinning -> "SPINNING..."
                freeSpinAvailable -> "FREE SPIN! 🎁"
                saveState.playerCoins >= 50 -> "SPIN FOR 🪙 50"
                else -> "NEED 🪙 50 (Shop)"
            }

            GlossyButton(
                text = spinButtonText,
                onClick = {
                    if (!isSpinning) {
                        val canSpin = freeSpinAvailable || saveState.playerCoins >= 50
                        if (canSpin) {
                            if (!freeSpinAvailable) {
                                viewModel.spendCoins(50)
                            }
                            isSpinning = true
                            val winningIndex = Random.nextInt(LUCKY_SPIN_REWARDS.size)
                            winningReward = LUCKY_SPIN_REWARDS[winningIndex]

                            val sliceAngle = 360f / LUCKY_SPIN_REWARDS.size
                            val targetSliceAngle = (LUCKY_SPIN_REWARDS.size - winningIndex) * sliceAngle - 90f
                            val fullRotations = 360f * 6
                            val targetTotalRotation = rotationAnim.value + fullRotations + targetSliceAngle

                            scope.launch {
                                rotationAnim.animateTo(
                                    targetValue = targetTotalRotation,
                                    animationSpec = tween(
                                        durationMillis = 4200,
                                        easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1.0f)
                                    )
                                )
                                isSpinning = false
                                val reward = LUCKY_SPIN_REWARDS[winningIndex]
                                viewModel.spinLuckyWheel(reward.coins, reward.booster)
                                showWinDialog = true
                            }
                        } else {
                            viewModel.navigateTo(GameScreen.SHOP)
                        }
                    }
                },
                colors = if (freeSpinAvailable) listOf(Color(0xFF10B981), Color(0xFF047857)) else listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(60.dp),
                testTag = "spin_wheel_action_btn"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (freeSpinAvailable) "🎉 Free daily spin ready!" else "1 Free spin resets daily • Extra spins cost 50 coins",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        // Win Celebration Dialog
        AnimatedVisibility(
            visible = showWinDialog,
            enter = fadeIn(),
            exit = fadeOut()
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
                        .padding(24.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
                            )
                        )
                        .border(2.dp, Color(0xFFFDE047), RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎉 CONGRATULATIONS! 🎉",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFDE047)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = winningReward?.iconEmoji ?: "🎁",
                            fontSize = 64.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "YOU WON",
                            fontSize = 14.sp,
                            color = Color(0xFFC7D2FE),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = winningReward?.title ?: "Great Prize!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GlossyButton(
                            text = "CLAIM REWARD",
                            onClick = { showWinDialog = false },
                            colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "claim_spin_reward_btn"
                        )
                    }
                }
            }
        }
    }
}
