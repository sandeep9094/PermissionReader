package com.developidea.permissionreader.feature.app_list

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
class AppListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppListUiState(isLoading = true))
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps(systemApps: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val apps = withContext(Dispatchers.IO) {
                    appRepository.fetchInstalledApps(systemApps)
                }
                _uiState.value = AppListUiState(
                    apps = apps,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AppListUiState(
                    apps = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Failed to load apps"
                )
            }
        }
    }

    fun refreshApps(systemApps: Boolean = true) {
        loadApps(systemApps)
    }
}

