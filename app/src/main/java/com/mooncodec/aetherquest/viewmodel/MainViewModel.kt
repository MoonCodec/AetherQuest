package com.mooncodec.aetherquest.viewmodel

import androidx.lifecycle.ViewModel
import com.mooncodec.aetherquest.model.Item
import com.mooncodec.aetherquest.model.Player
import com.mooncodec.aetherquest.model.Quest
import com.mooncodec.aetherquest.model.QuestDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class MainViewModel : ViewModel() {

    private val _player = MutableStateFlow(Player())
    val player: StateFlow<Player> = _player.asStateFlow()

    private val _quests = MutableStateFlow<List<Quest>>(
        listOf(
            Quest("1", "Boire 1.5L d'eau", "S'hydrater tout au long de la journée", QuestDifficulty.EASY),
            Quest("2", "Séance de sport", "30 minutes d'exercice", QuestDifficulty.MEDIUM)
        )
    )
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

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

            currentPlayer.copy(
                level = newLevel,
                currentXp = newXp
            )
        }
    }

    fun addGold(amount: Int) {
        _player.update { currentPlayer ->
            currentPlayer.copy(gold = currentPlayer.gold + amount)
        }
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
    }

    fun addQuest(title: String, description: String, difficulty: QuestDifficulty) {
        val newQuest = Quest(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description,
            difficulty = difficulty
        )
        _quests.update { currentQuests -> currentQuests + newQuest }
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
            addGold(quest.difficulty.goldReward)
        }
    }
}