package com.rndeveloper.ultimate.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.repositories.UserLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userLocationRepository: UserLocationRepository,
) : ViewModel() {

    private val _uiHomeState = MutableStateFlow(HomeUiState())
    val uiHomeState: StateFlow<HomeUiState> = _uiHomeState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            _uiHomeState.update {
                it.copy(loc = userLocationRepository.getLocationsRequest())
            }
        }
    }

}