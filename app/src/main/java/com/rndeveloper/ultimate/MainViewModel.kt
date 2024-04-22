package com.rndeveloper.ultimate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeveloper.ultimate.repositories.NetworkConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {


    private val _networkConnectivityState = MutableStateFlow(NetworkConnectivity.Status.Available)
    val uiNetworkConnectivityState: StateFlow<NetworkConnectivity.Status> =
        _networkConnectivityState.asStateFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = NetworkConnectivity.Status.Available
        )

    init {
        viewModelScope.launch {
            onNetworkConnectivity()
        }
    }

    private suspend fun onNetworkConnectivity() {
        networkConnectivity.observe().collectLatest { newNetworkConnectivityUiState ->
            _networkConnectivityState.update {
                newNetworkConnectivityUiState
            }
        }
    }
}