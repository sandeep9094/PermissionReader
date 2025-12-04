package com.developidea.permissionreader.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinnedAppsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "pinned_apps_prefs"
        private const val KEY_PINNED_APPS = "pinned_apps"
        
        // Default pinned apps - common system/important apps
        val DEFAULT_PINNED_APPS = arrayOf(
            "com.android.settings",
        )
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun getPinnedApps(): Set<String> {
        val userPinnedSet = prefs.getStringSet(KEY_PINNED_APPS, null) ?: emptySet()
        // Always merge with default pinned apps
        return (DEFAULT_PINNED_APPS.toSet() + userPinnedSet)
    }
    
    fun pinApp(packageName: String) {
        val userPinnedSet = prefs.getStringSet(KEY_PINNED_APPS, null)?.toMutableSet() ?: mutableSetOf()
        userPinnedSet.add(packageName)
        savePinnedApps(userPinnedSet)
    }
    
    fun unpinApp(packageName: String) {
        // Don't allow unpinning default apps
        if (packageName in DEFAULT_PINNED_APPS) {
            return
        }
        val userPinnedSet = prefs.getStringSet(KEY_PINNED_APPS, null)?.toMutableSet() ?: mutableSetOf()
        userPinnedSet.remove(packageName)
        savePinnedApps(userPinnedSet)
    }
    
    fun isPinned(packageName: String): Boolean {
        return getPinnedApps().contains(packageName)
    }
    
    private fun savePinnedApps(userPinnedApps: Set<String>) {
        // Only save user-pinned apps (excluding defaults)
        val userOnly = userPinnedApps.filter { it !in DEFAULT_PINNED_APPS }.toSet()
        prefs.edit()
            .putStringSet(KEY_PINNED_APPS, userOnly)
            .apply()
    }
    
    fun resetToDefaults() {
        // Clear user-pinned apps, keeping only defaults
        prefs.edit()
            .putStringSet(KEY_PINNED_APPS, emptySet())
            .apply()
    }
}

