package com.example.game.model

import androidx.compose.ui.graphics.Color

enum class BubbleColor(
    val id: Int,
    val primaryColor: Color,
    val highlightColor: Color,
    val shadowColor: Color,
    val symbol: String, // Colorblind accessible symbol
    val colorName: String
) {
    RED(
        id = 1,
        primaryColor = Color(0xFFEF4444),
        highlightColor = Color(0xFFFCA5A5),
        shadowColor = Color(0xFF991B1B),
        symbol = "★",
        colorName = "Ruby"
    ),
    BLUE(
        id = 2,
        primaryColor = Color(0xFF3B82F6),
        highlightColor = Color(0xFF93C5FD),
        shadowColor = Color(0xFF1E40AF),
        symbol = "◆",
        colorName = "Sapphire"
    ),
    GREEN(
        id = 3,
        primaryColor = Color(0xFF10B981),
        highlightColor = Color(0xFF6EE7B7),
        shadowColor = Color(0xFF065F46),
        symbol = "●",
        colorName = "Emerald"
    ),
    YELLOW(
        id = 4,
        primaryColor = Color(0xFFF59E0B),
        highlightColor = Color(0xFFFDE68A),
        shadowColor = Color(0xFFB45309),
        symbol = "▲",
        colorName = "Citrine"
    ),
    PURPLE(
        id = 5,
        primaryColor = Color(0xFF8B5CF6),
        highlightColor = Color(0xFFC4B5FD),
        shadowColor = Color(0xFF5B21B6),
        symbol = "♥",
        colorName = "Amethyst"
    ),
    ORANGE(
        id = 6,
        primaryColor = Color(0xFFF97316),
        highlightColor = Color(0xFFFDBA74),
        shadowColor = Color(0xFFC2410C),
        symbol = "✦",
        colorName = "Topaz"
    );

    companion object {
        fun fromId(id: Int): BubbleColor = entries.firstOrNull { it.id == id } ?: RED
    }
}

enum class BubbleType {
    NORMAL,
    PET,           // Holds a cute baby animal pet
    BOMB,          // Explodes 2-radius cluster
    RAINBOW,       // Matches any adjacent color
    LIGHTNING,     // Clears entire row
    FIREBALL,      // Straight pierce
    STONE          // Heavy obstacle: can only drop by detaching or bomb
}

enum class BoosterType(
    val title: String,
    val description: String,
    val costCoins: Int,
    val iconSymbol: String
) {
    SUPER_AIM(
        title = "Super Aim",
        description = "Shows extended reflection laser trajectory guide",
        costCoins = 60,
        iconSymbol = "🎯"
    ),
    BOMB_BUBBLE(
        title = "Bomb Bubble",
        description = "Explodes a wide circle of bubbles on impact",
        costCoins = 100,
        iconSymbol = "💣"
    ),
    RAINBOW_BUBBLE(
        title = "Rainbow Magic",
        description = "Matches and pops any color bubble it touches",
        costCoins = 80,
        iconSymbol = "🌈"
    ),
    LIGHTNING_BUBBLE(
        title = "Lightning Zap",
        description = "Blasts and clears the entire horizontal row",
        costCoins = 120,
        iconSymbol = "⚡"
    ),
    FIREBALL(
        title = "Fireball",
        description = "Burns straight through bubbles without stopping",
        costCoins = 150,
        iconSymbol = "🔥"
    ),
    EXTRA_SHOTS(
        title = "+5 Shots",
        description = "Adds 5 additional shooter bubbles",
        costCoins = 75,
        iconSymbol = "➕"
    )
}
