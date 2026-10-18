package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.game.ui.components.KingdomBottomNavBar
import com.example.game.ui.screens.AchievementsScreen
import com.example.game.ui.screens.BoosterShopScreen
import com.example.game.ui.screens.DailyRewardsScreen
import com.example.game.ui.screens.EventsScreen
import com.example.game.ui.screens.GameplayScreen
import com.example.game.ui.screens.HomeScreen
import com.example.game.ui.screens.LuckySpinScreen
import com.example.game.ui.screens.PetSanctuaryScreen
import com.example.game.ui.screens.SettingsScreen
import com.example.game.ui.screens.SplashScreen
import com.example.game.ui.screens.WorldMapScreen
import com.example.game.viewmodel.GameScreen
import com.example.game.viewmodel.GameViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                BubbleKingdomApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.soundManager.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundManager.stopBackgroundMusic()
    }
}

@Composable
fun BubbleKingdomApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // 5টি প্রধান ট্যাবে নেভিগেশন বার সবসময় স্থায়ী থাকবে
    val isMainTab = currentScreen in listOf(
        GameScreen.HOME,
        GameScreen.WORLD_MAP,
        GameScreen.EVENTS,
        GameScreen.SHOP,
        GameScreen.SETTINGS
    )

    BackHandler(enabled = currentScreen != GameScreen.HOME && currentScreen != GameScreen.SPLASH) {
        when (currentScreen) {
            GameScreen.GAMEPLAY -> viewModel.navigateTo(GameScreen.WORLD_MAP)
            GameScreen.PET_SANCTUARY, GameScreen.ACHIEVEMENTS -> viewModel.navigateTo(GameScreen.EVENTS)
            else -> viewModel.navigateTo(GameScreen.HOME)
        }
    }

    Scaffold(
        bottomBar = {
            if (isMainTab) {
                KingdomBottomNavBar(
                    currentScreen = currentScreen,
                    onHomeClick = { viewModel.navigateTo(GameScreen.HOME) },
                    onLevelsClick = { viewModel.navigateTo(GameScreen.WORLD_MAP) },
                    onEventsClick = { viewModel.navigateTo(GameScreen.EVENTS) },
                    onShopClick = { viewModel.navigateTo(GameScreen.SHOP) },
                    onSettingsClick = { viewModel.navigateTo(GameScreen.SETTINGS) }
                )
            }
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isMainTab) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            // বইয়ের পাতার মতো মসৃণ পেজ ফ্লিপ ও স্লাইড অ্যানিমেশন
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    val towards = if (targetState.ordinal > initialState.ordinal) {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Right
                    }
                    (slideIntoContainer(
                        towards = towards,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(380)) + scaleIn(initialScale = 0.94f, animationSpec = tween(380)))
                        .togetherWith(
                            slideOutOfContainer(
                                towards = towards,
                                animationSpec = tween(380, easing = FastOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.04f, animationSpec = tween(300))
                        )
                },
                label = "book_page_flip"
            ) { screen ->
                when (screen) {
                    GameScreen.SPLASH -> SplashScreen(viewModel = viewModel)
                    GameScreen.HOME -> HomeScreen(viewModel = viewModel)
                    GameScreen.EVENTS -> EventsScreen(viewModel = viewModel)
                    GameScreen.WORLD_MAP -> WorldMapScreen(viewModel = viewModel)
                    GameScreen.GAMEPLAY -> GameplayScreen(viewModel = viewModel)
                    GameScreen.PET_SANCTUARY -> PetSanctuaryScreen(viewModel = viewModel)
                    GameScreen.SHOP -> BoosterShopScreen(viewModel = viewModel)
                    GameScreen.DAILY_REWARDS -> DailyRewardsScreen(viewModel = viewModel)
                    GameScreen.LUCKY_SPIN -> LuckySpinScreen(viewModel = viewModel)
                    GameScreen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
                    GameScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
