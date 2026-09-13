package com.jnvst.guru.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.repository.PracticeRepositoryImpl
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProfileViewModel(
    private val repository: PracticeRepository = PracticeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState: StateFlow<CreateProfileUiState> = _uiState.asStateFlow()

    init {
        loadStates()
    }

    private fun loadStates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStates = true) }
            val result = repository.getStates()
            if (result is Resource.Success) {
                _uiState.update { it.copy(states = result.data ?: emptyList(), isLoadingStates = false) }
            } else {
                _uiState.update { it.copy(isLoadingStates = false, error = result.message) }
            }
        }
    }

    fun onStateSelected(stateId: Long) {
        _uiState.update { it.copy(stateId = stateId, districtId = null, districts = emptyList()) }
        loadDistricts(stateId)
    }

    private fun loadDistricts(stateId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDistricts = true) }
            val result = repository.getDistricts(stateId)
            if (result is Resource.Success) {
                _uiState.update { it.copy(districts = result.data ?: emptyList(), isLoadingDistricts = false) }
            } else {
                _uiState.update { it.copy(isLoadingDistricts = false, error = result.message) }
            }
        }
    }

    fun onFieldChange(
        name: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null,
        category: String? = null,
        residentialArea: String? = null,
        classLevel: Int? = null,
        districtId: Long? = null,
        preferredLanguage: String? = null
    ) {
        _uiState.update { current ->
            current.copy(
                name = name ?: current.name,
                dateOfBirth = dateOfBirth ?: current.dateOfBirth,
                gender = gender ?: current.gender,
                category = category ?: current.category,
                residentialArea = residentialArea ?: current.residentialArea,
                classLevel = classLevel ?: current.classLevel,
                districtId = districtId ?: current.districtId,
                preferredLanguage = preferredLanguage ?: current.preferredLanguage,
                error = null
            )
        }
    }

    fun createProfile() {
        val state = _uiState.value
        if (state.name.isBlank() || state.dateOfBirth.isBlank() || state.gender.isBlank() ||
            state.category.isBlank() || state.residentialArea.isBlank() ||
            state.stateId == null || state.districtId == null || state.preferredLanguage.isBlank()) {
            _uiState.update { it.copy(error = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingProfile = true) }
            val result = repository.createStudentProfile(
                name = state.name,
                dateOfBirth = state.dateOfBirth,
                gender = state.gender,
                category = state.category,
                residentialArea = state.residentialArea,
                classLevel = state.classLevel,
                stateId = state.stateId,
                districtId = state.districtId,
                preferredLanguage = state.preferredLanguage,
                examSessionId = state.examSessionId
            )
            
            if (result is Resource.Success) {
                _uiState.update { it.copy(isCreatingProfile = false, profileCreated = true) }
            } else {
                _uiState.update { it.copy(isCreatingProfile = false, error = result.message) }
            }
        }
    }
}
