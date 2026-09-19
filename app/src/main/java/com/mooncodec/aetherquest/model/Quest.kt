package com.mooncodec.aetherquest.model

enum class QuestDifficulty(val xpReward: Int, val goldReward: Int) {
    EASY(xpReward = 20, goldReward = 10),
    MEDIUM(xpReward = 50, goldReward = 25),
    HARD(xpReward = 100, goldReward = 60)
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: QuestDifficulty = QuestDifficulty.EASY,
    val isCompleted: Boolean = false
)