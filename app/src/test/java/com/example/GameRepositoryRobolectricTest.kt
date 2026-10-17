package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.data.GameRepository
import com.example.game.model.BoosterType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GameRepositoryRobolectricTest {
    private lateinit var context: Context
    private lateinit var repository: GameRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = GameRepository(context)
        repository.resetAllProgress()
    }

    @Test
    fun testInitialSaveState() {
        val state = repository.getState()
        assertEquals(1, state.unlockedLevel)
        assertEquals(350, state.playerCoins)
        assertTrue(state.rescuedPets.contains("pippa"))
        assertTrue((state.boosterInventory[BoosterType.BOMB_BUBBLE] ?: 0) >= 1)
    }

    @Test
    fun testLevelCompletionUnlocksNextLevelAndStoresStars() {
        val state = repository.saveLevelComplete(
            level = 1,
            stars = 3,
            score = 5200,
            coinsEarned = 250,
            newPetsRescued = listOf("barnaby")
        )

        assertEquals(2, state.unlockedLevel)
        assertEquals(3, state.levelStars[1])
        assertEquals(5200, state.levelHighScores[1])
        assertEquals(600, state.playerCoins) // 350 + 250
        assertTrue(state.rescuedPets.contains("barnaby"))
    }

    @Test
    fun testSpendAndAddCoins() {
        repository.addCoins(100)
        assertEquals(450, repository.getState().playerCoins)

        val success = repository.spendCoins(200)
        assertTrue(success)
        assertEquals(250, repository.getState().playerCoins)

        val fail = repository.spendCoins(5000)
        assertFalse(fail)
        assertEquals(250, repository.getState().playerCoins)
    }

    @Test
    fun testBoosterInventoryManagement() {
        val initialBombs = repository.getState().boosterInventory[BoosterType.BOMB_BUBBLE] ?: 0
        repository.addBooster(BoosterType.BOMB_BUBBLE, 2)
        assertEquals(initialBombs + 2, repository.getState().boosterInventory[BoosterType.BOMB_BUBBLE])

        val consumed = repository.consumeBooster(BoosterType.BOMB_BUBBLE)
        assertTrue(consumed)
        assertEquals(initialBombs + 1, repository.getState().boosterInventory[BoosterType.BOMB_BUBBLE])
    }

    @Test
    fun testDailyRewardClaim() {
        val (firstClaim, state1) = repository.claimDailyReward()
        assertTrue(firstClaim)
        assertEquals(1, state1.dailyRewardStreak)

        // Same-day claim should be rejected
        val (secondClaim, _) = repository.claimDailyReward()
        assertFalse(secondClaim)
    }
}
