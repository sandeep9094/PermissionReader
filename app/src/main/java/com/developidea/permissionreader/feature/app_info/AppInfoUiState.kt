package com.developidea.permissionreader.feature.app_info

import com.developidea.permissionreader.core.model.AppInfo

data class AppInfoUiState(
    val appInfo: AppInfo? = null,
    val permissions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

