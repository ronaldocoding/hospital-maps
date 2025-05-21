package br.com.hospitalmaps.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _shouldInvokeHome = MutableStateFlow(true)
    val shouldInvokeHome = _shouldInvokeHome.asStateFlow()

    fun setShouldInvokeHome(shouldInvoke: Boolean) {
        _shouldInvokeHome.value = shouldInvoke
    }
}