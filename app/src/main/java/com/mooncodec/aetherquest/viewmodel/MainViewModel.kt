package com.mooncodec.aetherquest.viewmodel

import androidx.lifecycle.ViewModel
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
}