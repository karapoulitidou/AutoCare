package com.example.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autocare.ui.theme.AutoCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoCareTheme {
                MainScreen()
            }
        }
    }
}

data class Car(
    val brand: String,
    val model: String,
    val plate: String
)

data class CarAction(
    val carIndex: Int,
    val type: String,
    val date: String,
    val notes: String
)

val carsList = mutableStateListOf(
    Car("Toyota", "Yaris", "NIK-1234"),
    Car("Nissan", "Qashqai", "EET-3456"),
    Car("Audi", "A3", "KZN-7865")
)

val actionsList=mutableStateListOf(
    CarAction(0,"Service","05/04/2026","Αλλαγή λαδιών και φίλτρων"),
    CarAction(1,"ΚΤΕΟ","20/05/2026","Επόμενος τεχνικός έλεγχος")
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("cars") {
            CarsScreen(navController)
        }

        composable("addCar") {
            AddCarScreen(navController)
        }

        composable("details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments
                ?.getString("index")
                ?.toIntOrNull() ?: 0

            CarDetailsScreen(navController, index)
        }

        composable("addAction/{index}") {backStackEntry->
            val index=backStackEntry.arguments
                ?.getString("index")
                ?.toIntOrNull() ?: 0

            AddActionScreen(navController,index)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AutoCare") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Καλώς ήρθες στο AutoCare")

            Button(
                onClick = { navController.navigate("cars") },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Τα οχήματά μου")
            }

            Button(
                onClick = { navController.navigate("addCar") },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Προσθήκη οχήματος")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Τα οχήματά μου") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Πίσω"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            carsList.forEachIndexed { index, car ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            navController.navigate("details/$index")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${car.brand} ${car.model}")
                        Text("Πινακίδα: ${car.plate}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(navController: NavController) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προσθήκη οχήματος") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Μάρκα") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Μοντέλο") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Πινακίδα") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (brand.isNotEmpty() && model.isNotEmpty() && plate.isNotEmpty()) {
                        carsList.add(Car(brand, model, plate))
                        navController.navigate("cars")
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Αποθήκευση")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    navController: NavController,
    index: Int
) {
    val car = carsList.getOrNull(index)
    val carActions = actionsList.filter { it.carIndex == index }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Λεπτομέρειες") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (car != null) {
                Text("Μάρκα: ${car.brand}")
                Text("Μοντέλο: ${car.model}")
                Text("Πινακίδα: ${car.plate}")

                Button(
                    onClick = { navController.navigate("addAction/$index") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Προσθήκη ενέργειας")
                }

                Text(
                    text = "Ιστορικό ενεργειών",
                    modifier = Modifier.padding(top = 24.dp)
                )

                if (carActions.isEmpty()) {
                    Text(
                        text = "Δεν υπάρχουν ακόμα καταχωρήσεις.",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    carActions.forEach { action ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(action.type)
                                Text("Ημερομηνία: ${action.date}")
                                Text("Σημειώσεις: ${action.notes}")
                            }
                        }
                    }
                }
            } else {
                Text("Δεν βρέθηκε το όχημα.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(
    navController: NavController,
    carIndex: Int
) {
    var type by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προσθήκη ενέργειας") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Τύπος ενέργειας π.χ. Service, ΚΤΕΟ, Ασφάλεια") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Ημερομηνία") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Σημειώσεις") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (type.isNotEmpty() && date.isNotEmpty()) {
                        actionsList.add(
                            CarAction(
                                carIndex = carIndex,
                                type = type,
                                date = date,
                                notes = notes
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Αποθήκευση")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    AutoCareTheme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}