package com.example.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.levels.LevelObjective
import com.example.game.model.BoosterType
import com.example.game.ui.BubbleRenderer
import com.example.game.ui.components.GlossyButton
import com.example.game.ui.components.StarRatingBar
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel
import com.example.game.viewmodel.PlayState

@Composable
fun GameplayScreen(viewModel: GameViewModel) {
    val state by viewModel.gameplayState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    val virtualWidth = 720f
    val virtualHeight = 1280f
    val bubbleRadius = 45f
    val shooterX = virtualWidth / 2f
    val shooterY = 1110f

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF172554),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("gameplay_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top HUD
            TopGameHud(
                levelNum = state.currentLevel.levelNumber,
                levelTitle = state.currentLevel.title,
                shotsRemaining = state.shotsRemaining,
                score = state.score,
                objective = state.currentLevel.objective,
                rescuedPets = state.rescuedPetsInLevel.size,
                targetPets = state.currentLevel.targetPetsCount,
                onPauseClick = { viewModel.togglePause() }
            )

            // 2. Interactive Canvas Area
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("game_canvas_area")
            ) {
                val canvasW = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val canvasH = constraints.maxHeight.toFloat().coerceAtLeast(1f)
                val sX = canvasW / virtualWidth
                val sY = canvasH / virtualHeight

                @Suppress("UNUSED_VARIABLE")
                val animTick = state.animationTick

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(canvasW, canvasH) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val virtDownX = down.position.x / sX
                                val virtDownY = down.position.y / sY
                                var isAiming = false
                                if (virtDownY < shooterY - 20f) {
                                    isAiming = true
                                    viewModel.onAimUpdate(
                                        touchX = virtDownX,
                                        touchY = virtDownY,
                                        shooterX = shooterX,
                                        shooterY = shooterY,
                                        boardWidth = virtualWidth,
                                        bubbleRadius = bubbleRadius
                                    )
                                }
                                val pointerId = down.id
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                    if (!change.pressed) {
                                        if (isAiming) {
                                            viewModel.onAimRelease(shooterX, shooterY)
                                        }
                                        break
                                    }
                                    try { change.consume() } catch (_: Throwable) {}
                                    val virtX = change.position.x / sX
                                    val virtY = change.position.y / sY
                                    if (virtY < shooterY - 20f) {
                                        isAiming = true
                                        viewModel.onAimUpdate(
                                            touchX = virtX,
                                            touchY = virtY,
                                            shooterX = shooterX,
                                            shooterY = shooterY,
                                            boardWidth = virtualWidth,
                                            bubbleRadius = bubbleRadius
                                        )
                                    } else {
                                        isAiming = false
                                        viewModel.cancelAim()
                                    }
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val dangerY = (bubbleRadius + 14 * bubbleRadius * 1.732f) * sY
                        drawLine(
                            color = Color(0xFFEF4444).copy(alpha = 0.45f),
                            start = Offset(0f, dangerY),
                            end = Offset(canvasW, dangerY),
                            strokeWidth = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                        )

                        for (r in 0 until viewModel.hexGrid.maxRows) {
                            for (c in 0 until viewModel.hexGrid.getColsForRow(r)) {
                                val b = viewModel.hexGrid.getBubble(r, c)
                                if (b != null) {
                                    val (vx, vy) = viewModel.hexGrid.getCenter(r, c, bubbleRadius, virtualWidth)
                                    BubbleRenderer.drawBubble(
                                        drawScope = this,
                                        centerX = vx * sX,
                                        centerY = vy * sY,
                                        radius = bubbleRadius * sX,
                                        color = b.color,
                                        type = b.type,
                                        petId = b.petId,
                                        colorblindEnabled = saveState.colorblindEnabled
                                    )
                                }
                            }
                        }

                        if (state.playState == PlayState.AIMING && state.aimTrajectory.size >= 2) {
                            for (i in 0 until state.aimTrajectory.size - 1) {
                                val p1 = state.aimTrajectory[i]
                                val p2 = state.aimTrajectory[i + 1]
                                drawLine(
                                    color = if (state.isSuperAimActive) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.8f),
                                    start = Offset(p1.x * sX, p1.y * sY),
                                    end = Offset(p2.x * sX, p2.y * sY),
                                    strokeWidth = if (state.isSuperAimActive) 6f else 4f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                )
                            }

                            val endPoint = state.aimTrajectory.last()
                            drawCircle(
                                color = Color(0xFFFDE047).copy(alpha = 0.85f),
                                radius = bubbleRadius * sX * 0.9f,
                                center = Offset(endPoint.x * sX, endPoint.y * sY),
                                style = Stroke(width = 3f)
                            )
                        }

                        val proj = viewModel.currentProjectile
                        if (proj != null) {
                            BubbleRenderer.drawBubble(
                                drawScope = this,
                                centerX = proj.x * sX,
                                centerY = proj.y * sY,
                                radius = bubbleRadius * sX,
                                color = proj.bubble.color,
                                type = proj.bubble.type,
                                petId = proj.bubble.petId,
                                colorblindEnabled = saveState.colorblindEnabled
                            )
                        }

                        for (p in viewModel.particleSystem.particles) {
                            drawCircle(
                                color = p.color.copy(alpha = p.alpha),
                                radius = p.size * sX,
                                center = Offset(p.x * sX, p.y * sY)
                            )
                        }

                        for (fb in viewModel.particleSystem.fallingBubbles) {
                            BubbleRenderer.drawBubble(
                                drawScope = this,
                                centerX = fb.x * sX,
                                centerY = fb.y * sY,
                                radius = fb.radius * sX,
                                color = fb.color,
                                type = fb.type,
                                petId = fb.petId,
                                colorblindEnabled = saveState.colorblindEnabled
                            )
                        }

                        for (popup in viewModel.particleSystem.scorePopups) {
                            try {
                                val paint = android.graphics.Paint().apply {
                                    this.color = android.graphics.Color.argb(
                                        (255 * popup.alpha).toInt(),
                                        255, 215, 0
                                    )
                                    this.textSize = (32f * popup.scale) * sX
                                    this.isFakeBoldText = true
                                    this.textAlign = android.graphics.Paint.Align.CENTER
                                }
                                drawContext.canvas.nativeCanvas.drawText(
                                    popup.text,
                                    popup.x * sX,
                                    popup.y * sY,
                                    paint
                                )
                            } catch (_: Throwable) {}
                        }

                        drawShooterPlatform(
                            drawScope = this,
                            centerX = shooterX * sX,
                            centerY = shooterY * sY,
                            bubbleRadius = bubbleRadius * sX
                        )

                        if (state.playState != PlayState.SHOOTING) {
                            BubbleRenderer.drawBubble(
                                drawScope = this,
                                centerX = shooterX * sX,
                                centerY = shooterY * sY,
                                radius = bubbleRadius * sX,
                                color = state.currentBubble.color,
                                type = state.currentBubble.type,
                                petId = state.currentBubble.petId,
                                colorblindEnabled = saveState.colorblindEnabled
                            )
                        }

                        val nextX = (shooterX - 140f) * sX
                        val nextY = (shooterY + 20f) * sY
                        BubbleRenderer.drawBubble(
                            drawScope = this,
                            centerX = nextX,
                            centerY = nextY,
                            radius = (bubbleRadius * 0.72f) * sX,
                            color = state.nextBubble.color,
                            type = state.nextBubble.type,
                            petId = state.nextBubble.petId,
                            colorblindEnabled = saveState.colorblindEnabled
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                            .offset(x = (-45).dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6))
                            .clickable { viewModel.swapBubbles() }
                            .testTag("bubble_swap_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Bubbles",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 3. Bottom Booster Shelf With Cleared External Badges
            BoosterTray(
                saveState = saveState,
                onBoosterClick = { viewModel.activateBooster(it) }
            )
        }

        if (state.isPaused) {
            PauseDialog(
                viewModel = viewModel,
                onResume = { viewModel.togglePause() },
                onRestart = { viewModel.retryLevel() },
                onQuit = { viewModel.navigateTo(GameScreen.WORLD_MAP) }
            )
        }

        AnimatedVisibility(
            visible = state.playState == PlayState.VICTORY,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VictoryDialog(
                levelNum = state.currentLevel.levelNumber,
                score = state.score,
                stars = state.starsEarned,
                coinsWon = state.coinsWon,
                petsRescued = state.rescuedPetsInLevel.size,
                onNext = { viewModel.nextLevel() },
                onReplay = { viewModel.retryLevel() },
                onMap = { viewModel.navigateTo(GameScreen.WORLD_MAP) }
            )
        }

        AnimatedVisibility(
            visible = state.playState == PlayState.DEFEAT,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DefeatDialog(
                playerCoins = saveState.playerCoins,
                onContinueShots = { viewModel.continueWithExtraShots() },
                onRetry = { viewModel.retryLevel() },
                onMap = { viewModel.navigateTo(GameScreen.WORLD_MAP) }
            )
        }
    }
}

@Composable
fun TopGameHud(
    levelNum: Int,
    levelTitle: String,
    shotsRemaining: Int,
    score: Int,
    objective: LevelObjective,
    rescuedPets: Int,
    targetPets: Int,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .testTag("pause_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = Color.White
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LEVEL $levelNum",
                color = Color(0xFFFDE047),
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            val objectiveText = when (objective) {
                LevelObjective.RESCUE_PETS -> "🐾 Pets: $rescuedPets / $targetPets"
                LevelObjective.CLEAR_ALL -> "⭐ Clear Board"
                LevelObjective.TARGET_SCORE -> "🎯 Score: $score"
            }
            Text(
                text = objectiveText,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (shotsRemaining <= 5) Color(0xFFEF4444) else Color(0xFF3B82F6)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("shots_remaining_tag"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🏹 $shotsRemaining",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * বুস্টারের কাউন্টার নাম্বারটি বাইরে চমৎকারভাবে ভাসমান ব্যাজ আকারে দেওয়া হলো (ভেতরে ঢুকবে না)
 */
@Composable
fun BoosterTray(
    saveState: com.example.game.data.GameSaveState,
    onBoosterClick: (BoosterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.98f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val boosters = listOf(
            BoosterType.SUPER_AIM,
            BoosterType.BOMB_BUBBLE,
            BoosterType.RAINBOW_BUBBLE,
            BoosterType.LIGHTNING_BUBBLE,
            BoosterType.FIREBALL,
            BoosterType.EXTRA_SHOTS
        )

        for (booster in boosters) {
            val count = saveState.boosterInventory[booster] ?: 0
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBoosterClick(booster) }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .testTag("booster_${booster.name.lowercase()}")
            ) {
                // Outer Container for Icon + External Protruding Counter Badge
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Booster Icon Circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF334155), Color(0xFF1E293B))
                                )
                            )
                            .border(1.5.dp, Color(0xFF818CF8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = booster.iconSymbol, fontSize = 22.sp)
                    }

                    // 100% EXTERNAL 3D COUNT BADGE (সম্পূর্ণ বাইরে ভাসমান গোল্ডেন ব্যাজ)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-3).dp)
                            .size(20.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFDE047), Color(0xFFD97706))
                                )
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (count > 0) "$count" else "+",
                            color = Color(0xFF1E1B4B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Text(
                    text = booster.title.split(" ").first(),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun drawShooterPlatform(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    centerX: Float,
    centerY: Float,
    bubbleRadius: Float
) {
    drawScope.drawCircle(
        color = Color(0xFF1E1B4B),
        radius = bubbleRadius * 1.55f,
        center = Offset(centerX, centerY + bubbleRadius * 0.2f)
    )
    drawScope.drawCircle(
        color = Color(0xFF6366F1),
        radius = bubbleRadius * 1.55f,
        center = Offset(centerX, centerY + bubbleRadius * 0.2f),
        style = Stroke(width = 4f)
    )

    drawScope.drawCircle(
        color = Color(0xFF1E293B),
        radius = bubbleRadius * 0.95f,
        center = Offset(centerX - 140f * (bubbleRadius / 45f), centerY + 20f * (bubbleRadius / 45f))
    )
}

@Composable
fun VictoryDialog(
    levelNum: Int,
    score: Int,
    stars: Int,
    coinsWon: Int,
    petsRescued: Int,
    onNext: () -> Unit,
    onReplay: () -> Unit,
    onMap: () -> Unit
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
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF312E81), Color(0xFF1E1B4B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(32.dp))
                .padding(24.dp)
                .testTag("victory_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎉 LEVEL COMPLETE! 🎉",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFDE047)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StarRatingBar(stars = stars, starSize = 38)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Score: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "🪙 +$coinsWon Coins  •  🐾 $petsRescued Pets Rescued!",
                    fontSize = 14.sp,
                    color = Color(0xFFC7D2FE),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                GlossyButton(
                    text = "NEXT LEVEL ▶",
                    onClick = onNext,
                    colors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "victory_next_btn"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlossyButton(
                        text = "Replay",
                        onClick = onReplay,
                        colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                        modifier = Modifier.weight(1f),
                        testTag = "victory_replay_btn"
                    )
                    GlossyButton(
                        text = "Map",
                        onClick = onMap,
                        colors = listOf(Color(0xFF475569), Color(0xFF334155)),
                        modifier = Modifier.weight(1f),
                        testTag = "victory_map_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun DefeatDialog(
    playerCoins: Int,
    onContinueShots: () -> Unit,
    onRetry: () -> Unit,
    onMap: () -> Unit
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
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF450A0A), Color(0xFF1E1B4B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(32.dp))
                .padding(24.dp)
                .testTag("defeat_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "💔 OUT OF SHOTS 💔",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFCA5A5)
                )

                Text(
                    text = "Don't give up! Your furry friends need you!",
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )

                GlossyButton(
                    text = "+5 SHOTS (🪙 60)",
                    onClick = onContinueShots,
                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "defeat_revive_btn"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlossyButton(
                        text = "Retry",
                        onClick = onRetry,
                        colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                        modifier = Modifier.weight(1f),
                        testTag = "defeat_retry_btn"
                    )
                    GlossyButton(
                        text = "Map",
                        onClick = onMap,
                        colors = listOf(Color(0xFF475569), Color(0xFF334155)),
                        modifier = Modifier.weight(1f),
                        testTag = "defeat_map_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun PauseDialog(
    viewModel: GameViewModel,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1E1B4B))
                .border(2.dp, Color(0xFF6366F1), RoundedCornerShape(28.dp))
                .padding(24.dp)
                .testTag("pause_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "PAUSED",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                GlossyButton(
                    text = "RESUME",
                    icon = "▶",
                    onClick = onResume,
                    colors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_resume_btn"
                )

                GlossyButton(
                    text = "RESTART",
                    icon = "🔄",
                    onClick = onRestart,
                    colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_restart_btn"
                )

                GlossyButton(
                    text = "QUIT TO MAP",
                    icon = "🗺️",
                    onClick = onQuit,
                    colors = listOf(Color(0xFF475569), Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_quit_btn"
                )
            }
        }
    }
}
