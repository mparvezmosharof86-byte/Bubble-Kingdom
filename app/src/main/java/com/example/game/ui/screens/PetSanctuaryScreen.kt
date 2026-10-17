package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.PetData
import com.example.game.model.PetRegistry
import com.example.game.ui.components.CoinPill
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun PetSanctuaryScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF3B0764)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("pet_sanctuary_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("sanctuary_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "PET SANCTUARY",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinPill(coins = saveState.playerCoins)
            }

            // Description Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(Color(0xFF312E81).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "🐾 Rescue trapped pets in puzzle levels to unlock them here! Tap each pet to shower them with love and boost their perks.",
                    color = Color(0xFFE0E7FF),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Pets List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(PetRegistry.ALL_PETS) { pet ->
                    val isRescued = saveState.rescuedPets.contains(pet.id)
                    val bondLevel = saveState.petBonds[pet.id] ?: 0

                    PetCard(
                        pet = pet,
                        isRescued = isRescued,
                        bondLevel = bondLevel,
                        onPetClick = {
                            if (isRescued) {
                                viewModel.petAnimal(pet.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PetCard(
    pet: PetData,
    isRescued: Boolean,
    bondLevel: Int,
    onPetClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isRescued) {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF312E81)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1F2937), Color(0xFF111827))
                    )
                }
            )
            .border(
                1.5.dp,
                if (isRescued) pet.primaryColor.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
            .testTag("pet_card_${pet.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar Bubble
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRescued) pet.primaryColor.copy(alpha = 0.35f) else Color.DarkGray
                    )
                    .border(2.dp, if (isRescued) pet.primaryColor else Color.Gray, CircleShape)
                    .clickable(enabled = isRescued) { onPetClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isRescued) {
                    Text(text = pet.iconEmoji, fontSize = 38.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pet.name,
                        color = if (isRescued) Color.White else Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isRescued) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEC4899).copy(alpha = 0.25f))
                                .clickable { onPetClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("pet_btn_${pet.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Pet",
                                tint = Color(0xFFF472B6),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lvl $bondLevel",
                                color = Color(0xFFFBCFE8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Unlocks Lvl ${pet.unlockLevel}",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = pet.species,
                    color = pet.primaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = if (isRescued) pet.description else "Trapped in the kingdom! Beat level ${pet.unlockLevel} to set them free.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (isRescued) {
                    Text(
                        text = "⭐ Perk: ${pet.perkDescription}",
                        color = Color(0xFFFDE047),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
