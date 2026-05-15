package com.example.autocare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(navController: NavController, carIndex: Int) {
    val context = LocalContext.current
    val car = getUserCars().getOrNull(carIndex)

    var t by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var kilometers by remember { mutableStateOf("") }
    var isDoc by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προσθήκη στο Ιστορικό", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("Τίτλος") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text("Ημερομηνία") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Σημειώσεις") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Κόστος") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = kilometers, onValueChange = { kilometers = it }, label = { Text("Χιλιόμετρα") }, modifier = Modifier.fillMaxWidth())

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Είναι αρχείο / PDF;", Modifier.weight(1f))
                Switch(checked = isDoc, onCheckedChange = { isDoc = it })
            }

            Button(
                onClick = {
                    if (t.isNotBlank() && car != null) {
                        actionsList.add(
                            CarAction(
                                owner = currentUsername,
                                carId = car.plate,
                                type = t,
                                date = d,
                                isDocument = isDoc,
                                notes = notes,
                                cost = cost,
                                kilometers = kilometers
                            )
                        )
                        saveData(context)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("ΑΠΟΘΗΚΕΥΣΗ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActionScreen(navController: NavController, actionIndex: Int) {
    val context = LocalContext.current
    val action = actionsList.getOrNull(actionIndex)

    if (action == null) {
        Text("Δεν βρέθηκε η ενέργεια.")
        return
    }

    var t by remember { mutableStateOf(action.type) }
    var d by remember { mutableStateOf(action.date) }
    var notes by remember { mutableStateOf(action.notes) }
    var cost by remember { mutableStateOf(action.cost) }
    var kilometers by remember { mutableStateOf(action.kilometers) }
    var isDoc by remember { mutableStateOf(action.isDocument) }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Επεξεργασία Ενέργειας", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("Τίτλος") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text("Ημερομηνία") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Σημειώσεις") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Κόστος") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = kilometers, onValueChange = { kilometers = it }, label = { Text("Χιλιόμετρα") }, modifier = Modifier.fillMaxWidth())

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Είναι αρχείο / PDF;", Modifier.weight(1f))
                Switch(checked = isDoc, onCheckedChange = { isDoc = it })
            }

            Button(
                onClick = {
                    actionsList[actionIndex] = action.copy(
                        type = t,
                        date = d,
                        notes = notes,
                        cost = cost,
                        kilometers = kilometers,
                        isDocument = isDoc
                    )
                    saveData(context)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("ΑΠΟΘΗΚΕΥΣΗ ΑΛΛΑΓΩΝ", fontWeight = FontWeight.Bold)
            }
        }
    }
}