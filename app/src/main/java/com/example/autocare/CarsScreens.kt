package com.example.autocare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicCarsScreen(navController: NavController) {
    val userCars = getUserCars()

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Τα οχήματά μου",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(userCars) { index, car ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            navController.navigate("details/$index")
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (car.isBike)
                                Icons.Default.TwoWheeler
                            else
                                Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text(
                                "${car.brand} ${car.model}",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                car.plate,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(navController: NavController, index: Int) {
    val context = LocalContext.current
    val userCars = getUserCars()
    val car = userCars.getOrNull(index)
    val carActions = actionsList
        .withIndex()
        .filter { it.value.owner == currentUsername && it.value.carId == car?.plate }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Λεπτομέρειες", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            car?.let {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "${it.brand} ${it.model}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Πινακίδα: ${it.plate}", color = Color.White.copy(0.8f))

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { navController.navigate("editCar/$index") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Edit")
                            }

                            Button(
                                onClick = {
                                    deleteCarAt(index, context)
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Ιστορικό",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { navController.navigate("addAction/$index") }) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    itemsIndexed(carActions) { _, indexedAction ->
                        val originalIndex = indexedAction.index
                        val a = indexedAction.value

                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (a.isDocument) navController.navigate("pdfViewer/$originalIndex")
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                headlineContent = { Text(a.type, fontWeight = FontWeight.Bold) },
                                supportingContent = {
                                    Column {
                                        Text("Ημερομηνία: ${a.date}")
                                        if (a.notes.isNotBlank()) Text("Σημειώσεις: ${a.notes}")
                                        if (a.cost.isNotBlank()) Text("Κόστος: ${a.cost}")
                                        if (a.kilometers.isNotBlank()) Text("Χιλιόμετρα: ${a.kilometers}")

                                        Spacer(Modifier.height(8.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(onClick = { navController.navigate("editAction/$originalIndex") }) {
                                                Text("Edit")
                                            }

                                            Button(onClick = { deleteActionAt(originalIndex, context) }) {
                                                Text("Delete")
                                            }
                                        }
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        if (a.isDocument) Icons.Default.PictureAsPdf else Icons.Default.Build,
                                        contentDescription = null,
                                        tint = if (a.isDocument) Color(0xFFD32F2F) else Color.Gray
                                    )
                                },
                                trailingContent = {
                                    if (a.isDocument) Icon(Icons.Default.ChevronRight, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(navController: NavController) {
    val context = LocalContext.current
    var b by remember { mutableStateOf("") }
    var m by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var isBike by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Νέο Όχημα", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(value = b, onValueChange = { b = it }, label = { Text("Μάρκα") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("Μοντέλο") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Πινακίδα") }, modifier = Modifier.fillMaxWidth())
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Είναι μηχανάκι;",
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isBike,
                    onCheckedChange = {
                        isBike = it
                    }
                )
            }
            Button(
                onClick = {
                    if (b.isNotBlank() && p.isNotBlank()) {
                        carsList.add(
                            Car(
                                currentUsername,
                                b,
                                m,
                                p,
                                isBike = isBike
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
fun EditCarScreen(navController: NavController, index: Int) {
    val context = LocalContext.current
    val userCars = getUserCars()
    val car = userCars.getOrNull(index)

    if (car == null) {
        Text("Δεν βρέθηκε το όχημα.")
        return
    }

    var b by remember { mutableStateOf(car.brand) }
    var m by remember { mutableStateOf(car.model) }
    var p by remember { mutableStateOf(car.plate) }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Επεξεργασία Οχήματος", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(value = b, onValueChange = { b = it }, label = { Text("Μάρκα") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("Μοντέλο") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Πινακίδα") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val globalIndex = carsList.indexOf(car)
                    if (globalIndex != -1) {
                        actionsList.replaceAll { action ->
                            if (action.owner == currentUsername && action.carId == car.plate) {
                                action.copy(carId = p)
                            } else {
                                action
                            }
                        }

                        carsList[globalIndex] = car.copy(brand = b, model = m, plate = p)
                        saveData(context)
                    }
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