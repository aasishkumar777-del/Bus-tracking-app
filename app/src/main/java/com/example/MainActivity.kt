package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DriverScreen
import com.example.ui.screens.ParentScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.RouteListScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BusTrackerApp(viewModel)
            }
        }
    }
}

@Composable
fun BusTrackerApp(viewModel: BusViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "role_selection") {
        composable("role_selection") {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    if (role == "parent") {
                        navController.navigate("parent_routes")
                    } else {
                        navController.navigate("driver")
                    }
                }
            )
        }
        
        composable("parent_routes") {
            RouteListScreen(
                onRouteSelected = { routeId ->
                    navController.navigate("parent_track/$routeId")
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable("parent_track/{routeId}") { backStackEntry ->
            ParentScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable("driver") {
            DriverScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
