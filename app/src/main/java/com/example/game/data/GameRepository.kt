package com.example.game.data

import android.content.Context
import android.content.SharedPreferences
import com.example.game.model.BoosterType
import org.json.JSONObject
import java.util.Calendar

data class GameSaveState(
    val unlockedLevel: Int = 1,
    val levelStars: Map<Int, Int> = emptyMap(),
    val levelHighScores: Map<Int, Int> = emptyMap(),
    val playerCoins: Int = 350,
    val rescuedPets: Set<String> = setOf("pippa"),
    val petBonds: Map<String, Int> = mapOf("pippa" to 1),
    val boosterInventory: Map<BoosterType, Int> = mapOf(
        BoosterType.SUPER_AIM to 2,
        BoosterType.BOMB_BUBBLE to 2,
        BoosterType.RAINBOW_BUBBLE to 2,
        BoosterType.LIGHTNING_BUBBLE to 1,
        BoosterType.FIREBALL to 1,
        BoosterType.EXTRA_SHOTS to 2
    ),
    val dailyRewardStreak: Int = 0,
    val lastDailyClaimDay: Long = 0L,
    val lastLuckySpinDay: Long = 0L,
    val completedAchievements: Set<String> = emptySet(),
    val musicVolume: Float = 0.6f,
    val sfxVolume: Float = 1.0f,
    val voiceVolume: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val colorblindEnabled: Boolean = false
)

class GameRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bubble_kingdom_save_v1", Context.MODE_PRIVATE)

    private var cachedState: GameSaveState = loadState()

    fun getState(): GameSaveState = cachedState

    fun saveLevelComplete(
        level: Int,
        stars: Int,
        score: Int,
        coinsEarned: Int,
        newPetsRescued: List<String>
    ): GameSaveState {
        val newUnlocked = maxOf(cachedState.unlockedLevel, level + 1).coerceAtMost(100)
        val currentStars = cachedState.levelStars[level] ?: 0
        val updatedStars = cachedState.levelStars.toMutableMap().apply {
            put(level, maxOf(currentStars, stars))
        }
        val currentHigh = cachedState.levelHighScores[level] ?: 0
        val updatedScores = cachedState.levelHighScores.toMutableMap().apply {
            put(level, maxOf(currentHigh, score))
        }

        val updatedPets = cachedState.rescuedPets.toMutableSet().apply {
            addAll(newPetsRescued)
        }

        val updatedBonds = cachedState.petBonds.toMutableMap()
        for (pet in newPetsRescued) {
            updatedBonds[pet] = (updatedBonds[pet] ?: 0) + 1
        }

        // Check achievements
        val achievements = cachedState.completedAchievements.toMutableSet()
        achievements.add("first_win")
        if (updatedStars.values.count { it == 3 } >= 10) achievements.add("star_collector")
        if (updatedPets.size >= 4) achievements.add("pet_whisperer")
        if (level >= 35) achievements.add("forest_master")
        if (level >= 70) achievements.add("candy_king")
        if (level >= 100) achievements.add("kingdom_champion")

        val newState = cachedState.copy(
            unlockedLevel = newUnlocked,
            levelStars = updatedStars,
            levelHighScores = updatedScores,
            playerCoins = cachedState.playerCoins + coinsEarned,
            rescuedPets = updatedPets,
            petBonds = updatedBonds,
            completedAchievements = achievements
        )
        persistState(newState)
        return newState
    }

    fun addCoins(amount: Int): GameSaveState {
        val newState = cachedState.copy(playerCoins = cachedState.playerCoins + amount)
        persistState(newState)
        return newState
    }

    fun spendCoins(amount: Int): Boolean {
        if (cachedState.playerCoins < amount) return false
        val newState = cachedState.copy(playerCoins = cachedState.playerCoins - amount)
        persistState(newState)
        return true
    }

    fun addBooster(type: BoosterType, count: Int = 1): GameSaveState {
        val updated = cachedState.boosterInventory.toMutableMap()
        updated[type] = (updated[type] ?: 0) + count
        val newState = cachedState.copy(boosterInventory = updated)
        persistState(newState)
        return newState
    }

    fun consumeBooster(type: BoosterType): Boolean {
        val current = cachedState.boosterInventory[type] ?: 0
        if (current <= 0) return false
        val updated = cachedState.boosterInventory.toMutableMap()
        updated[type] = current - 1
        val newState = cachedState.copy(boosterInventory = updated)
        persistState(newState)
        return true
    }

    fun claimDailyReward(): Pair<Boolean, GameSaveState> {
        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        if (currentDay <= cachedState.lastDailyClaimDay) {
            return Pair(false, cachedState) // Already claimed today
        }

        val streak = if (currentDay - cachedState.lastDailyClaimDay == 1L) {
            (cachedState.dailyRewardStreak % 7) + 1
        } else {
            1
        }

        // Daily reward amounts
        val bonusCoins = streak * 100
        val bonusBooster = when (streak) {
            3 -> BoosterType.BOMB_BUBBLE
            5 -> BoosterType.RAINBOW_BUBBLE
            7 -> BoosterType.LIGHTNING_BUBBLE
            else -> null
        }

        val updatedBoosters = cachedState.boosterInventory.toMutableMap()
        if (bonusBooster != null) {
            updatedBoosters[bonusBooster] = (updatedBoosters[bonusBooster] ?: 0) + 1
        }

        val achievements = cachedState.completedAchievements.toMutableSet()
        if (streak >= 7) achievements.add("daily_devotion")

        val newState = cachedState.copy(
            playerCoins = cachedState.playerCoins + bonusCoins,
            boosterInventory = updatedBoosters,
            dailyRewardStreak = streak,
            lastDailyClaimDay = currentDay,
            completedAchievements = achievements
        )
        persistState(newState)
        return Pair(true, newState)
    }

    fun updateSettings(
        musicVolume: Float? = null,
        sfxVolume: Float? = null,
        voiceVolume: Float? = null,
        hapticsEnabled: Boolean? = null,
        colorblindEnabled: Boolean? = null
    ): GameSaveState {
        val newState = cachedState.copy(
            musicVolume = musicVolume ?: cachedState.musicVolume,
            sfxVolume = sfxVolume ?: cachedState.sfxVolume,
            voiceVolume = voiceVolume ?: cachedState.voiceVolume,
            hapticsEnabled = hapticsEnabled ?: cachedState.hapticsEnabled,
            colorblindEnabled = colorblindEnabled ?: cachedState.colorblindEnabled
        )
        persistState(newState)
        return newState
    }

    fun petAnimal(petId: String): GameSaveState {
        val updatedBonds = cachedState.petBonds.toMutableMap()
        updatedBonds[petId] = (updatedBonds[petId] ?: 0) + 1
        val newState = cachedState.copy(petBonds = updatedBonds)
        persistState(newState)
        return newState
    }

    fun canSpinForFree(): Boolean {
        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        return currentDay > cachedState.lastLuckySpinDay
    }

    fun recordLuckySpin(coins: Int, booster: BoosterType?): GameSaveState {
        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val updatedBoosters = cachedState.boosterInventory.toMutableMap()
        if (booster != null) {
            updatedBoosters[booster] = (updatedBoosters[booster] ?: 0) + 1
        }
        val newState = cachedState.copy(
            playerCoins = cachedState.playerCoins + coins,
            boosterInventory = updatedBoosters,
            lastLuckySpinDay = currentDay
        )
        persistState(newState)
        return newState
    }

    fun resetAllProgress(): GameSaveState {
        val fresh = GameSaveState()
        persistState(fresh)
        return fresh
    }

    private fun persistState(state: GameSaveState) {
        cachedState = state
        try {
            val json = JSONObject()
            json.put("unlockedLevel", state.unlockedLevel)
            json.put("playerCoins", state.playerCoins)
            json.put("dailyRewardStreak", state.dailyRewardStreak)
            json.put("lastDailyClaimDay", state.lastDailyClaimDay)
            json.put("lastLuckySpinDay", state.lastLuckySpinDay)
            json.put("musicVolume", state.musicVolume.toDouble())
            json.put("sfxVolume", state.sfxVolume.toDouble())
            json.put("voiceVolume", state.voiceVolume.toDouble())
            json.put("hapticsEnabled", state.hapticsEnabled)
            json.put("colorblindEnabled", state.colorblindEnabled)

            // Maps & Sets
            val starsJson = JSONObject()
            state.levelStars.forEach { (k, v) -> starsJson.put(k.toString(), v) }
            json.put("levelStars", starsJson)

            val scoresJson = JSONObject()
            state.levelHighScores.forEach { (k, v) -> scoresJson.put(k.toString(), v) }
            json.put("levelHighScores", scoresJson)

            val petsJson = org.json.JSONArray(state.rescuedPets)
            json.put("rescuedPets", petsJson)

            val bondsJson = JSONObject()
            state.petBonds.forEach { (k, v) -> bondsJson.put(k, v) }
            json.put("petBonds", bondsJson)

            val boostersJson = JSONObject()
            state.boosterInventory.forEach { (k, v) -> boostersJson.put(k.name, v) }
            json.put("boosterInventory", boostersJson)

            val achJson = org.json.JSONArray(state.completedAchievements)
            json.put("completedAchievements", achJson)

            prefs.edit().putString("save_data_json", json.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadState(): GameSaveState {
        val raw = prefs.getString("save_data_json", null) ?: return GameSaveState()
        return try {
            val json = JSONObject(raw)
            val unlockedLevel = json.optInt("unlockedLevel", 1)
            val playerCoins = json.optInt("playerCoins", 350)
            val dailyRewardStreak = json.optInt("dailyRewardStreak", 0)
            val lastDailyClaimDay = json.optLong("lastDailyClaimDay", 0L)
            val lastLuckySpinDay = json.optLong("lastLuckySpinDay", 0L)
            val musicVolume = json.optDouble("musicVolume", 0.6).toFloat()
            val sfxVolume = json.optDouble("sfxVolume", 1.0).toFloat()
            val voiceVolume = json.optDouble("voiceVolume", 1.0).toFloat()
            val hapticsEnabled = json.optBoolean("hapticsEnabled", true)
            val colorblindEnabled = json.optBoolean("colorblindEnabled", false)

            val levelStars = mutableMapOf<Int, Int>()
            val starsObj = json.optJSONObject("levelStars")
            starsObj?.keys()?.forEach { k ->
                k.toIntOrNull()?.let { levelStars[it] = starsObj.getInt(k) }
            }

            val levelHighScores = mutableMapOf<Int, Int>()
            val scoresObj = json.optJSONObject("levelHighScores")
            scoresObj?.keys()?.forEach { k ->
                k.toIntOrNull()?.let { levelHighScores[it] = scoresObj.getInt(k) }
            }

            val rescuedPets = mutableSetOf<String>()
            val petsArr = json.optJSONArray("rescuedPets")
            if (petsArr != null) {
                for (i in 0 until petsArr.length()) {
                    rescuedPets.add(petsArr.getString(i))
                }
            } else {
                rescuedPets.add("pippa")
            }

            val petBonds = mutableMapOf<String, Int>()
            val bondsObj = json.optJSONObject("petBonds")
            bondsObj?.keys()?.forEach { k ->
                petBonds[k] = bondsObj.getInt(k)
            }

            val boosterInventory = mutableMapOf<BoosterType, Int>()
            val boostersObj = json.optJSONObject("boosterInventory")
            BoosterType.entries.forEach { b ->
                boosterInventory[b] = boostersObj?.optInt(b.name, 2) ?: 2
            }

            val completedAchievements = mutableSetOf<String>()
            val achArr = json.optJSONArray("completedAchievements")
            if (achArr != null) {
                for (i in 0 until achArr.length()) {
                    completedAchievements.add(achArr.getString(i))
                }
            }

            GameSaveState(
                unlockedLevel = unlockedLevel,
                levelStars = levelStars,
                levelHighScores = levelHighScores,
                playerCoins = playerCoins,
                rescuedPets = rescuedPets,
                petBonds = petBonds,
                boosterInventory = boosterInventory,
                dailyRewardStreak = dailyRewardStreak,
                lastDailyClaimDay = lastDailyClaimDay,
                lastLuckySpinDay = lastLuckySpinDay,
                completedAchievements = completedAchievements,
                musicVolume = musicVolume,
                sfxVolume = sfxVolume,
                voiceVolume = voiceVolume,
                hapticsEnabled = hapticsEnabled,
                colorblindEnabled = colorblindEnabled
            )
        } catch (_: Exception) {
            GameSaveState()
        }
    }
}
