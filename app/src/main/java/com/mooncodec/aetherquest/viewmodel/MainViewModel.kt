package com.mooncodec.aetherquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mooncodec.aetherquest.data.UserPreferencesRepository
import com.mooncodec.aetherquest.model.Item
import com.mooncodec.aetherquest.model.Player
import com.mooncodec.aetherquest.model.Quest
import com.mooncodec.aetherquest.model.QuestDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    private val _player = MutableStateFlow(Player())
    val player: StateFlow<Player> = _player.asStateFlow()

    private val _quests = MutableStateFlow<List<Quest>>(
        listOf(
            Quest("1", "Boire 1.5L d'eau", "S'hydrater tout au long de la journée", QuestDifficulty.EASY),
            Quest("2", "Séance de sport", "30 minutes d'exercice", QuestDifficulty.MEDIUM)
        )
    )
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    init {
        viewModelScope.launch {
            repository.playerData.collect { (name, level, xp) ->
                _player.update { it.copy(name = name, level = level, currentXp = xp) }
            }
        }
        viewModelScope.launch {
            repository.playerGold.collect { gold ->
                _player.update { it.copy(gold = gold) }
            }
        }
    }

    private fun persistPlayer() {
        viewModelScope.launch {
            val p = _player.value
            repository.savePlayerStats(p.name, p.level, p.currentXp, p.gold)
        }
    }

    fun addXp(amount: Int) {
        _player.update { currentPlayer ->
            var newXp = currentPlayer.currentXp + amount
            var newLevel = currentPlayer.level
            var xpNeeded = (100 * newLevel.toDouble().pow(1.2)).toInt()

            while (newXp >= xpNeeded) {
                newXp -= xpNeeded
                newLevel++
                xpNeeded = (100 * newLevel.toDouble().pow(1.2)).toInt()
            }

            currentPlayer.copy(level = newLevel, currentXp = newXp)
        }
        persistPlayer()
    }

    fun buyItem(item: Item): Boolean {
        val currentGold = _player.value.gold
        if (currentGold >= item.value) {
            _player.update { currentPlayer ->
                currentPlayer.copy(
                    gold = currentPlayer.gold - item.value,
                    inventory = currentPlayer.inventory + item
                )
            }
            persistPlayer()
            return true
        }
        return false
    }

    fun removeItem(item: Item) {
        _player.update { currentPlayer ->
            val updatedInventory = currentPlayer.inventory.toMutableList()
            updatedInventory.remove(item)
            currentPlayer.copy(inventory = updatedInventory)
        }
        persistPlayer()
    }

    fun completeQuest(questId: String) {
        val quest = _quests.value.find { it.id == questId } ?: return
        if (!quest.isCompleted) {
            _quests.update { currentQuests ->
                currentQuests.map {
                    if (it.id == questId) it.copy(isCompleted = true) else it
                }
            }
            addXp(quest.difficulty.xpReward)
            _player.update { it.copy(gold = it.gold + quest.difficulty.goldReward) }
            persistPlayer()
        }
    }
}