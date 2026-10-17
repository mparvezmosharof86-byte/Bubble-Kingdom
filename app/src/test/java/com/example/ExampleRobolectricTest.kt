package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.data.GameSaveState
import com.example.game.levels.LevelDatabase
import com.example.game.ui.screens.LUCKY_SPIN_REWARDS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bubble Kingdom", appName)
  }

  @Test
  fun `test level database generation`() {
    val level1 = LevelDatabase.getLevel(1)
    assertEquals(1, level1.levelNumber)
    assertEquals("Emerald Forest", level1.worldName)
    assertTrue(level1.shotsAllowed > 0)
    assertTrue(level1.grid.isNotEmpty())
    val petCount = level1.grid.flatten().count { it?.petId != null }
    assertEquals(1, petCount)
  }

  @Test
  fun `test lucky spin rewards configuration`() {
    assertEquals(8, LUCKY_SPIN_REWARDS.size)
    LUCKY_SPIN_REWARDS.forEach { reward ->
      assertNotNull(reward.title)
      assertNotNull(reward.iconEmoji)
      assertTrue(reward.coins >= 0)
    }
  }

  @Test
  fun `test game save state initial values`() {
    val state = GameSaveState()
    assertEquals(1, state.unlockedLevel)
    assertEquals(350, state.playerCoins)
    assertTrue(state.musicVolume > 0f)
  }
}
