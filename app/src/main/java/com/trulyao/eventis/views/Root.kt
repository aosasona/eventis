package com.trulyao.eventis.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.trulyao.eventis.Database
import com.trulyao.eventis.utils.Store
import com.trulyao.eventis.utils.StoreKey

@Composable
fun Root() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val signedInUser by Store.get(context, key = StoreKey.User, default = null)
        .collectAsState(initial = 0)

    val database = Database(context)

    NavHost(
        navController = navController,
        startDestination = if (signedInUser !== null && signedInUser != 0) {
            Views.Home.name
        } else {
            Views.SignUp.name
        }
    ) {
    }
}