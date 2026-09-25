package com.jnvst.guru.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.repository.PracticeRepositoryImpl
import com.jnvst.guru.domain.model.ProgressResponse
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val repository: PracticeRepository = PracticeRepositoryImpl()
) : ViewModel() {

    private val _progress = MutableStateFlow<Resource<ProgressResponse>>(Resource.Loading())
    val progress: StateFlow<Resource<ProgressResponse>> = _progress.asStateFlow()

    fun loadProgress() {
        viewModelScope.launch {
            if (_progress.value.data == null) {
                _progress.value = Resource.Loading()
            }
            _progress.value = repository.getProgress(0, 20)
        }
    }
}
