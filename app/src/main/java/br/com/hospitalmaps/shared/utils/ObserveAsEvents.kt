package br.com.hospitalmaps.shared.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(
        key1 = lifecycleOwner,
        key2 = key1,
        key3 = key2
    ) {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            withContext(context = Dispatchers.Main.immediate) {
                flow.collect(collector = onEvent)
            }
        }
    }
}