package br.com.hospitalmaps.presentation.main.view

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.hospitalmaps.navigation.Route
import br.com.hospitalmaps.presentation.home.view.HomeScreen
import br.com.hospitalmaps.presentation.locationpermission.view.LocationPermissionScreen
import br.com.hospitalmaps.presentation.locationpermission.view.hasLocationPermissions
import br.com.hospitalmaps.presentation.main.viewmodel.MainViewModel
import br.com.hospitalmaps.presentation.navigation.NavigationScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val shouldInvokeHome by viewModel.shouldInvokeHome.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = if (hasLocationPermissions(context)) {
            Route.Home
        } else {
            Route.LocationPermission
        }
    ) {
        composable<Route.LocationPermission> {
            LocationPermissionScreen(
                onLocationPermissionGranted = {
                    if (shouldInvokeHome) {
                        viewModel.setShouldInvokeHome(false)
                        navController.navigate(route = Route.Home)
                    }
                }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onBackButtonClick = {
                    context.getActivity()?.finish()
                },
                onNavigate = { placeId ->
                    Log.d("MainScreen", "Navigating to place ID: $placeId")
                    navController.currentBackStackEntry?.savedStateHandle?.set("destinationPlaceId", placeId)
                    navController.navigate(Route.Navigation)
                }
            )
        }
        composable<Route.Navigation> {
            NavigationScreen(navController)
        }
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}