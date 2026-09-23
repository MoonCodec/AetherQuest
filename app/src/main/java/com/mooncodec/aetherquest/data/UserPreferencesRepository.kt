package com.mooncodec.aetherquest.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val PLAYER_NAME = stringPreferencesKey("player_name")
        val PLAYER_LEVEL = intPreferencesKey("player_level")
        val PLAYER_XP = intPreferencesKey("player_xp")
        val PLAYER_GOLD = intPreferencesKey("player_gold")
    }

    val playerData: Flow<Triple<String, Int, Int>> = context.dataStore.data.map { prefs ->
        Triple(
            prefs[PLAYER_NAME] ?: "Initié",
            prefs[PLAYER_LEVEL] ?: 1,
            prefs[PLAYER_XP] ?: 0
        )
    }

    val playerGold: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PLAYER_GOLD] ?: 100
    }

    suspend fun savePlayerStats(name: String, level: Int, xp: Int, gold: Int) {
        context.dataStore.edit { prefs ->
            prefs[PLAYER_NAME] = name
            prefs[PLAYER_LEVEL] = level
            prefs[PLAYER_XP] = xp
            prefs[PLAYER_GOLD] = gold
        }
    }
}