package com.developidea.permissionreader.core.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val icon: Drawable?,
    val name: String,
    val packageName: String,
    val version: String
)

