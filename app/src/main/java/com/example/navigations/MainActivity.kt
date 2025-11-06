 package com.example.navigations

 import android.os.Bundle
 import androidx.activity.ComponentActivity
 import androidx.activity.compose.setContent
 import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.padding
 import androidx.compose.material3.Button
 import androidx.compose.material3.Scaffold
 import androidx.compose.material3.Text
 import androidx.compose.material3.NavigationBar
 import androidx.compose.material3.NavigationBarItem
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Home
 import androidx.compose.material.icons.filled.Info
 import androidx.compose.material.icons.filled.Settings
 import androidx.compose.material3.Icon
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.unit.dp
 import androidx.navigation.NavHostController
 import androidx.navigation.compose.NavHost
 import androidx.navigation.compose.composable
 import androidx.navigation.compose.currentBackStackEntryAsState
 import androidx.navigation.compose.rememberNavController
 import androidx.navigation.navigation

 /**
  * =================================================================
  * Application Entry Point (MainActivity) - REQUIRED
  * =================================================================
  * This is the standard entry point that loads the Compose UI.
  */
 class MainActivity : ComponentActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContent {
             // Use a simple theme wrapper
             MaterialTheme {
                 Surface(
                     modifier = Modifier.fillMaxSize(),
                     color = MaterialTheme.colorScheme.background
                 ) {
                     AppNavigation()
                 }
             }
         }
     }
 }


 /**
  * =================================================================
  * 1. Defining Routes/Destinations
  * =================================================================
  * Using sealed classes for type-safe navigation is the best practice.
  */
 sealed class Screen(val route: String, val title: String? = null) {
     // Linear
     object LinearScreenA : Screen("linear_a")
     object LinearScreenB : Screen("linear_b")
     object LinearScreenC : Screen("linear_c")

     // Hierarchical
     object Dashboard : Screen("dashboard", "Dashboard")
     object SettingsRoot : Screen("settings_root") // Graph start
     object GeneralSettings : Screen("general_settings", "General")
     object AccountSettings : Screen("account_settings", "Account")

     // Lateral (Bottom Nav)
     object Home : Screen("home", "Home")
     object Profile : Screen("profile", "Profile")
     object Search : Screen("search", "Search")
 }

 /**
  * =================================================================
  * Main Navigation Setup (NavHost)
  * =================================================================
  */
 @Composable
 fun AppNavigation() {
     val navController = rememberNavController()

     Scaffold(
         bottomBar = {
             // Integrating Lateral Navigation (Bottom Bar)
             AppBottomBar(navController)
         }
     ) { paddingValues ->
         NavHost(
             navController = navController,
             startDestination = Screen.LinearScreenA.route,
             modifier = Modifier.padding(paddingValues)
         ) {
             // ----------------------------------------------------
             // A. Linear Navigation: Sequential Flow
             // ----------------------------------------------------
             composable(Screen.LinearScreenA.route) {
                 LinearScreen(
                     name = "A",
                     nextDestination = Screen.LinearScreenB.route,
                     navController = navController
                 )
             }
             composable(Screen.LinearScreenB.route) {
                 LinearScreen(
                     name = "B",
                     nextDestination = Screen.LinearScreenC.route,
                     navController = navController
                 )
             }
             composable(Screen.LinearScreenC.route) {
                 FinalScreen(navController = navController)
             }


             // ----------------------------------------------------
             // B. Hierarchical Navigation (Nested Graph)
             // ----------------------------------------------------
             // Defines a main entry point (Dashboard) and a sub-graph (Settings)
             composable(Screen.Dashboard.route) {
                 DashboardScreen(navController = navController)
             }

             // The 'navigation' block defines the hierarchical relationship.
             // All screens inside this graph are children of the SettingsRoot.
             navigation(
                 startDestination = Screen.GeneralSettings.route,
                 route = Screen.SettingsRoot.route
             ) {
                 composable(Screen.GeneralSettings.route) {
                     HierarchicalScreen(
                         name = "General Settings (Parent)",
                         navController = navController,
                         // Navigating down the hierarchy (e.g., to Account)
                         nextDestination = Screen.AccountSettings.route
                     )
                 }
                 composable(Screen.AccountSettings.route) {
                     HierarchicalScreen(
                         name = "Account Details (Child)",
                         navController = navController,
                         // When the system 'Up' button is pressed, the Navigation Component
                         // automatically pops back up to the GeneralSettings route (the parent).
                         nextDestination = Screen.Dashboard.route // Example of leaving the hierarchy
                     )
                 }
             }

             // ----------------------------------------------------
             // C. Lateral Navigation (Screens under Bottom Bar)
             // ----------------------------------------------------
             composable(Screen.Home.route) { LateralScreen("Home") }
             composable(Screen.Profile.route) { LateralScreen("Profile") }
             composable(Screen.Search.route) { LateralScreen("Search") }
         }
     }
 }

 // =================================================================
// 2. Linear Navigation Implementation
// =================================================================
 @Composable
 fun LinearScreen(name: String, nextDestination: String, navController: NavHostController) {
     Column(
         modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text("Screen $name")
         Button(onClick = {
             // Simple sequential navigation call
             navController.navigate(nextDestination)
         }) {
             Text("Go to Next Screen")
         }
     }
 }

 @Composable
 fun FinalScreen(navController: NavHostController) {
     Column(
         modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text("Linear Path Complete (Screen C)")
         // Example of Back Stack Management (Implicit)
         Button(onClick = {
             // Pressing the system Back button or calling navigateUp() will
             // automatically pop C, then B, then A.
             navController.popBackStack(Screen.LinearScreenA.route, inclusive = false)
         }) {
             Text("Pop to Screen A (via popBackStack)")
         }
     }
 }


 // =================================================================
// 3. Hierarchical Navigation Implementation
// =================================================================
 @Composable
 fun DashboardScreen(navController: NavHostController) {
     Column(
         modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text("Dashboard (Top Level)")
         Button(onClick = {
             // Navigating into the Settings sub-hierarchy (Parent/Child structure)
             navController.navigate(Screen.SettingsRoot.route)
         }) {
             Text("Go to Settings")
         }
     }
 }

 @Composable
 fun HierarchicalScreen(name: String, navController: NavHostController, nextDestination: String) {
     Column(
         modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text("Hierarchical: $name")
         Button(onClick = {
             // Navigating down to a child screen
             navController.navigate(nextDestination)
         }) {
             Text("Navigate Deeper")
         }
         // NOTE: The 'Up' button (usually in the app bar) automatically calls navigateUp()
         // and handles the move back to the parent destination defined in the NavHost.
     }
 }


 // =================================================================
// 4. Lateral Navigation Implementation (Bottom Navigation Bar)
// =================================================================
 val lateralDestinations = listOf(Screen.Home, Screen.Profile, Screen.Search)

 @Composable
 fun AppBottomBar(navController: NavHostController) {
     NavigationBar {
         val navBackStackEntry by navController.currentBackStackEntryAsState()
         val currentRoute = navBackStackEntry?.destination?.route

         lateralDestinations.forEach { screen ->
             NavigationBarItem(
                 icon = {
                     Icon(
                         imageVector = when(screen) {
                             Screen.Home -> Icons.Filled.Home
                             Screen.Profile -> Icons.Filled.Info
                             Screen.Search -> Icons.Filled.Settings
                             else -> Icons.Filled.Home // Placeholder for others
                         },
                         contentDescription = screen.title
                     )
                 },
                 label = { Text(screen.title ?: screen.route) },
                 selected = currentRoute == screen.route,
                 onClick = {
                     // C. Best Practice for Lateral Navigation (Re-selecting a tab)
                     navController.navigate(screen.route) {
                         // Back Stack Management (Key for Lateral Navigation)
                         // This ensures that when moving between tabs (lateral),
                         // the back stack is cleared up to the start of the app,
                         // so pressing 'Back' exits the app, not switches tabs.

                         // 1. Pop up to the start destination of the graph to avoid
                         // building up a large stack of destinations on the back stack as you switch tabs.
                         popUpTo(navController.graph.startDestinationId) {
                             saveState = true
                         }
                         // 2. Avoid multiple copies of the same destination when re-selecting the same item
                         launchSingleTop = true
                         // 3. Restore state when re-selecting a previously selected item
                         restoreState = true
                     }
                 }
             )
         }
     }
 }

 @Composable
 fun LateralScreen(name: String) {
     Column(
         modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text("Lateral Screen: $name")
     }
 }

 /**
  * =================================================================
  * D. Explicit Back Stack Management (Demonstrated in AppNavigation)
  * =================================================================
  *
  * This is primarily achieved using the 'popUpTo' and 'inclusive' arguments in the
  * navController.navigate() function (or within a NavOptions builder).
  *
  * Example from the AppBottomBar:
  * navController.navigate(screen.route) {
  * // Pop up to the start destination of the graph
  * popUpTo(navController.graph.startDestinationId) {
  * // 'inclusive = true' would remove the start destination as well.
  * // 'saveState = true' saves the state of the popped screens.
  * inclusive = false
  * saveState = true
  * }
  * launchSingleTop = true // Don't create a new instance if already on top
  * }
  *
  * Example from FinalScreen (Screen C):
  * navController.popBackStack(Screen.LinearScreenA.route, inclusive = false)
  * // Pops destinations off the stack until it reaches Screen A.
  * // 'inclusive = false' means Screen A remains on the stack.
  */