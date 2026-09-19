package com.mooncodec.aetherquest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mooncodec.aetherquest.model.Item
import com.mooncodec.aetherquest.model.ItemType
import com.mooncodec.aetherquest.model.Quest
import com.mooncodec.aetherquest.ui.theme.AetherQuestTheme
import com.mooncodec.aetherquest.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetherQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayerHomeScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerHomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val player by viewModel.player.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val context = LocalContext.current
    val progress = if (player.xpToNextLevel > 0) {
        player.currentXp.toFloat() / player.xpToNextLevel.toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- PROFIL JOUEUR ---
        Text(
            text = player.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Niveau ${player.level} — Or : ${player.gold} 🪙",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        Text(
            text = "XP : ${player.currentXp} / ${player.xpToNextLevel}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION INVENTAIRE ---
        Text(
            text = "Inventaire (${player.inventory.size})",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (player.inventory.isEmpty()) {
            Text(
                text = "Inventaire vide",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(player.inventory) { item ->
                    ItemCard(item = item, onUse = { viewModel.removeItem(item) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION QUÊTES / HABITUDES ---
        Text(
            text = "Quêtes du jour",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quests) { quest ->
                QuestCard(
                    quest = quest,
                    onComplete = { viewModel.completeQuest(quest.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- BOUTIQUE DE TEST ---
        Button(
            onClick = {
                val potion = Item(
                    id = System.currentTimeMillis().toString(),
                    name = "Potion de soin",
                    description = "+20 HP",
                    type = ItemType.CONSUMABLE,
                    value = 25
                )
                val success = viewModel.buyItem(potion)
                if (!success) {
                    Toast.makeText(context, "Pas assez d'or !", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Acheter Potion (25 🪙)")
        }
    }
}

@Composable
fun ItemCard(
    item: Item,
    onUse: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.name, style = MaterialTheme.typography.labelMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = onUse, modifier = Modifier.height(28.dp)) {
                Text("Utiliser", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun QuestCard(
    quest: Quest,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = quest.title, style = MaterialTheme.typography.titleSmall)
                Text(text = quest.description, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Récompense : +${quest.difficulty.xpReward} XP / +${quest.difficulty.goldReward} 🪙",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onComplete,
                enabled = !quest.isCompleted
            ) {
                Text(if (quest.isCompleted) "Fait !" else "Valider")
            }
        }
    }
}