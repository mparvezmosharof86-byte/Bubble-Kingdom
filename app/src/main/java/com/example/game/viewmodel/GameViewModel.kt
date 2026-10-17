package com.example.game.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.audio.SoundManager
import com.example.game.audio.VoiceManager
import com.example.game.data.GameRepository
import com.example.game.data.GameSaveState
import com.example.game.engine.GridCoord
import com.example.game.engine.HexGrid
import com.example.game.engine.GameBubble
import com.example.game.engine.ParticleSystem
import com.example.game.engine.TrajectoryPoint
import com.example.game.levels.LevelDatabase
import com.example.game.levels.LevelDefinition
import com.example.game.levels.LevelObjective
import com.example.game.model.BoosterType
import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class GameScreen {
    SPLASH,
    HOME,
    WORLD_MAP,
    GAMEPLAY,
    PET_SANCTUARY,
    SHOP,
    ACHIEVEMENTS,
    DAILY_REWARDS,
    LUCKY_SPIN,
    SETTINGS,
    EVENTS // নতুন ইভেন্ট স্ক্রিন
}

enum class PlayState {
    READY,
    AIMING,
    SHOOTING,
    RESOLVING,
    VICTORY,
    DEFEAT,
    PAUSED
}

data class Projectile(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val bubble: GameBubble,
    val boosterType: BoosterType? = null
)

data class GameplayUiState(
    val currentLevel: LevelDefinition = LevelDatabase.getLevel(1),
    val playState: PlayState = PlayState.READY,
    val shotsRemaining: Int = 30,
    val score: Int = 0,
    val starsEarned: Int = 0,
    val comboMultiplier: Int = 1,
    val rescuedPetsInLevel: List<String> = emptyList(),
    val currentBubble: GameBubble = GameBubble(1L, BubbleColor.RED),
    val nextBubble: GameBubble = GameBubble(2L, BubbleColor.BLUE),
    val aimTrajectory: List<TrajectoryPoint> = emptyList(),
    val targetCell: GridCoord? = null,
    val activeBooster: BoosterType? = null,
    val isSuperAimActive: Boolean = false,
    val isPaused: Boolean = false,
    val coinsWon: Int = 0,
    val animationTick: Long = 0L
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository(application)
    val soundManager = SoundManager(application)
    val voiceManager = VoiceManager(application)
    val particleSystem = ParticleSystem()

    private val _saveState = MutableStateFlow(repository.getState())
    val saveState: StateFlow<GameSaveState> = _saveState.asStateFlow()

    private val _currentScreen = MutableStateFlow(GameScreen.SPLASH)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _gameplayState = MutableStateFlow(GameplayUiState())
    val gameplayState: StateFlow<GameplayUiState> = _gameplayState.asStateFlow()

    val hexGrid = HexGrid(columnsEven = 8, columnsOdd = 7, maxRows = 18)

    var currentProjectile: Projectile? = null
    private var gameLoopJob: Job? = null
    private var nextBubbleId = 100L

    init {
        soundManager.sfxVolume = _saveState.value.sfxVolume
        soundManager.musicVolume = _saveState.value.musicVolume
        soundManager.hapticsEnabled = _saveState.value.hapticsEnabled
        voiceManager.voiceVolume = _saveState.value.voiceVolume
        soundManager.startBackgroundMusic()
    }

    fun navigateTo(screen: GameScreen) {
        _currentScreen.value = screen
        if (screen == GameScreen.GAMEPLAY) {
            voiceManager.onGameStart()
        }
    }

    fun startLevel(levelNum: Int) {
        val levelDef = LevelDatabase.getLevel(levelNum)
        hexGrid.clear()
        particleSystem.clear()
        currentProjectile = null

        for (r in 0 until levelDef.grid.size) {
            val row = levelDef.grid[r]
            for (c in 0 until row.size) {
                val cell = row[c]
                if (cell != null) {
                    hexGrid.setBubble(
                        r, c,
                        GameBubble(
                            id = nextBubbleId++,
                            color = cell.color,
                            type = cell.type,
                            petId = cell.petId
                        )
                    )
                }
            }
        }

        val firstBubble = generateShooterBubble(levelDef)
        val secondBubble = generateShooterBubble(levelDef)

        _gameplayState.value = GameplayUiState(
            currentLevel = levelDef,
            playState = PlayState.READY,
            shotsRemaining = levelDef.shotsAllowed,
            score = 0,
            starsEarned = 0,
            comboMultiplier = 1,
            rescuedPetsInLevel = emptyList(),
            currentBubble = firstBubble,
            nextBubble = secondBubble,
            aimTrajectory = emptyList(),
            activeBooster = null,
            isSuperAimActive = false,
            isPaused = false
        )

        _currentScreen.value = GameScreen.GAMEPLAY
        startGameLoop()
        voiceManager.onGameStart()
    }

    private fun generateShooterBubble(levelDef: LevelDefinition): GameBubble {
        val colorsInGrid = mutableSetOf<BubbleColor>()
        for (r in 0 until hexGrid.maxRows) {
            for (c in 0 until hexGrid.getColsForRow(r)) {
                val b = hexGrid.getBubble(r, c)
                if (b != null && b.type == BubbleType.NORMAL) {
                    colorsInGrid.add(b.color)
                }
            }
        }

        val pool = if (colorsInGrid.isNotEmpty()) {
            colorsInGrid.toList()
        } else if (levelDef.allowedShooterColors.isNotEmpty()) {
            levelDef.allowedShooterColors
        } else {
            listOf(BubbleColor.RED, BubbleColor.BLUE)
        }
        val chosenColor = pool[Random.nextInt(pool.size)]
        return GameBubble(id = nextBubbleId++, color = chosenColor, type = BubbleType.NORMAL)
    }

    fun swapBubbles() {
        if (_gameplayState.value.playState != PlayState.READY && _gameplayState.value.playState != PlayState.AIMING) return
        val cur = _gameplayState.value.currentBubble
        val nxt = _gameplayState.value.nextBubble
        _gameplayState.update { it.copy(currentBubble = nxt, nextBubble = cur) }
        soundManager.playShoot()
    }

    fun activateBooster(booster: BoosterType) {
        if (_gameplayState.value.playState != PlayState.READY && _gameplayState.value.playState != PlayState.AIMING) return

        if (!repository.consumeBooster(booster)) {
            return
        }
        _saveState.value = repository.getState()
        voiceManager.onBoosterUsed(booster.title)

        when (booster) {
            BoosterType.SUPER_AIM -> {
                _gameplayState.update { it.copy(isSuperAimActive = true, activeBooster = booster) }
            }
            BoosterType.BOMB_BUBBLE -> {
                _gameplayState.update {
                    it.copy(
                        currentBubble = it.currentBubble.copy(type = BubbleType.BOMB),
                        activeBooster = booster
                    )
                }
            }
            BoosterType.RAINBOW_BUBBLE -> {
                _gameplayState.update {
                    it.copy(
                        currentBubble = it.currentBubble.copy(type = BubbleType.RAINBOW),
                        activeBooster = booster
                    )
                }
            }
            BoosterType.LIGHTNING_BUBBLE -> {
                _gameplayState.update {
                    it.copy(
                        currentBubble = it.currentBubble.copy(type = BubbleType.LIGHTNING),
                        activeBooster = booster
                    )
                }
            }
            BoosterType.FIREBALL -> {
                _gameplayState.update {
                    it.copy(
                        currentBubble = it.currentBubble.copy(type = BubbleType.FIREBALL),
                        activeBooster = booster
                    )
                }
            }
            BoosterType.EXTRA_SHOTS -> {
                _gameplayState.update {
                    it.copy(shotsRemaining = it.shotsRemaining + 5)
                }
                particleSystem.addScorePopup(
                    x = 300f,
                    y = 600f,
                    text = "+5 SHOTS!",
                    color = androidx.compose.ui.graphics.Color(0xFF10B981)
                )
            }
        }
    }

    fun onAimUpdate(touchX: Float, touchY: Float, shooterX: Float, shooterY: Float, boardWidth: Float, bubbleRadius: Float) {
        if (_gameplayState.value.playState != PlayState.READY && _gameplayState.value.playState != PlayState.AIMING) return

        val dx = touchX - shooterX
        val dy = touchY - shooterY

        if (dy >= -20f) {
            _gameplayState.update { it.copy(aimTrajectory = emptyList(), playState = PlayState.READY) }
            return
        }

        var angle = atan2(dy, dx)
        val minAngle = (-165.0 * PI / 180.0).toFloat()
        val maxAngle = (-15.0 * PI / 180.0).toFloat()
        angle = angle.coerceIn(minAngle, maxAngle)

        val maxBounces = if (_gameplayState.value.isSuperAimActive) 4 else 2
        val traj = hexGrid.calculateTrajectory(
            startX = shooterX,
            startY = shooterY,
            angleRadians = angle,
            bubbleRadius = bubbleRadius,
            boardWidth = boardWidth,
            maxBounces = maxBounces
        )

        _gameplayState.update {
            it.copy(
                playState = PlayState.AIMING,
                aimTrajectory = traj.points,
                targetCell = traj.targetCell
            )
        }
    }

    fun cancelAim() {
        if (_gameplayState.value.playState == PlayState.AIMING) {
            _gameplayState.update { it.copy(playState = PlayState.READY, aimTrajectory = emptyList()) }
        }
    }

    fun canSpinForFree(): Boolean = repository.canSpinForFree()

    fun spendCoins(amount: Int): Boolean {
        val ok = repository.spendCoins(amount)
        if (ok) {
            _saveState.value = repository.getState()
        }
        return ok
    }

    fun spinLuckyWheel(prizeCoins: Int, prizeBooster: BoosterType?) {
        val updated = repository.recordLuckySpin(prizeCoins, prizeBooster)
        _saveState.value = updated
        soundManager.playVictoryFanfare()
        voiceManager.speak("Congratulations! Wonderful prize!", priority = true)
    }

    fun onSpinTick() {
        soundManager.playHaptic(10, 80)
        soundManager.playWallBounce()
    }

    fun onAimRelease(shooterX: Float, shooterY: Float) {
        val state = _gameplayState.value
        if (state.playState != PlayState.AIMING || state.aimTrajectory.size < 2) {
            _gameplayState.update { it.copy(playState = PlayState.READY, aimTrajectory = emptyList()) }
            return
        }

        val p1 = state.aimTrajectory[1]
        val dx = p1.x - shooterX
        val dy = p1.y - shooterY
        val angle = atan2(dy, dx)
        val speed = 2100f

        currentProjectile = Projectile(
            x = shooterX,
            y = shooterY,
            vx = cos(angle) * speed,
            vy = sin(angle) * speed,
            bubble = state.currentBubble,
            boosterType = state.activeBooster
        )

        soundManager.playShoot()

        _gameplayState.update {
            it.copy(
                playState = PlayState.SHOOTING,
                shotsRemaining = it.shotsRemaining - 1,
                aimTrajectory = emptyList(),
                activeBooster = null
            )
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()

            while (isActive) {
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                if (!_gameplayState.value.isPaused) {
                    updatePhysics(dt)
                }

                delay(16)
            }
        }
    }

    private fun updatePhysics(dt: Float) {
        val screenWidth = 720f
        val screenHeight = 1280f
        val bubbleRadius = 45f

        particleSystem.update(dt, screenHeight, screenWidth) { pointsScored ->
            addScore(pointsScored)
        }

        if (currentProjectile != null || particleSystem.hasActiveParticles()) {
            _gameplayState.update { it.copy(animationTick = it.animationTick + 1) }
        }

        val proj = currentProjectile ?: return

        proj.x += proj.vx * dt
        proj.y += proj.vy * dt

        if (proj.x - bubbleRadius <= 0) {
            proj.x = bubbleRadius
            proj.vx = -proj.vx
            soundManager.playWallBounce()
        } else if (proj.x + bubbleRadius >= screenWidth) {
            proj.x = screenWidth - bubbleRadius
            proj.vx = -proj.vx
            soundManager.playWallBounce()
        }

        if (proj.boosterType == BoosterType.FIREBALL) {
            for (r in 0 until hexGrid.maxRows) {
                for (c in 0 until hexGrid.getColsForRow(r)) {
                    val b = hexGrid.getBubble(r, c)
                    if (b != null) {
                        val (cx, cy) = hexGrid.getCenter(r, c, bubbleRadius, screenWidth)
                        val distSq = (proj.x - cx) * (proj.x - cx) + (proj.y - cy) * (proj.y - cy)
                        if (distSq <= (bubbleRadius * 2.1f) * (bubbleRadius * 2.1f)) {
                            hexGrid.setBubble(r, c, null)
                            particleSystem.addPopBurst(cx, cy, b.color)
                            soundManager.playBubblePop(b.color)
                            addScore(150)
                        }
                    }
                }
            }
        }

        if (proj.y <= bubbleRadius) {
            attachAndResolve(proj, bubbleRadius, screenWidth)
            return
        }

        val collisionDistSq = (1.82f * bubbleRadius) * (1.82f * bubbleRadius)
        for (r in 0 until hexGrid.maxRows) {
            for (c in 0 until hexGrid.getColsForRow(r)) {
                if (hexGrid.getBubble(r, c) != null) {
                    val (cx, cy) = hexGrid.getCenter(r, c, bubbleRadius, screenWidth)
                    val dx = proj.x - cx
                    val dy = proj.y - cy
                    if (dx * dx + dy * dy <= collisionDistSq) {
                        attachAndResolve(proj, bubbleRadius, screenWidth)
                        return
                    }
                }
            }
        }
    }

    private fun attachAndResolve(proj: Projectile, bubbleRadius: Float, screenWidth: Float) {
        val targetCell = hexGrid.findBestAttachmentCell(proj.x, proj.y, bubbleRadius, screenWidth)
        currentProjectile = null

        val safeCell = targetCell ?: run {
            var emptyCoord: GridCoord? = null
            for (c in 0 until hexGrid.getColsForRow(0)) {
                if (hexGrid.getBubble(0, c) == null) {
                    emptyCoord = GridCoord(0, c)
                    break
                }
            }
            emptyCoord ?: GridCoord(0, 0)
        }
        hexGrid.setBubble(safeCell.row, safeCell.col, proj.bubble)
        resolveBoardMatches(safeCell.row, safeCell.col, proj.bubble, proj.boosterType, bubbleRadius, screenWidth)
    }

    private fun resolveBoardMatches(
        hitRow: Int,
        hitCol: Int,
        bubble: GameBubble,
        booster: BoosterType?,
        bubbleRadius: Float,
        screenWidth: Float
    ) {
        _gameplayState.update { it.copy(playState = PlayState.RESOLVING) }

        var matchedCoords = mutableSetOf<GridCoord>()
        var isMatchSuccessful = false

        when (booster) {
            BoosterType.BOMB_BUBBLE -> {
                val bombNeighbors = hexGrid.getNeighbors(hitRow, hitCol).toMutableSet()
                bombNeighbors.add(GridCoord(hitRow, hitCol))
                for (nb in hexGrid.getNeighbors(hitRow, hitCol)) {
                    bombNeighbors.addAll(hexGrid.getNeighbors(nb.row, nb.col))
                }
                matchedCoords = bombNeighbors
                isMatchSuccessful = true
                soundManager.playBombExplosion()
            }
            BoosterType.LIGHTNING_BUBBLE -> {
                for (c in 0 until hexGrid.getColsForRow(hitRow)) {
                    matchedCoords.add(GridCoord(hitRow, c))
                }
                isMatchSuccessful = true
                soundManager.playBombExplosion()
            }
            else -> {
                val connected = hexGrid.findConnectedMatches(hitRow, hitCol)
                if (connected.size >= 3 || bubble.type == BubbleType.RAINBOW) {
                    matchedCoords = connected.toMutableSet()
                    isMatchSuccessful = true
                }
            }
        }

        if (isMatchSuccessful && matchedCoords.isNotEmpty()) {
            val combo = _gameplayState.value.comboMultiplier
            var petsRescuedThisTurn = mutableListOf<String>()

            for (coord in matchedCoords) {
                val b = hexGrid.getBubble(coord.row, coord.col)
                if (b != null) {
                    val (cx, cy) = hexGrid.getCenter(coord.row, coord.col, bubbleRadius, screenWidth)
                    particleSystem.addPopBurst(cx, cy, b.color)
                    soundManager.playBubblePop(b.color)

                    if (b.type == BubbleType.PET && b.petId != null) {
                        petsRescuedThisTurn.add(b.petId)
                        particleSystem.addPetRescueCelebration(cx, cy)
                        soundManager.playPetRescueChime()
                        voiceManager.onPetRescued()
                    }

                    hexGrid.setBubble(coord.row, coord.col, null)
                }
            }

            val points = matchedCoords.size * 100 * combo
            addScore(points)
            val centerCoord = matchedCoords.first()
            val (popupX, popupY) = hexGrid.getCenter(centerCoord.row, centerCoord.col, bubbleRadius, screenWidth)
            particleSystem.addScorePopup(popupX, popupY, "+$points")

            val newCombo = combo + 1
            if (newCombo >= 2) {
                soundManager.playComboChime(newCombo)
                voiceManager.onCombo(newCombo)
            } else {
                voiceManager.onMatch()
            }

            val detached = hexGrid.findDetachedBubbles()
            if (detached.isNotEmpty()) {
                if (detached.size >= 4) {
                    voiceManager.onBigDrop()
                }
                for ((dCoord, dBubble) in detached) {
                    val (dx, dy) = hexGrid.getCenter(dCoord.row, dCoord.col, bubbleRadius, screenWidth)
                    hexGrid.setBubble(dCoord.row, dCoord.col, null)
                    particleSystem.addFallingBubble(dx, dy, bubbleRadius, dBubble.color, dBubble.type, dBubble.petId)

                    if (dBubble.type == BubbleType.PET && dBubble.petId != null) {
                        petsRescuedThisTurn.add(dBubble.petId)
                        particleSystem.addPetRescueCelebration(dx, dy)
                        soundManager.playPetRescueChime()
                        voiceManager.onPetRescued()
                    }
                }
            }

            val updatedPets = _gameplayState.value.rescuedPetsInLevel.toMutableList()
            updatedPets.addAll(petsRescuedThisTurn)

            _gameplayState.update {
                it.copy(
                    comboMultiplier = newCombo,
                    rescuedPetsInLevel = updatedPets
                )
            }
        } else {
            _gameplayState.update { it.copy(comboMultiplier = 1) }
        }

        checkEndConditions()
    }

    private fun checkEndConditions() {
        val state = _gameplayState.value
        val level = state.currentLevel

        val isVictory = when (level.objective) {
            LevelObjective.RESCUE_PETS -> {
                if (level.targetPetsCount > 0) {
                    state.rescuedPetsInLevel.size >= level.targetPetsCount || (state.rescuedPetsInLevel.isNotEmpty() && hexGrid.countRemainingPets() == 0)
                } else {
                    hexGrid.isBoardClear()
                }
            }
            LevelObjective.CLEAR_ALL -> {
                hexGrid.isBoardClear()
            }
            LevelObjective.TARGET_SCORE -> {
                state.score >= level.targetScore
            }
        }

        if (isVictory) {
            handleVictory()
            return
        }

        val lowestRow = hexGrid.getLowestRow()
        val isCeilingBreached = (lowestRow >= 16)

        if (state.shotsRemaining <= 0 || isCeilingBreached) {
            handleDefeat()
            return
        }

        val nextShooter = generateShooterBubble(level)
        _gameplayState.update {
            it.copy(
                playState = PlayState.READY,
                currentBubble = it.nextBubble,
                nextBubble = nextShooter
            )
        }
    }

    private fun handleVictory() {
        val state = _gameplayState.value
        val level = state.currentLevel

        val (s1, s2, s3) = level.starThresholds
        val stars = when {
            state.score >= s3 -> 3
            state.score >= s2 -> 2
            else -> 1
        }

        val bonusCoins = 150 + (state.shotsRemaining * 35) + (stars * 50)
        _saveState.value = repository.saveLevelComplete(
            level = level.levelNumber,
            stars = stars,
            score = state.score,
            coinsEarned = bonusCoins,
            newPetsRescued = state.rescuedPetsInLevel
        )

        soundManager.playVictoryFanfare()
        voiceManager.onLevelComplete()

        _gameplayState.update {
            it.copy(
                playState = PlayState.VICTORY,
                starsEarned = stars,
                coinsWon = bonusCoins
            )
        }
    }

    private fun handleDefeat() {
        soundManager.playDefeatChime()
        voiceManager.onLevelFailed()
        _gameplayState.update { it.copy(playState = PlayState.DEFEAT) }
    }

    fun continueWithExtraShots() {
        if (repository.spendCoins(60)) {
            _saveState.value = repository.getState()
            _gameplayState.update {
                it.copy(
                    shotsRemaining = it.shotsRemaining + 5,
                    playState = PlayState.READY
                )
            }
        }
    }

    fun retryLevel() {
        startLevel(_gameplayState.value.currentLevel.levelNumber)
    }

    fun nextLevel() {
        val nextLvl = (_gameplayState.value.currentLevel.levelNumber + 1).coerceAtMost(100)
        startLevel(nextLvl)
    }

    fun togglePause() {
        val currentlyPaused = _gameplayState.value.isPaused
        _gameplayState.update { it.copy(isPaused = !currentlyPaused) }
    }

    private fun addScore(points: Int) {
        _gameplayState.update { it.copy(score = it.score + points) }
    }

    fun buyBooster(booster: BoosterType) {
        if (repository.spendCoins(booster.costCoins)) {
            _saveState.value = repository.addBooster(booster, 1)
            soundManager.playPetRescueChime()
        }
    }

    fun claimDailyReward(): Boolean {
        val (success, newState) = repository.claimDailyReward()
        _saveState.value = newState
        if (success) {
            soundManager.playVictoryFanfare()
            voiceManager.speak("Daily reward claimed! Wonderful!", priority = true)
        }
        return success
    }

    fun petAnimal(petId: String) {
        _saveState.value = repository.petAnimal(petId)
        soundManager.playPetRescueChime()
        soundManager.playHaptic(30, 150)
    }

    fun updateSettings(
        music: Float? = null,
        sfx: Float? = null,
        voice: Float? = null,
        haptics: Boolean? = null,
        colorblind: Boolean? = null
    ) {
        val updated = repository.updateSettings(music, sfx, voice, haptics, colorblind)
        _saveState.value = updated
        music?.let { soundManager.musicVolume = it }
        sfx?.let { soundManager.sfxVolume = it }
        voice?.let { voiceManager.voiceVolume = it }
        haptics?.let { soundManager.hapticsEnabled = it }
    }

    fun resetAllProgress() {
        _saveState.value = repository.resetAllProgress()
        startLevel(1)
        _currentScreen.value = GameScreen.HOME
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        soundManager.stopBackgroundMusic()
        voiceManager.shutdown()
    }
}
