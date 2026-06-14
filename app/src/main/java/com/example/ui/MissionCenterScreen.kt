package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel
import com.example.feature.mission_center.MissionCenterScreen

@Composable
fun MissionCenterScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    MissionCenterScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
