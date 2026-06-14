package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.CryptoViewModel
import com.example.feature.oracle_feed.OracleFeedScreen

@Composable
fun HomeScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    OracleFeedScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
