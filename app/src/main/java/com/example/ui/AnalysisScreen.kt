package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel
import com.example.feature.signal_pro.SignalProScreen

@Composable
fun AnalysisScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    SignalProScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
