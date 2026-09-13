package com.jnvst.guru.ui.profile

import com.jnvst.guru.domain.model.District
import com.jnvst.guru.domain.model.State

data class CreateProfileUiState(
    val name: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val category: String = "",
    val residentialArea: String = "",
    val classLevel: Int = 6,
    val stateId: Long? = null,
    val districtId: Long? = null,
    val preferredLanguage: String = "en",
    val examSessionId: Long = 1,
    
    val states: List<State> = emptyList(),
    val districts: List<District> = emptyList(),
    
    val isLoadingStates: Boolean = false,
    val isLoadingDistricts: Boolean = false,
    val isCreatingProfile: Boolean = false,
    
    val error: String? = null,
    val profileCreated: Boolean = false
)
