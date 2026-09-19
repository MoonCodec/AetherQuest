package com.mooncodec.aetherquest.model

import kotlin.math.pow

data class Player(
    val name: String = "Initié",
    val level: Int =1,
    val currentXp: Int = 0,
    val gold: Int = 0,
) {
    val xpToNextLevel: Int
        get() = (100 * level.toDouble().pow(1.2)).toInt()
}
