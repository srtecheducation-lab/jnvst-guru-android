package com.jnvst.guru.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jnvst.guru.R
import com.jnvst.guru.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    onProfileCreated: () -> Unit,
    viewModel: CreateProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.profileCreated) {
        if (uiState.profileCreated) {
            onProfileCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_profile), fontWeight = FontWeight.Black) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onFieldChange(name = it) },
                label = { Text(stringResource(R.string.label_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = { viewModel.onFieldChange(dateOfBirth = it) },
                label = { Text(stringResource(R.string.label_dob)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2014-05-15") }
            )

            // Gender Dropdown
            DropdownField(
                label = stringResource(R.string.label_gender),
                options = listOf("MALE", "FEMALE", "OTHER"),
                selectedOption = uiState.gender,
                onOptionSelected = { viewModel.onFieldChange(gender = it) }
            )

            // Category Dropdown
            DropdownField(
                label = stringResource(R.string.label_category),
                options = listOf("GENERAL", "OBC", "SC", "ST"),
                selectedOption = uiState.category,
                onOptionSelected = { viewModel.onFieldChange(category = it) }
            )

            // Residential Area
            DropdownField(
                label = stringResource(R.string.label_residential),
                options = listOf("RURAL", "URBAN"),
                selectedOption = uiState.residentialArea,
                onOptionSelected = { viewModel.onFieldChange(residentialArea = it) }
            )

            // Class Level (Default 6)
            OutlinedTextField(
                value = uiState.classLevel.toString(),
                onValueChange = { viewModel.onFieldChange(classLevel = it.toIntOrNull() ?: 6) },
                label = { Text(stringResource(R.string.label_class_level)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Currently only Class 6 is supported
            )

            // State Dropdown
            StateDropdown(
                states = uiState.states,
                selectedStateId = uiState.stateId,
                isLoading = uiState.isLoadingStates,
                onStateSelected = { viewModel.onStateSelected(it) }
            )

            // District Dropdown
            DistrictDropdown(
                districts = uiState.districts,
                selectedDistrictId = uiState.districtId,
                isLoading = uiState.isLoadingDistricts,
                enabled = uiState.stateId != null,
                onDistrictSelected = { viewModel.onFieldChange(districtId = it) }
            )

            // Preferred Language
            DropdownField(
                label = stringResource(R.string.label_language),
                options = listOf("en", "bn"),
                selectedOption = uiState.preferredLanguage,
                onOptionSelected = { viewModel.onFieldChange(preferredLanguage = it) }
            )

            // Exam Session
            OutlinedTextField(
                value = "2025-26",
                onValueChange = {},
                label = { Text(stringResource(R.string.label_exam_session)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.createProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isCreatingProfile,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isCreatingProfile) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.btn_create_profile), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdown(
    states: List<com.jnvst.guru.domain.model.State>,
    selectedStateId: Long?,
    isLoading: Boolean,
    onStateSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedState = states.find { it.id == selectedStateId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!isLoading) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedState?.name ?: if (isLoading) "Loading..." else stringResource(R.string.hint_select),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_state)) },
            trailingIcon = { if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            states.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state.name) },
                    onClick = {
                        onStateSelected(state.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictDropdown(
    districts: List<com.jnvst.guru.domain.model.District>,
    selectedDistrictId: Long?,
    isLoading: Boolean,
    enabled: Boolean,
    onDistrictSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedDistrict = districts.find { it.id == selectedDistrictId }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (!isLoading && enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDistrict?.name ?: if (isLoading) "Loading..." else if (!enabled) "Select State First" else stringResource(R.string.hint_select),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_district)) },
            trailingIcon = { if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            districts.forEach { district ->
                DropdownMenuItem(
                    text = { Text(district.name) },
                    onClick = {
                        onDistrictSelected(district.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
