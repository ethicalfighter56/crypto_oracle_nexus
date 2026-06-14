package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel
import com.example.feature.accuracy_center.AccuracyCenterScreen

@Composable
fun AccuracyCenterScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    com.example.feature.accuracy_center.AccuracyCenterScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
