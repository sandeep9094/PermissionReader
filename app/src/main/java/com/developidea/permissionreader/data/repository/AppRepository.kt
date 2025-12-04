package com.developidea.permissionreader.data.repository

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.developidea.permissionreader.core.model.AppInfo
import com.developidea.permissionreader.data.local.PinnedAppsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val pinnedAppsDataSource: PinnedAppsDataSource
) {
    
    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    fun fetchInstalledApps(systemApps: Boolean = false): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        
        // Get all installed packages including system apps
        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        
        for (packageInfo in packages) {
            try {
                val applicationInfo = packageInfo.applicationInfo
                if (applicationInfo == null) {
                    continue
                }
                
                // Filter system apps based on the parameter
                if (!systemApps && (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                    continue
                }
                
                // Only include apps that have icons
                if (applicationInfo.icon == 0) {
                    continue
                }
                
                val appName = packageManager.getApplicationLabel(applicationInfo).toString()
                
                // Try to get the icon, skip if it fails
                val icon = try {
                    packageManager.getApplicationIcon(applicationInfo.packageName)
                } catch (e: Exception) {
                    // Skip apps without valid icons
                    continue
                }
                
                // Verify icon is not null
                if (icon == null) {
                    continue
                }
                
                val version = packageInfo.versionName ?: "Unknown"
                
                apps.add(
                    AppInfo(
                        icon = icon,
                        name = appName,
                        packageName = applicationInfo.packageName,
                        version = version
                    )
                )
            } catch (e: Exception) {
                // Skip apps that can't be loaded
                continue
            }
        }
        
        // Sort: pinned apps first, then by app name
        val pinnedApps = pinnedAppsDataSource.getPinnedApps()
        return apps.sortedWith(compareBy<AppInfo> { 
            !pinnedApps.contains(it.packageName) 
        }.thenBy { 
            it.name.lowercase() 
        })
    }
    
    fun pinApp(packageName: String) {
        pinnedAppsDataSource.pinApp(packageName)
    }
    
    fun unpinApp(packageName: String) {
        pinnedAppsDataSource.unpinApp(packageName)
    }
    
    fun isPinned(packageName: String): Boolean {
        return pinnedAppsDataSource.isPinned(packageName)
    }
    
    fun getDefaultPinnedApps(): Array<String> {
        return PinnedAppsDataSource.DEFAULT_PINNED_APPS
    }
    
    fun getAppPermissions(packageName: String): List<String> {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )
            packageInfo.requestedPermissions?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getAppManifest(packageName: String): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES or 
                PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS
            )
            val applicationInfo = packageInfo.applicationInfo
            
            val manifest = StringBuilder()
            manifest.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
            manifest.append("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n")
            manifest.append("    package=\"${packageInfo.packageName}\"\n")
            manifest.append("    android:versionCode=\"${packageInfo.versionCode}\"\n")
            manifest.append("    android:versionName=\"${packageInfo.versionName ?: "Unknown"}\">\n\n")
            
            manifest.append("    <uses-sdk\n")
            manifest.append("        android:minSdkVersion=\"${applicationInfo?.minSdkVersion ?: "Unknown"}\"\n")
            manifest.append("        android:targetSdkVersion=\"${applicationInfo?.targetSdkVersion ?: "Unknown"}\" />\n\n")
            
            // Get permissions
            val permissions = getAppPermissions(packageName)
            if (permissions.isNotEmpty()) {
                permissions.forEach { permission ->
                    manifest.append("    <uses-permission android:name=\"$permission\" />\n")
                }
                manifest.append("\n")
            }
            
            if (applicationInfo == null) {
                return "Error: ApplicationInfo is null"
            }
            
            manifest.append("    <application\n")
            manifest.append("        android:label=\"${escapeXml(packageManager.getApplicationLabel(applicationInfo).toString())}\"\n")
            manifest.append("        android:name=\"${escapeXml(applicationInfo.className ?: "")}\"\n")
            manifest.append("        android:icon=\"@mipmap/ic_launcher\">\n\n")
            
            // Get activities
            val activities = packageInfo.activities
            if (activities != null && activities.isNotEmpty()) {
                activities.forEach { activity ->
                    manifest.append("        <activity\n")
                    manifest.append("            android:name=\"${escapeXml(activity.name)}\"\n")
                    if (activity.exported) {
                        manifest.append("            android:exported=\"true\" />\n")
                    } else {
                        manifest.append("            android:exported=\"false\" />\n")
                    }
                }
                manifest.append("\n")
            }
            
            // Get services
            val services = packageInfo.services
            if (services != null && services.isNotEmpty()) {
                services.forEach { service ->
                    manifest.append("        <service\n")
                    manifest.append("            android:name=\"${escapeXml(service.name)}\" />\n")
                }
                manifest.append("\n")
            }
            
            // Get receivers
            val receivers = packageInfo.receivers
            if (receivers != null && receivers.isNotEmpty()) {
                receivers.forEach { receiver ->
                    manifest.append("        <receiver\n")
                    manifest.append("            android:name=\"${escapeXml(receiver.name)}\" />\n")
                }
                manifest.append("\n")
            }
            
            manifest.append("    </application>\n")
            manifest.append("</manifest>")
            
            manifest.toString()
        } catch (e: Exception) {
            "Error loading manifest: ${e.message}"
        }
    }
    
    fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            val applicationInfo = packageInfo.applicationInfo
            if(applicationInfo == null) {
                return null
            }
            
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            val version = packageInfo.versionName ?: "Unknown"
            
            AppInfo(
                icon = icon,
                name = appName,
                packageName = packageName,
                version = version
            )
        } catch (e: Exception) {
            null
        }
    }
}

