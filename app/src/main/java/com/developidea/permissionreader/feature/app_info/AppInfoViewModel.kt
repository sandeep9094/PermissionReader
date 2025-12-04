package com.developidea.permissionreader.feature.app_info

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
class AppInfoViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppInfoUiState(isLoading = true))
    val uiState: StateFlow<AppInfoUiState> = _uiState.asStateFlow()

    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val appInfo = withContext(Dispatchers.IO) {
                    appRepository.getAppInfo(packageName)
                }
                
                val permissions = withContext(Dispatchers.IO) {
                    appRepository.getAppPermissions(packageName)
                }
                
                _uiState.value = AppInfoUiState(
                    appInfo = appInfo,
                    permissions = permissions,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AppInfoUiState(
                    appInfo = null,
                    permissions = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Failed to load app info"
                )
            }
        }
    }
}

