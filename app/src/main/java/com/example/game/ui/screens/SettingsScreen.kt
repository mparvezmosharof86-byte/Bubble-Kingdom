package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.GlossyButton
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF1E293B)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("settings_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "SETTINGS",
                    color = Color(0xFFFDE047),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Section
            SettingsSectionHeader(title = "AUDIO & VOICE")

            SettingsSliderItem(
                label = "🎵 Music Volume",
                value = saveState.musicVolume,
                onValueChange = { viewModel.updateSettings(music = it) }
            )

            SettingsSliderItem(
                label = "🔊 Sound Effects (SFX)",
                value = saveState.sfxVolume,
                onValueChange = { viewModel.updateSettings(sfx = it) }
            )

            SettingsSliderItem(
                label = "🗣️ Female Voice-Over",
                value = saveState.voiceVolume,
                onValueChange = { viewModel.updateSettings(voice = it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Accessibility Section
            SettingsSectionHeader(title = "GAMEPLAY & ACCESSIBILITY")

            SettingsToggleItem(
                label = "📳 Haptic Vibration",
                subtitle = "Tactile feedback when popping bubbles",
                checked = saveState.hapticsEnabled,
                onCheckedChange = { viewModel.updateSettings(haptics = it) }
            )

            SettingsToggleItem(
                label = "👁️ Colorblind Symbols",
                subtitle = "Show geometric patterns on bubbles (★, ◆, ●, ▲)",
                checked = saveState.colorblindEnabled,
                onCheckedChange = { viewModel.updateSettings(colorblind = it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Information & Safety Section
            SettingsSectionHeader(title = "ABOUT & PRIVACY")

            SettingsActionItem(
                label = "🔒 Privacy Policy & Data Safety",
                subtitle = "Child-safe, 100% offline gameplay, no data collected",
                onClick = { showPrivacyDialog = true }
            )

            SettingsActionItem(
                label = "⚠️ Reset All Progress",
                subtitle = "Clears unlocked levels, coins, and records",
                onClick = { showResetDialog = true },
                isDestructive = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bubble Kingdom v1.0 • AI Studio Build",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text("Privacy Policy & Data Safety", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Bubble Kingdom is designed as a family-friendly, child-safe offline puzzle game.\n\n" +
                                "• We do NOT collect, transmit, or store any personal information.\n" +
                                "• All game progress, level stars, coins, and pet collections are saved strictly on your local device.\n" +
                                "• No invasive background analytics or third-party trackers are utilized.\n" +
                                "• Ad monetization remains completely optional and disabled unless configured by the user."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("Close", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Progress?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to reset all progress? This will reset your unlocked levels, coins, and pet companions to level 1.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetAllProgress()
                        showResetDialog = false
                    }) {
                        Text("Reset Everything", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF818CF8),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsSliderItem(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = "${(value * 100).toInt()}%", color = Color(0xFF94A3B8), fontSize = 13.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF6366F1),
                activeTrackColor = Color(0xFF818CF8),
                inactiveTrackColor = Color(0xFF334155)
            )
        )
    }
}

@Composable
fun SettingsToggleItem(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF10B981),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = if (isDestructive) Color(0xFFFCA5A5) else Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}
