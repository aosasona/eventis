package com.trulyao.eventis.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trulyao.eventis.Database
import com.trulyao.eventis.utils.Store
import com.trulyao.eventis.utils.StoreKey
import com.trulyao.eventis.views.auth.SignIn
import com.trulyao.eventis.views.auth.SignUp

@Composable
fun Root() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val signedInUser: Int? by Store.get(context, key = StoreKey.User, default = null)
        .collectAsState(initial = 0)

    if (signedInUser == 0) {
        return
    }

    val database = Database(context)

    Scaffold(
        bottomBar = {
            if (signedInUser !== null) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(
                            onClick = { navController.navigate(Views.Home.name) },
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Events",
                            )
                        }

                        IconButton(onClick = { navController.navigate(Views.Settings.name) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(contentPadding),
            startDestination = if (signedInUser !== null) {
                Views.Home.name
            } else {
                Views.SignUp.name
            }
        ) {
            composable(Views.SignUp.name) {
                SignUp(database = database, navController)
            }

            composable(Views.SignIn.name) {
                SignIn(database = database, navController)
            }

            composable(Views.Home.name) {
                Home(
                    database = database,
                    navController = navController,
                    userId = signedInUser
                )
            }

            composable(Views.Settings.name) {
                Settings(
                    database = database,
                    navController = navController,
                    userId = signedInUser
                )
            }
        }
    }
}