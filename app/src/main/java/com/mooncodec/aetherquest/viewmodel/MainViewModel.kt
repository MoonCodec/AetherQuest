package com.mooncodec.aetherquest.viewmodel

import androidx.lifecycle.ViewModel
import com.mooncodec.aetherquest.model.Item
import com.mooncodec.aetherquest.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class MainViewModel : ViewModel() {

    private val _player = MutableStateFlow(Player())
    val player: StateFlow<Player> = _player.asStateFlow()

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
}