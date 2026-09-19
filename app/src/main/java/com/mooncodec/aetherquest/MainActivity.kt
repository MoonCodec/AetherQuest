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
import androidx.compose.foundation.layout.width
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
    val context = LocalContext.current
    val progress = if (player.xpToNextLevel > 0) {
        player.currentXp.toFloat() / player.xpToNextLevel.toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = player.name,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Niveau ${player.level}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "XP : ${player.currentXp} / ${player.xpToNextLevel}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Or : ${player.gold} 🪙",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECTION INVENTAIRE ---
        Text(
            text = "Inventaire (${player.inventory.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        // --- ACTIONS & BOUTIQUE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.addXp(40) }) {
                Text("+40 XP")
            }

            Button(onClick = {
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
            }) {
                Text("Acheter Potion (25 🪙)")
            }
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
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.name, style = MaterialTheme.typography.labelLarge)
            Text(text = item.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = onUse, modifier = Modifier.height(32.dp)) {
                Text("Utiliser", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}