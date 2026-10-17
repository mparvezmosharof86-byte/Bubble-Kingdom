package com.example.game.model

import androidx.compose.ui.graphics.Color

data class PetData(
    val id: String,
    val name: String,
    val species: String,
    val description: String,
    val iconEmoji: String,
    val primaryColor: Color,
    val rescueSoundCue: String,
    val perkDescription: String,
    val unlockLevel: Int
)

object PetRegistry {
    val ALL_PETS = listOf(
        PetData(
            id = "pippa",
            name = "Pippa",
            species = "Golden Puppy",
            description = "A cheerful golden pup with floppy ears and a heart of gold. Loves tummy rubs!",
            iconEmoji = "🐶",
            primaryColor = Color(0xFFFBBF24),
            rescueSoundCue = "Yip yip! Thank you!",
            perkDescription = "+10% bonus coins from all popped bubbles",
            unlockLevel = 1
        ),
        PetData(
            id = "barnaby",
            name = "Barnaby",
            species = "Cotton Bunny",
            description = "A bouncy rabbit with velvety ears and curious pink nose. Master of bouncing shots!",
            iconEmoji = "🐰",
            primaryColor = Color(0xFFF472B6),
            rescueSoundCue = "Hop hop! Free at last!",
            perkDescription = "+1 wall bounce reflection length",
            unlockLevel = 5
        ),
        PetData(
            id = "kiki",
            name = "Kiki",
            species = "Starry Kitten",
            description = "A playful violet kitten with twinkling starry eyes and a sparkling tail.",
            iconEmoji = "🐱",
            primaryColor = Color(0xFFA78BFA),
            rescueSoundCue = "Purrr! You saved me!",
            perkDescription = "Extends combo streak duration by 2 shots",
            unlockLevel = 12
        ),
        PetData(
            id = "oliver",
            name = "Oliver",
            species = "Sage Owl",
            description = "A wise young owlet with grand round goggles. Knows every hidden angle on the board.",
            iconEmoji = "🦉",
            primaryColor = Color(0xFF34D399),
            rescueSoundCue = "Hoo-ray! Splendid shot!",
            perkDescription = "Occasionally highlights optimal bounce targets",
            unlockLevel = 22
        ),
        PetData(
            id = "sammy",
            name = "Sammy",
            species = "Acorn Squirrel",
            description = "An energetic little squirrel who hoards magic nuts and extra bubble ammo.",
            iconEmoji = "🐿️",
            primaryColor = Color(0xFFFB923C),
            rescueSoundCue = "Chitter chitter! Nuts to you!",
            perkDescription = "15% chance to refund a shot on a 4+ combo",
            unlockLevel = 35
        ),
        PetData(
            id = "leo",
            name = "Leo",
            species = "Ruby Fox",
            description = "A brave fox cub with a fiery bushy tail. His passion detonates explosive joy!",
            iconEmoji = "🦊",
            primaryColor = Color(0xFFF87171),
            rescueSoundCue = "Yip! That was a blast!",
            perkDescription = "Increases bomb explosion radius by 1 hex",
            unlockLevel = 50
        ),
        PetData(
            id = "pip",
            name = "Pip",
            species = "Frost Penguin",
            description = "A charming baby penguin in an ice scarf. Wiggles with excitement when bubbles pop!",
            iconEmoji = "🐧",
            primaryColor = Color(0xFF38BDF8),
            rescueSoundCue = "Peep peep! Frosty fresh!",
            perkDescription = "Freezes ceiling descent for 3 turns on rescue",
            unlockLevel = 70
        ),
        PetData(
            id = "mimi",
            name = "Mimi",
            species = "Blossom Mouse",
            description = "A tiny fairy mouse holding a clover leaf. Grants magical luck in tough spots.",
            iconEmoji = "🐭",
            primaryColor = Color(0xFFE879F9),
            rescueSoundCue = "Squeak squeak! A lucky miracle!",
            perkDescription = "Grants 1 free Rainbow Bubble every 10 levels",
            unlockLevel = 85
        )
    )

    fun getPet(id: String): PetData = ALL_PETS.firstOrNull { it.id == id } ?: ALL_PETS.first()
}
