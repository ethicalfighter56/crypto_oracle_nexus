package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel

@Composable
fun MissionCenterScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    com.example.feature.mission_center.MissionCenterScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
