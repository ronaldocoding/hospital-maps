package br.com.hospitalmaps.presentation.main.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.hospitalmaps.navigation.Route
import br.com.hospitalmaps.presentation.home.view.HomeScreen
import br.com.hospitalmaps.presentation.locationpermission.view.LocationPermissionScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.LocationPermission) {
        composable<Route.LocationPermission> {
            LocationPermissionScreen(
                onLocationPermissionGranted = {
                    navController.navigate(route = Route.Home)
                }
            )
        }
        composable<Route.Home> {
            HomeScreen()
        }
    }
}