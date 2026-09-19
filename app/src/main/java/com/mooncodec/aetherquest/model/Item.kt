package com.mooncodec.aetherquest.model

enum class ItemType {
    CONSUMABLE,
    EQUIPMENT,
    QUEST
}

data class Item(
    val id: String,
    val name: String,
    val description: String,
    val type: ItemType,
    val value: Int,
    val statBonus: Int = 0
)