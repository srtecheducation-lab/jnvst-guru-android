package com.jnvst.guru.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.network.SupabaseClient
import com.jnvst.guru.data.repository.PracticeRepositoryImpl
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: PracticeRepository = PracticeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val email = SupabaseClient.getCurrentUserEmail()
            when (val result = repository.getStudentProfile()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = result.data,
                            email = email,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            email = email,
                            error = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false, email = email) }
                }
            }
        }
    }
}
