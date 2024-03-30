package com.trulyao.eventis.views

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trulyao.eventis.Database
import com.trulyao.eventis.models.Event
import com.trulyao.eventis.models.EventModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(database: Database, navController: NavController, userId: Int?) {
    if (userId == 0 || userId == null) return;

    val context = LocalContext.current
    val eventsInstance = Event(database.readableDatabase)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val events = remember { mutableStateListOf<EventModel>() }

    // Modal state
    var isLoadingEvents by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var savingEvent by remember { mutableStateOf(false) }
    val enableSaveButton by remember {
        derivedStateOf { title.trim().isNotEmpty() }
    }


    fun load() {
        if (isLoadingEvents) return;
        isLoadingEvents = true
        val userEvents = eventsInstance.findEventsByUserID(userId)
        events.clear()
        events.addAll(userEvents)
        isLoadingEvents = false
    }

    fun saveEvent() {
        scope.launch {
            savingEvent = false
            val event = Event(database.writableDatabase)
            if (userId == 0 || userId == null) return@launch;
            event.createEvent(title, description, userId)
            Toast.makeText(context, "New event added!", Toast.LENGTH_SHORT).show()

            title = ""
            description = ""
            savingEvent = false
            showBottomSheet = false

            load()
        }
    }

    LaunchedEffect("home") {
        load()
    }


    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New event") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { contentPadding ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    Text("Create Event", fontSize = 24.sp, fontWeight = FontWeight(600))
                    Spacer(modifier = Modifier.size(16.dp))

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title") },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(
                        modifier = Modifier
                            .size(12.dp)
                            .fillMaxWidth()
                    )

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        maxLines = 12,
                        placeholder = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (!savingEvent) {
                            Button(onClick = { saveEvent() }, enabled = enableSaveButton) {
                                Text(text = "Create")
                            }
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(contentPadding)) {
            stickyHeader {
                Text(
                    text = "Events",
                    fontSize = 42.sp,
                    fontWeight = FontWeight(700),
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                )
            }

            items(events) { event ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Checkbox(
                        checked = event.isCompleted.value,
                        onCheckedChange = { event.updateIsCompleted(eventsInstance, it) }
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text(text = event.title ?: "")
                }
            }
        }
    }
}