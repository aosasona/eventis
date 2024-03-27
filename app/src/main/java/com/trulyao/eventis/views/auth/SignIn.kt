package com.trulyao.eventis.views.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trulyao.eventis.Database
import com.trulyao.eventis.models.User
import com.trulyao.eventis.utils.AppException
import com.trulyao.eventis.utils.Store
import com.trulyao.eventis.utils.StoreKey
import com.trulyao.eventis.utils.handleException
import com.trulyao.eventis.views.Views
import kotlinx.coroutines.launch

@Composable
fun SignIn(database: Database, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    val enableSignUpButton by remember {
        derivedStateOf {
            username.replace(Regex("/[^a-zA-Z0-9_]/"), "").length > 2 && password.length >= 6
        }
    }

    fun handleSignIn() {
        scope.launch {
            try {
                isLoading = true
                if (username.length < 3) throw AppException("Username must be at least 3 alphanumeric characters")
                if (password.length < 6) throw AppException("Password must be at least 6 characters")

                val user = User(database.writableDatabase)
                val targetUser = user.findByUsername(username)

                if (!targetUser.isPresent) {
                    throw AppException("Invalid credentials provided!")
                }

                if (!user.verifyPassword(
                        password = password,
                        hashedPassword = targetUser.get().password!!
                    )
                ) {
                    throw AppException("Invalid credentials provided!")
                }

                Store.set(context, StoreKey.User, targetUser.get().id)
            } catch (e: Exception) {
                handleException(context, e)
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Sign In", fontSize = 32.sp)

        Spacer(modifier = Modifier.size(20.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Username") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(32.dp))

        Button(
            onClick = { handleSignIn() },
            shape = RoundedCornerShape(6.dp),
            enabled = enableSignUpButton && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Sign in", modifier = Modifier.padding(vertical = 10.dp))
        }

        Spacer(modifier = Modifier.size(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.navigate(Views.SignUp.name) }) {
                Text("Create an account")
            }
        }
    }
}