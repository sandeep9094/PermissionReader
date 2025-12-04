package com.developidea.permissionreader.feature.app_list

import com.developidea.permissionreader.core.model.AppInfo

data class AppListUiState(
    val apps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

