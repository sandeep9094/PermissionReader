package com.developidea.permissionreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.developidea.permissionreader.feature.app_info.AppInfoScreen
import com.developidea.permissionreader.feature.app_list.AppListScreen
import com.developidea.permissionreader.feature.app_manifest.AppManifestScreen

sealed class Screen(val route: String) {
    object AppList : Screen("app_list")
    object AppInfo : Screen("app_info/{packageName}") {
        fun createRoute(packageName: String) = "app_info/$packageName"
    }
    object Manifest : Screen("manifest/{packageName}") {
        fun createRoute(packageName: String) = "manifest/$packageName"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AppList.route,
        modifier = modifier
    ) {
        composable(Screen.AppList.route) {
            AppListScreen(
                onAppClick = { app ->
                    navController.navigate(Screen.AppInfo.createRoute(app.packageName))
                }
            )
        }
        
        composable(
            route = Screen.AppInfo.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            AppInfoScreen(
                packageName = packageName,
                onReadManifestClick = {
                    navController.navigate(Screen.Manifest.createRoute(packageName))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Manifest.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            AppManifestScreen(
                packageName = packageName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

