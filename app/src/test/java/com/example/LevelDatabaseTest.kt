package com.example

import com.example.game.levels.LevelDatabase
import com.example.game.levels.LevelObjective
import com.example.game.model.BubbleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelDatabaseTest {

    @Test
    fun testAllOneHundredLevelsAreValid() {
        for (lvl in 1..100) {
            val levelDef = LevelDatabase.getLevel(lvl)
            assertEquals(lvl, levelDef.levelNumber)
            assertTrue("Title should not be blank for level $lvl", levelDef.title.isNotBlank())
            assertTrue("Shots must be at least 20 for solvable play", levelDef.shotsAllowed >= 20)
            assertTrue("Allowed colors must not be empty", levelDef.allowedShooterColors.isNotEmpty())
            assertTrue("Grid must have rows", levelDef.grid.isNotEmpty())

            var bubbleCount = 0
            var petCount = 0
            for (row in levelDef.grid) {
                for (cell in row) {
                    if (cell != null) {
                        bubbleCount++
                        if (cell.type == BubbleType.PET) {
                            petCount++
                            assertNotNull("Pet bubble must have petId", cell.petId)
                        }
                    }
                }
            }

            assertTrue("Level $lvl must contain bubbles to pop", bubbleCount > 0)

            if (levelDef.objective == LevelObjective.RESCUE_PETS) {
                assertTrue("Pet level $lvl must contain at least 1 trapped pet", petCount > 0)
                assertEquals(levelDef.targetPetsCount, petCount)
            }
        }
    }

    @Test
    fun testBiomesAreCorrectlyDistributed() {
        // World 1: 1-35 (Emerald Forest)
        assertEquals(1, LevelDatabase.getLevel(1).worldId)
        assertEquals(1, LevelDatabase.getLevel(35).worldId)
        assertEquals("Emerald Forest", LevelDatabase.getLevel(1).worldName)

        // World 2: 36-70 (Candy Meadows)
        assertEquals(2, LevelDatabase.getLevel(36).worldId)
        assertEquals(2, LevelDatabase.getLevel(70).worldId)
        assertEquals("Candy Meadows", LevelDatabase.getLevel(36).worldName)

        // World 3: 71-100 (Celestial Islands)
        assertEquals(3, LevelDatabase.getLevel(71).worldId)
        assertEquals(3, LevelDatabase.getLevel(100).worldId)
        assertEquals("Celestial Islands", LevelDatabase.getLevel(71).worldName)
    }
}
