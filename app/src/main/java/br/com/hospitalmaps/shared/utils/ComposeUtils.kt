package br.com.hospitalmaps.shared.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
fun statusBarHeightDp(): Dp {
    val insets = WindowInsets.statusBars.asPaddingValues()
    return insets.calculateTopPadding()
}

@Composable
fun bottomBarHeightDp(): Dp {
    val insets = WindowInsets.navigationBars.asPaddingValues()
    return insets.calculateBottomPadding()
}
