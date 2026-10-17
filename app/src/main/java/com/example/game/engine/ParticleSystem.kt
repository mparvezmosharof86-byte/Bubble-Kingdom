package com.example.game.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ParticleType {
    SPARKLE,
    STAR,
    RING,
    CONFETTI
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val type: ParticleType,
    var size: Float,
    var alpha: Float = 1.0f,
    var rotation: Float = 0f,
    val rotSpeed: Float = 0f,
    var life: Float = 0f,
    val maxLife: Float = 0.6f
)

data class FallingBubble(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    val color: BubbleColor,
    val type: BubbleType,
    val petId: String?,
    var bounceCount: Int = 0,
    var isDone: Boolean = false
)

data class ScorePopup(
    var x: Float,
    var y: Float,
    val text: String,
    val color: Color,
    var alpha: Float = 1.0f,
    var scale: Float = 1.0f,
    var life: Float = 0f,
    val maxLife: Float = 0.9f
)

class ParticleSystem {
    private val lock = Any()
    private val _particles = mutableListOf<Particle>()
    private val _fallingBubbles = mutableListOf<FallingBubble>()
    private val _scorePopups = mutableListOf<ScorePopup>()

    val particles: List<Particle> get() = synchronized(lock) { _particles.toList() }
    val fallingBubbles: List<FallingBubble> get() = synchronized(lock) { _fallingBubbles.toList() }
    val scorePopups: List<ScorePopup> get() = synchronized(lock) { _scorePopups.toList() }

    fun hasActiveParticles(): Boolean = synchronized(lock) {
        _particles.isNotEmpty() || _fallingBubbles.isNotEmpty() || _scorePopups.isNotEmpty()
    }

    fun addPopBurst(x: Float, y: Float, bubbleColor: BubbleColor) = synchronized(lock) {
        val baseColor = bubbleColor.primaryColor
        val highlight = bubbleColor.highlightColor

        // Expanding pop ring
        _particles.add(
            Particle(
                x = x,
                y = y,
                vx = 0f,
                vy = 0f,
                color = highlight,
                type = ParticleType.RING,
                size = 12f,
                alpha = 0.9f,
                maxLife = 0.35f
            )
        )

        // Pop sparkle fragments
        for (i in 0 until 12) {
            val angle = (i * (2.0 * Math.PI) / 12.0) + Random.nextDouble(-0.2, 0.2)
            val speed = Random.nextFloat() * 260f + 120f
            val pColor = if (Random.nextBoolean()) baseColor else highlight
            _particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat(),
                    color = pColor,
                    type = if (i % 2 == 0) ParticleType.STAR else ParticleType.SPARKLE,
                    size = Random.nextFloat() * 6f + 4f,
                    alpha = 1.0f,
                    rotation = Random.nextFloat() * 360f,
                    rotSpeed = Random.nextFloat() * 400f - 200f,
                    maxLife = Random.nextFloat() * 0.3f + 0.4f
                )
            )
        }
    }

    fun addPetRescueCelebration(x: Float, y: Float) = synchronized(lock) {
        val rainbowColors = listOf(
            Color(0xFFFFD700), Color(0xFFFF69B4), Color(0xFF00E5FF),
            Color(0xFF76FF03), Color(0xFFFF3D00), Color(0xFFD500F9)
        )
        for (i in 0 until 24) {
            val angle = Random.nextDouble(0.0, 2.0 * Math.PI)
            val speed = Random.nextFloat() * 340f + 100f
            _particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat() - 150f, // upward burst
                    color = rainbowColors[i % rainbowColors.size],
                    type = ParticleType.CONFETTI,
                    size = Random.nextFloat() * 8f + 5f,
                    alpha = 1.0f,
                    rotation = Random.nextFloat() * 360f,
                    rotSpeed = Random.nextFloat() * 500f - 250f,
                    maxLife = 1.2f
                )
            )
        }
    }

    fun addFallingBubble(
        startX: Float,
        startY: Float,
        radius: Float,
        color: BubbleColor,
        type: BubbleType,
        petId: String?
    ) = synchronized(lock) {
        val vx = (Random.nextFloat() * 140f - 70f)
        val vy = (Random.nextFloat() * -100f - 50f) // slight initial hop
        _fallingBubbles.add(
            FallingBubble(
                x = startX,
                y = startY,
                vx = vx,
                vy = vy,
                radius = radius,
                color = color,
                type = type,
                petId = petId
            )
        )
    }

    fun addScorePopup(x: Float, y: Float, text: String, color: Color = Color(0xFFFFD700)) = synchronized(lock) {
        _scorePopups.add(
            ScorePopup(
                x = x,
                y = y,
                text = text,
                color = color
            )
        )
    }

    fun update(dt: Float, screenHeight: Float, screenWidth: Float, onDropScored: ((Int) -> Unit)? = null) = synchronized(lock) {
        // Update particles
        val pIter = _particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.life += dt
            if (p.life >= p.maxLife) {
                pIter.remove()
                continue
            }
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.rotation += p.rotSpeed * dt
            p.vy += 450f * dt // gravity
            p.alpha = (1.0f - (p.life / p.maxLife)).coerceIn(0f, 1f)
            if (p.type == ParticleType.RING) {
                p.size += 180f * dt
            }
        }

        // Update falling bubbles
        val gravity = 1600f
        val bottomY = screenHeight - 60f
        val fIter = _fallingBubbles.iterator()
        while (fIter.hasNext()) {
            val fb = fIter.next()
            fb.vy += gravity * dt
            fb.x += fb.vx * dt
            fb.y += fb.vy * dt

            // Wall bounce
            if (fb.x - fb.radius < 0) {
                fb.x = fb.radius
                fb.vx = -fb.vx * 0.7f
            } else if (fb.x + fb.radius > screenWidth) {
                fb.x = screenWidth - fb.radius
                fb.vx = -fb.vx * 0.7f
            }

            // Bottom pop & bucket score: pop cleanly into sparkles and points
            if (fb.y >= bottomY) {
                val bucketScore = when ((fb.x / (screenWidth / 5f)).toInt().coerceIn(0, 4)) {
                    0 -> 150
                    1 -> 300
                    2 -> 600
                    3 -> 300
                    else -> 150
                }
                onDropScored?.invoke(bucketScore)
                _scorePopups.add(ScorePopup(fb.x, fb.y - 30f, "+$bucketScore", Color(0xFFFFD700)))
                // Pop burst
                val baseColor = fb.color.primaryColor
                val highlight = fb.color.highlightColor
                _particles.add(
                    Particle(
                        x = fb.x,
                        y = fb.y,
                        vx = 0f,
                        vy = 0f,
                        color = highlight,
                        type = ParticleType.RING,
                        size = 12f,
                        alpha = 0.9f,
                        maxLife = 0.35f
                    )
                )
                for (i in 0 until 8) {
                    val angle = (i * (2.0 * Math.PI) / 8.0)
                    val speed = Random.nextFloat() * 200f + 100f
                    _particles.add(
                        Particle(
                            x = fb.x,
                            y = fb.y,
                            vx = (cos(angle) * speed).toFloat(),
                            vy = (sin(angle) * speed).toFloat(),
                            color = if (i % 2 == 0) baseColor else highlight,
                            type = ParticleType.SPARKLE,
                            size = Random.nextFloat() * 5f + 3f,
                            alpha = 1.0f,
                            maxLife = 0.4f
                        )
                    )
                }
                fIter.remove()
            }
        }

        // Update score popups
        val sIter = _scorePopups.iterator()
        while (sIter.hasNext()) {
            val s = sIter.next()
            s.life += dt
            if (s.life >= s.maxLife) {
                sIter.remove()
                continue
            }
            s.y -= 75f * dt
            val progress = s.life / s.maxLife
            s.scale = 1.0f + 0.3f * sin(progress * Math.PI).toFloat()
            s.alpha = (1.0f - progress).coerceIn(0f, 1f)
        }
    }

    fun clear() = synchronized(lock) {
        _particles.clear()
        _fallingBubbles.clear()
        _scorePopups.clear()
    }
}
