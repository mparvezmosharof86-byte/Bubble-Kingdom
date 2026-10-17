package com.example.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import kotlin.math.cos
import kotlin.math.sin

object BubbleRenderer {

    /**
     * Draws a single glossy 3D cartoon bubble with specular highlights, rim depth,
     * special bubble icons, and cute pet faces when trapped.
     * 100% hardware-safe pure vector drawing.
     */
    fun drawBubble(
        drawScope: DrawScope,
        centerX: Float,
        centerY: Float,
        radius: Float,
        color: BubbleColor,
        type: BubbleType = BubbleType.NORMAL,
        petId: String? = null,
        colorblindEnabled: Boolean = false,
        scale: Float = 1.0f,
        alpha: Float = 1.0f
    ) {
        val r = radius * scale
        if (r <= 1f) return

        val center = Offset(centerX, centerY)

        when (type) {
            BubbleType.STONE -> {
                drawStoneBubble(drawScope, center, r, alpha)
                return
            }
            BubbleType.BOMB -> {
                drawBombBubble(drawScope, center, r, alpha)
                return
            }
            BubbleType.RAINBOW -> {
                drawRainbowBubble(drawScope, center, r, alpha)
                return
            }
            BubbleType.LIGHTNING -> {
                drawLightningBubble(drawScope, center, r, alpha)
                return
            }
            BubbleType.FIREBALL -> {
                drawFireballBubble(drawScope, center, r, alpha)
                return
            }
            BubbleType.PET -> {
                drawPetBubble(drawScope, center, r, color, alpha)
                return
            }
            BubbleType.NORMAL -> {
                // Regular bubble
            }
        }

        // 1. Soft Shadow underneath
        drawScope.drawCircle(
            color = Color.Black.copy(alpha = 0.22f * alpha),
            radius = r * 0.95f,
            center = Offset(center.x + r * 0.1f, center.y + r * 0.15f)
        )

        // 2. Base 3D Spherical Radial Gradient
        val lightOffset = Offset(center.x - r * 0.32f, center.y - r * 0.32f)
        val sphereBrush = Brush.radialGradient(
            colors = listOf(
                color.highlightColor.copy(alpha = alpha),
                color.primaryColor.copy(alpha = alpha),
                color.shadowColor.copy(alpha = alpha)
            ),
            center = lightOffset,
            radius = r * 1.35f
        )
        drawScope.drawCircle(
            brush = sphereBrush,
            radius = r,
            center = center
        )

        // 3. Inner glass reflection ring (translucent glowing rim)
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.28f * alpha),
            radius = r * 0.90f,
            center = center,
            style = Stroke(width = r * 0.14f)
        )

        // 4. Primary Specular Glare (glossy top-left highlight)
        val glareCenter = Offset(center.x - r * 0.34f, center.y - r * 0.34f)
        drawScope.drawOval(
            color = Color.White.copy(alpha = 0.78f * alpha),
            topLeft = Offset(glareCenter.x - r * 0.30f, glareCenter.y - r * 0.18f),
            size = Size(r * 0.60f, r * 0.36f)
        )

        // 5. Secondary soft bottom-right reflection
        val bounceCenter = Offset(center.x + r * 0.28f, center.y + r * 0.30f)
        drawScope.drawOval(
            color = Color.White.copy(alpha = 0.35f * alpha),
            topLeft = Offset(bounceCenter.x - r * 0.22f, bounceCenter.y - r * 0.12f),
            size = Size(r * 0.44f, r * 0.24f)
        )

        // 6. Optional Colorblind Accessible Symbol (drawn with pure vector paths)
        if (colorblindEnabled) {
            drawColorblindSymbol(drawScope, center, r * 0.45f, color, alpha)
        }
    }

    private fun drawColorblindSymbol(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        color: BubbleColor,
        alpha: Float
    ) {
        val symColor = Color.White.copy(alpha = 0.9f * alpha)
        when (color) {
            BubbleColor.RED -> {
                // Star
                val starPath = Path()
                for (i in 0 until 5) {
                    val angle = (i * 4 * Math.PI / 5) - Math.PI / 2
                    val px = center.x + (cos(angle) * size).toFloat()
                    val py = center.y + (sin(angle) * size).toFloat()
                    if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
                }
                starPath.close()
                drawScope.drawPath(starPath, symColor)
            }
            BubbleColor.BLUE -> {
                // Diamond
                val diamond = Path().apply {
                    moveTo(center.x, center.y - size)
                    lineTo(center.x + size, center.y)
                    lineTo(center.x, center.y + size)
                    lineTo(center.x - size, center.y)
                    close()
                }
                drawScope.drawPath(diamond, symColor)
            }
            BubbleColor.GREEN -> {
                // Circle
                drawScope.drawCircle(symColor, radius = size * 0.75f, center = center)
            }
            BubbleColor.YELLOW -> {
                // Triangle
                val tri = Path().apply {
                    moveTo(center.x, center.y - size)
                    lineTo(center.x + size, center.y + size * 0.8f)
                    lineTo(center.x - size, center.y + size * 0.8f)
                    close()
                }
                drawScope.drawPath(tri, symColor)
            }
            BubbleColor.PURPLE -> {
                // Square
                drawScope.drawRect(
                    symColor,
                    topLeft = Offset(center.x - size * 0.65f, center.y - size * 0.65f),
                    size = Size(size * 1.3f, size * 1.3f)
                )
            }
            BubbleColor.ORANGE -> {
                // Cross +
                val barW = size * 0.4f
                val barL = size * 1.4f
                drawScope.drawRect(
                    symColor,
                    topLeft = Offset(center.x - barW / 2f, center.y - barL / 2f),
                    size = Size(barW, barL)
                )
                drawScope.drawRect(
                    symColor,
                    topLeft = Offset(center.x - barL / 2f, center.y - barW / 2f),
                    size = Size(barL, barW)
                )
            }
        }
    }

    private fun drawPetBubble(
        drawScope: DrawScope,
        center: Offset,
        r: Float,
        color: BubbleColor,
        alpha: Float
    ) {
        // Glowing magic bubble base
        val magicBrush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f * alpha),
                color.highlightColor.copy(alpha = 0.85f * alpha),
                color.primaryColor.copy(alpha = 0.75f * alpha)
            ),
            center = center,
            radius = r * 1.2f
        )
        drawScope.drawCircle(brush = magicBrush, radius = r, center = center)

        // Golden rim
        drawScope.drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.85f * alpha),
            radius = r * 0.92f,
            center = center,
            style = Stroke(width = r * 0.12f)
        )

        // Cute Animal Face Vector:
        // Ears
        val earR = r * 0.28f
        drawScope.drawCircle(
            color = Color(0xFFF59E0B).copy(alpha = alpha),
            radius = earR,
            center = Offset(center.x - r * 0.45f, center.y - r * 0.42f)
        )
        drawScope.drawCircle(
            color = Color(0xFFF59E0B).copy(alpha = alpha),
            radius = earR,
            center = Offset(center.x + r * 0.45f, center.y - r * 0.42f)
        )

        // Face Head
        drawScope.drawCircle(
            color = Color(0xFFFDE68A).copy(alpha = alpha),
            radius = r * 0.55f,
            center = Offset(center.x, center.y + r * 0.05f)
        )

        // Happy Smiling Eyes
        val eyeR = r * 0.09f
        drawScope.drawCircle(
            color = Color(0xFF1E1B4B).copy(alpha = alpha),
            radius = eyeR,
            center = Offset(center.x - r * 0.20f, center.y - r * 0.05f)
        )
        drawScope.drawCircle(
            color = Color(0xFF1E1B4B).copy(alpha = alpha),
            radius = eyeR,
            center = Offset(center.x + r * 0.20f, center.y - r * 0.05f)
        )

        // Pink Cheeks
        drawScope.drawCircle(
            color = Color(0xFFF472B6).copy(alpha = 0.6f * alpha),
            radius = eyeR * 1.3f,
            center = Offset(center.x - r * 0.32f, center.y + r * 0.12f)
        )
        drawScope.drawCircle(
            color = Color(0xFFF472B6).copy(alpha = 0.6f * alpha),
            radius = eyeR * 1.3f,
            center = Offset(center.x + r * 0.32f, center.y + r * 0.12f)
        )

        // Nose
        drawScope.drawCircle(
            color = Color(0xFF451A03).copy(alpha = alpha),
            radius = r * 0.07f,
            center = Offset(center.x, center.y + r * 0.12f)
        )

        // Glossy bubble glare
        val glareCenter = Offset(center.x - r * 0.35f, center.y - r * 0.35f)
        drawScope.drawOval(
            color = Color.White.copy(alpha = 0.75f * alpha),
            topLeft = Offset(glareCenter.x - r * 0.28f, glareCenter.y - r * 0.16f),
            size = Size(r * 0.55f, r * 0.32f)
        )
    }

    private fun drawBombBubble(drawScope: DrawScope, center: Offset, r: Float, alpha: Float) {
        val bombBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF6B7280).copy(alpha = alpha),
                Color(0xFF374151).copy(alpha = alpha),
                Color(0xFF111827).copy(alpha = alpha)
            ),
            center = Offset(center.x - r * 0.3f, center.y - r * 0.3f),
            radius = r * 1.3f
        )
        drawScope.drawCircle(brush = bombBrush, radius = r, center = center)

        // Glowing orange spark on top
        drawScope.drawCircle(
            color = Color(0xFFF59E0B).copy(alpha = alpha),
            radius = r * 0.25f,
            center = Offset(center.x + r * 0.35f, center.y - r * 0.40f)
        )
        drawScope.drawCircle(
            color = Color(0xFFEF4444).copy(alpha = alpha),
            radius = r * 0.14f,
            center = Offset(center.x + r * 0.35f, center.y - r * 0.40f)
        )

        // Bomb fuse icon / spark
        val fuse = Path().apply {
            moveTo(center.x + r * 0.1f, center.y - r * 0.25f)
            lineTo(center.x + r * 0.35f, center.y - r * 0.40f)
        }
        drawScope.drawPath(fuse, Color(0xFFD97706), style = Stroke(width = r * 0.1f))

        // White skull / warning star in center
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.85f * alpha),
            radius = r * 0.28f,
            center = center
        )
        drawScope.drawCircle(
            color = Color(0xFF111827).copy(alpha = alpha),
            radius = r * 0.08f,
            center = Offset(center.x - r * 0.1f, center.y - r * 0.04f)
        )
        drawScope.drawCircle(
            color = Color(0xFF111827).copy(alpha = alpha),
            radius = r * 0.08f,
            center = Offset(center.x + r * 0.1f, center.y - r * 0.04f)
        )
    }

    private fun drawRainbowBubble(drawScope: DrawScope, center: Offset, r: Float, alpha: Float) {
        val rainbowBrush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFFEF4444).copy(alpha = alpha),
                Color(0xFFF59E0B).copy(alpha = alpha),
                Color(0xFF10B981).copy(alpha = alpha),
                Color(0xFF3B82F6).copy(alpha = alpha),
                Color(0xFF8B5CF6).copy(alpha = alpha),
                Color(0xFFEF4444).copy(alpha = alpha)
            ),
            center = center
        )
        drawScope.drawCircle(brush = rainbowBrush, radius = r, center = center)

        // Concentric rainbow rings
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.5f * alpha),
            radius = r * 0.65f,
            center = center,
            style = Stroke(width = r * 0.12f)
        )
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.85f * alpha),
            radius = r * 0.30f,
            center = center
        )
    }

    private fun drawLightningBubble(drawScope: DrawScope, center: Offset, r: Float, alpha: Float) {
        val electricBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF67E8F9).copy(alpha = alpha),
                Color(0xFF06B6D4).copy(alpha = alpha),
                Color(0xFF0E7490).copy(alpha = alpha)
            ),
            center = center,
            radius = r * 1.2f
        )
        drawScope.drawCircle(brush = electricBrush, radius = r, center = center)

        // Vector Lightning Bolt
        val bolt = Path().apply {
            moveTo(center.x + r * 0.1f, center.y - r * 0.65f)
            lineTo(center.x - r * 0.35f, center.y + r * 0.05f)
            lineTo(center.x + r * 0.05f, center.y + r * 0.05f)
            lineTo(center.x - r * 0.15f, center.y + r * 0.65f)
            lineTo(center.x + r * 0.40f, center.y - r * 0.10f)
            lineTo(center.x, center.y - r * 0.10f)
            close()
        }
        drawScope.drawPath(bolt, Color(0xFFFDE047).copy(alpha = alpha))
        drawScope.drawPath(bolt, Color.White.copy(alpha = alpha), style = Stroke(width = r * 0.08f))
    }

    private fun drawFireballBubble(drawScope: DrawScope, center: Offset, r: Float, alpha: Float) {
        val fireBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFDE047).copy(alpha = alpha),
                Color(0xFFF97316).copy(alpha = alpha),
                Color(0xFFDC2626).copy(alpha = alpha)
            ),
            center = center,
            radius = r * 1.2f
        )
        drawScope.drawCircle(brush = fireBrush, radius = r, center = center)

        // Vector Flame
        val flame = Path().apply {
            moveTo(center.x, center.y - r * 0.65f)
            cubicTo(
                center.x + r * 0.45f, center.y - r * 0.2f,
                center.x + r * 0.5f, center.y + r * 0.4f,
                center.x, center.y + r * 0.6f
            )
            cubicTo(
                center.x - r * 0.5f, center.y + r * 0.4f,
                center.x - r * 0.45f, center.y - r * 0.2f,
                center.x, center.y - r * 0.65f
            )
            close()
        }
        drawScope.drawPath(flame, Color(0xFFFEF08A).copy(alpha = alpha))
    }

    private fun drawStoneBubble(drawScope: DrawScope, center: Offset, r: Float, alpha: Float) {
        val stoneBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF9CA3AF).copy(alpha = alpha),
                Color(0xFF6B7280).copy(alpha = alpha),
                Color(0xFF374151).copy(alpha = alpha)
            ),
            center = Offset(center.x - r * 0.2f, center.y - r * 0.2f),
            radius = r * 1.2f
        )
        drawScope.drawCircle(brush = stoneBrush, radius = r, center = center)

        // Granite chisel cuts
        val cut1 = Path().apply {
            moveTo(center.x - r * 0.5f, center.y - r * 0.3f)
            lineTo(center.x, center.y)
            lineTo(center.x + r * 0.5f, center.y - r * 0.2f)
        }
        val cut2 = Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x - r * 0.2f, center.y + r * 0.5f)
        }
        drawScope.drawPath(cut1, Color(0xFF1F2937), style = Stroke(width = r * 0.12f))
        drawScope.drawPath(cut2, Color(0xFF1F2937), style = Stroke(width = r * 0.12f))
    }
}
