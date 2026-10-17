package com.example.game.levels

import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import com.example.game.model.PetRegistry

enum class LevelObjective {
    RESCUE_PETS,
    CLEAR_ALL,
    TARGET_SCORE
}

data class LevelCell(
    val color: BubbleColor = BubbleColor.RED,
    val type: BubbleType = BubbleType.NORMAL,
    val petId: String? = null
)

data class LevelDefinition(
    val levelNumber: Int,
    val title: String,
    val worldId: Int,
    val worldName: String,
    val objective: LevelObjective,
    val targetPetsCount: Int,
    val targetScore: Int,
    val shotsAllowed: Int,
    val starThresholds: Triple<Int, Int, Int>,
    val allowedShooterColors: List<BubbleColor>,
    val grid: List<List<LevelCell?>> // [row][col]
)

object LevelDatabase {
    const val TOTAL_LEVELS = 100

    private val cachedLevels = mutableMapOf<Int, LevelDefinition>()

    fun getLevel(levelNumber: Int): LevelDefinition {
        val clamped = levelNumber.coerceIn(1, TOTAL_LEVELS)
        return cachedLevels.getOrPut(clamped) { generateLevel(clamped) }
    }

    private fun generateLevel(lvl: Int): LevelDefinition {
        val worldId = when {
            lvl <= 35 -> 1 // Emerald Forest
            lvl <= 70 -> 2 // Candy Meadows
            else -> 3      // Celestial Islands
        }
        val worldName = when (worldId) {
            1 -> "Emerald Forest"
            2 -> "Candy Meadows"
            else -> "Celestial Islands"
        }

        val numColors = when {
            lvl <= 3 -> 3
            lvl <= 10 -> 4
            lvl <= 40 -> 5
            else -> 6
        }
        val allColors = BubbleColor.entries.take(numColors)

        // Objective alternates
        val isPetLevel = (lvl == 1 || lvl % 2 != 0 || lvl % 5 == 0)
        val objective = if (isPetLevel) LevelObjective.RESCUE_PETS else LevelObjective.CLEAR_ALL

        // Pets to rescue
        val petCount = when {
            !isPetLevel -> 0
            lvl <= 10 -> 1
            lvl <= 35 -> 2
            lvl <= 70 -> 3
            else -> 4
        }

        // Shots allowed: generous to ensure fun solvable gameplay
        val shots = when {
            lvl <= 5 -> 30
            lvl <= 20 -> 28
            lvl <= 50 -> 26
            else -> 24
        }

        val targetScore = 3000 + lvl * 250
        val star1 = targetScore
        val star2 = (targetScore * 1.5f).toInt()
        val star3 = (targetScore * 2.2f).toInt()

        val titles = listOf(
            "Welcome Woods", "Puppy Trail", "Sunny Glade", "Emerald Canopy", "Bunny Hop",
            "Whispering Pines", "Mushroom Grove", "Dewdrop Hollow", "Acorn Alley", "Forest Sanctuary",
            "Hidden Creek", "Owl's Perch", "Bramble Maze", "Fern Valley", "Twinkling Glade",
            "Mossy Stones", "Fox's Den", "Starlight Lake", "Ancient Oak", "Guardian Grove",
            "Timber Pass", "Riverbed Rock", "Squirrel Stash", "Amber Glen", "Firefly Haven",
            "Whispering Willows", "Pet Citadel", "Crystal Cavern", "Cedar Ridge", "Shadow Grove",
            "Enchanted Hollow", "Pinecone Path", "Moonlit Pond", "Forest Climax", "Kingdom Gates",
            // Candy Meadows
            "Sugar Spire", "Gumdrop Meadow", "Caramel Creek", "Cotton Clouds", "Lollipop Lane",
            "Taffy Twist", "Jellybean Ridge", "Chocolate Falls", "Marshmallow Peak", "Candy Castle",
            "Licorice Labyrinth", "Sweet Breeze", "Berry Orchard", "Sundae Summit", "Cookie Crag",
            "Gingerbread Gate", "Sprinkle Glade", "Fondant Forest", "Bubblegum Basin", "Pastel Prairie",
            "Nougat Nook", "Praline Pass", "Honey Hive", "Frosting Fjord", "Peppermint Park",
            "Sugar Blossom", "Macaron Manor", "Tart Terrace", "Toffee Trail", "Sweet Citadel",
            // Celestial Islands
            "Cloudtop Haven", "Skyward Isle", "Breeze Citadel", "Zephyr Zenith", "Starlight Spire",
            "Floating Garden", "Nimbus Nest", "Aurora Arch", "Pegasus Peak", "Sunburst Spire",
            "Prism Palace", "Lunar Lagoon", "Stellar Sanctuary", "Comet Crest", "Solar Shore",
            "Nebula Nexus", "Astral Atoll", "Galaxy Glade", "Eclipse Eyre", "Crown Kingdom"
        )
        val title = titles.getOrElse(lvl - 1) { "Royal Level $lvl" }

        // Generate grid
        val grid = createGridForLevel(lvl, allColors, isPetLevel, petCount)

        return LevelDefinition(
            levelNumber = lvl,
            title = title,
            worldId = worldId,
            worldName = worldName,
            objective = objective,
            targetPetsCount = petCount,
            targetScore = targetScore,
            shotsAllowed = shots,
            starThresholds = Triple(star1, star2, star3),
            allowedShooterColors = allColors,
            grid = grid
        )
    }

    private fun createGridForLevel(
        lvl: Int,
        colors: List<BubbleColor>,
        isPetLevel: Boolean,
        petCount: Int
    ): List<List<LevelCell?>> {
        val totalRows = when {
            lvl <= 5 -> 7
            lvl <= 20 -> 9
            lvl <= 50 -> 11
            else -> 12
        }

        val grid = mutableListOf<List<LevelCell?>>()
        val petRegistry = PetRegistry.ALL_PETS
        var petsPlaced = 0

        // Deterministic pseudo-random seed based on level number
        val seed = lvl * 31 + 17
        fun pseudoRand(i: Int): Float = kotlin.math.abs(kotlin.math.sin((seed + i * 13).toDouble())).toFloat()

        for (r in 0 until totalRows) {
            val cols = if (r % 2 == 0) 8 else 7
            val rowList = mutableListOf<LevelCell?>()

            for (c in 0 until cols) {
                val randVal = pseudoRand(r * 11 + c * 7)

                // Shape masks for varied architectures
                val shouldFill = when {
                    // Level 1: Gentle top cluster
                    lvl == 1 -> r in 0..3 && (c in 1 until cols - 1)
                    // Level 2: Heart/V shape
                    lvl == 2 -> (r in 0..4) && (c in (r / 2) until (cols - r / 2))
                    // Pyramid / Diamond / Honeycomb patterns
                    lvl % 4 == 0 -> (c >= (r % 3)) && (c <= cols - 1 - (r % 3))
                    lvl % 4 == 1 -> !(r > 2 && (c == 0 || c == cols - 1))
                    lvl % 4 == 2 -> (r in 0..2) || (r in 4..7 && c in 1 until cols - 1)
                    else -> (r + c) % 5 != 0
                }

                if (!shouldFill) {
                    rowList.add(null)
                    continue
                }

                // Check for Pet placement:
                // Place pets securely embedded inside the formation
                val isPetSpot = isPetLevel && petsPlaced < petCount &&
                        (r in 2..5) &&
                        (c == cols / 2 || (c == 1 && petsPlaced == 1) || (c == cols - 2 && petsPlaced == 2))

                if (isPetSpot) {
                    val pet = petRegistry[petsPlaced % petRegistry.size]
                    rowList.add(
                        LevelCell(
                            color = colors[(r + c) % colors.size],
                            type = BubbleType.PET,
                            petId = pet.id
                        )
                    )
                    petsPlaced++
                    continue
                }

                // Check for Special Bubbles (Bomb, Rainbow, Lightning, Stone)
                val isStone = (lvl > 15 && r in 2..4 && randVal > 0.88f)
                val isBomb = (lvl > 4 && randVal in 0.05f..0.08f)
                val isRainbow = (lvl > 8 && randVal in 0.12f..0.15f)
                val isLightning = (lvl > 12 && randVal in 0.20f..0.22f)

                val (type, cellColor) = when {
                    isStone -> Pair(BubbleType.STONE, colors[0])
                    isBomb -> Pair(BubbleType.BOMB, colors[0])
                    isRainbow -> Pair(BubbleType.RAINBOW, colors[0])
                    isLightning -> Pair(BubbleType.LIGHTNING, colors[0])
                    else -> {
                        // Create natural matching clusters (same color as neighbors to ensure fair combos)
                        val colorIndex = ((r / 2) + (c / 2) + lvl) % colors.size
                        Pair(BubbleType.NORMAL, colors[colorIndex])
                    }
                }

                rowList.add(LevelCell(color = cellColor, type = type))
            }
            grid.add(rowList)
        }

        // Safety guarantee: If a pet level, ensure all required pets are properly placed
        while (isPetLevel && petsPlaced < petCount) {
            val pet = petRegistry[petsPlaced % petRegistry.size]
            var placed = false
            for (r in 1 until totalRows) {
                for (c in 0 until grid[r].size) {
                    if (grid[r][c] != null && grid[r][c]?.type != BubbleType.PET) {
                        val mutableRow = grid[r].toMutableList()
                        mutableRow[c] = LevelCell(
                            color = colors[(r + c) % colors.size],
                            type = BubbleType.PET,
                            petId = pet.id
                        )
                        grid[r] = mutableRow
                        petsPlaced++
                        placed = true
                        break
                    }
                }
                if (placed) break
            }
            if (!placed) break
        }

        return grid
    }
}
