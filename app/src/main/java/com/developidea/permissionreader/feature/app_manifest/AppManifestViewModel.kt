package com.developidea.permissionreader.feature.app_manifest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developidea.permissionreader.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppManifestViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppManifestUiState(isLoading = true))
    val uiState: StateFlow<AppManifestUiState> = _uiState.asStateFlow()

    fun loadManifest(packageName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val manifestContent = withContext(Dispatchers.IO) {
                    appRepository.getAppManifest(packageName)
                }
                
                _uiState.value = AppManifestUiState(
                    manifestContent = manifestContent,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AppManifestUiState(
                    manifestContent = "",
                    isLoading = false,
                    error = e.message ?: "Failed to load manifest"
                )
            }
        }
    }
}

