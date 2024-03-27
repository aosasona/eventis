package com.trulyao.eventis.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trulyao.eventis.Database
import com.trulyao.eventis.models.User
import com.trulyao.eventis.models.UserModel
import com.trulyao.eventis.utils.Store
import com.trulyao.eventis.utils.StoreKey
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrDefault

@Composable
fun Settings(database: Database, navController: NavController, userId: Int?) {
    System.err.println("User ID: $userId")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (userId == null) return;

    val user = User(database.readableDatabase).findByID(userId).getOrDefault(UserModel())

    fun signOut() {
        scope.launch {
            Store.set(context, StoreKey.User, null)
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Settings",
                fontSize = 38.sp,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(20.dp)
        ) {
            Text(text = "Username: ${user.username}", modifier = Modifier.padding(vertical = 20.dp))

            Spacer(modifier = Modifier.size(8.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Sign out")
                }
            }
        }
    }
}