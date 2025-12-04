package com.developidea.permissionreader.feature.app_manifest

data class AppManifestUiState(
    val manifestContent: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

