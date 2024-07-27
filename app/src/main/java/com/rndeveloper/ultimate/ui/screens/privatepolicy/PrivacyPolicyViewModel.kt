package com.rndeveloper.ultimate.ui.screens.privatepolicy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeveloper.ultimate.model.PrivacyPolicy
import com.rndeveloper.ultimate.repositories.PrivacyPolicyRepository
import com.rndeveloper.ultimate.utils.Constants
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
class PrivacyPolicyViewModel @Inject constructor(
    private val privacyPolicyRepository: PrivacyPolicyRepository,
) : ViewModel() {

    private val _privatePolicyState = MutableStateFlow(false)
    val uiPrivatePolicyState: StateFlow<Boolean> = _privatePolicyState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    init {
        viewModelScope.launch {
            onGetPrivatePolicyValue()
        }
    }

    private suspend fun onGetPrivatePolicyValue() {
        privacyPolicyRepository.getData(Constants.PRIVATE_POLICY_KEY).collectLatest { privatePolicy ->
            _privatePolicyState.update {
                privatePolicy.isCheck
            }
        }
    }

    fun onSetPrivatePolicyValue(isCheck: Boolean) = viewModelScope.launch {
        PrivacyPolicy(
            id = Constants.PRIVATE_POLICY_KEY,
            isCheck = isCheck
        ).let {
            privacyPolicyRepository.saveData(it)
        }
    }
}