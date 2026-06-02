package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AnalysisScreen
import com.example.ui.HomeScreen
import com.example.ui.MarketRadarScreen
import com.example.ui.AccuracyCenterScreen
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CryptoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CryptoViewModel = viewModel()
            val currentScreen by viewModel.currentScreen.collectAsState()
            
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor))
                    ) {
                        // Home Tab
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Home,
                            onClick = { viewModel.navigateTo(AppScreen.Home) },
                            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home index") },
                            label = { Text("Oracle Feed", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CryptoCyan,
                                selectedTextColor = CryptoCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CryptoCyan.copy(alpha = 0.12f)
                            )
                        )

                        // Analysis Tab
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Analysis,
                            onClick = { viewModel.navigateTo(AppScreen.Analysis) },
                            icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Oracle signals Pro") },
                            label = { Text("Signals Pro", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CryptoCyan,
                                selectedTextColor = CryptoCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CryptoCyan.copy(alpha = 0.12f)
                            )
                        )

                        // Radar Tab
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.MarketRadar,
                            onClick = { viewModel.navigateTo(AppScreen.MarketRadar) },
                            icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "Live indicators radar") },
                            label = { Text("Live Radar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CryptoCyan,
                                selectedTextColor = CryptoCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CryptoCyan.copy(alpha = 0.12f)
                            )
                        )

                        // Accuracy & History Tab
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.AccuracyCenter,
                            onClick = { viewModel.navigateTo(AppScreen.AccuracyCenter) },
                            icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "Accuracy tracking metrics") },
                            label = { Text("Stats Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CryptoCyan,
                                selectedTextColor = CryptoCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CryptoCyan.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(DarkBackground)
                ) {
                    when (currentScreen) {
                        AppScreen.Home -> HomeScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        AppScreen.Analysis -> AnalysisScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        AppScreen.MarketRadar -> MarketRadarScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        AppScreen.AccuracyCenter -> AccuracyCenterScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
