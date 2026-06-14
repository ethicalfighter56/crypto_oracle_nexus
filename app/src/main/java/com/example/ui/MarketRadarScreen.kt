package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel
import com.example.feature.live_radar.LiveRadarScreen

@Composable
fun MarketRadarScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    LiveRadarScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
