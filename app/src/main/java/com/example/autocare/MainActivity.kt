package com.example.autocare

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autocare.ui.theme.AutoCareTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Car(
    val owner: String,
    val brand: String,
    val model: String,
    val plate: String,
    val isBike: Boolean = false
)

data class CarAction(
    val owner: String,
    val carId: String,
    val type: String,
    val date: String,
    val isDocument: Boolean = false,
    val notes: String = "",
    val cost: String = "",
    val kilometers: String = ""
)

data class User(
    val username: String,
    val password: String
)

val carsList = mutableStateListOf<Car>()
val actionsList = mutableStateListOf<CarAction>()
val usersList = mutableStateListOf<User>()

var currentUsername by mutableStateOf("")

val defaultCars = listOf(
    Car("nikos", "Toyota", "Yaris", "NIK-1234"),
    Car("nikos", "Audi", "A3", "KZN-7865"),
    Car("nikos", "Yamaha", "TDM 900", "BTR-456", isBike = true)
)

val defaultActions = listOf(
    CarAction("nikos", "NIK-1234", "Service Λαδιών", "15/06/2026", false, "Αλλαγή λαδιών και φίλτρου", "80€", "75.000 km"),
    CarAction("nikos", "NIK-1234", "Ασφάλεια Anytime", "12/05/2027", true, "Ετήσια ασφάλεια οχήματος", "240€", "-"),
    CarAction("nikos", "KZN-7865", "ΚΤΕΟ", "20/05/2026", false, "Περιοδικός τεχνικός έλεγχος", "50€", "98.000 km"),
    CarAction("nikos", "KZN-7865", "Ασφάλεια Hellas Direct", "14/09/2026", true, "Ανανέωση ασφαλιστηρίου", "210€", "-"),
    CarAction("nikos", "BTR-456", "Ασφάλεια Interamerican", "30/10/2026", true, "Ασφάλεια μοτοσυκλέτας", "160€", "-"),
    CarAction("nikos", "BTR-456", "Έλεγχος Αλυσίδας", "12/08/2026", false, "Έλεγχος και λίπανση αλυσίδας", "25€", "42.000 km")
)

val defaultUsers = listOf(
    User("nikos", "1234")
)

val mainGradient = Brush.verticalGradient(
    listOf(Color(0xFFD1E3FF), Color.White)
)

fun saveData(context: Context) {
    val prefs = context.getSharedPreferences("autocare", Context.MODE_PRIVATE)
    val gson = Gson()

    prefs.edit()
        .putString("cars", gson.toJson(carsList))
        .putString("actions", gson.toJson(actionsList))
        .putString("users", gson.toJson(usersList))
        .apply()
}

fun loadData(context: Context) {
    val prefs = context.getSharedPreferences("autocare", Context.MODE_PRIVATE)
    val gson = Gson()

    val carsJson = prefs.getString("cars", null)
    val actionsJson = prefs.getString("actions", null)
    val usersJson = prefs.getString("users", null)

    carsList.clear()
    actionsList.clear()
    usersList.clear()

    val carsType = object : TypeToken<List<Car>>() {}.type
    val actionsType = object : TypeToken<List<CarAction>>() {}.type
    val usersType = object : TypeToken<List<User>>() {}.type

    if (carsJson == null || actionsJson == null) {
        carsList.addAll(defaultCars)
        actionsList.addAll(defaultActions)
    } else {
        carsList.addAll(gson.fromJson(carsJson, carsType))
        actionsList.addAll(gson.fromJson(actionsJson, actionsType))
    }

    if (usersJson == null) {
        usersList.addAll(defaultUsers)
    } else {
        usersList.addAll(gson.fromJson(usersJson, usersType))
    }

    saveData(context)
}

fun getUserCars(): List<Car> {
    return carsList.filter { it.owner == currentUsername }
}

fun deleteCarAt(userCarIndex: Int, context: Context) {
    val userCars = getUserCars()
    val car = userCars.getOrNull(userCarIndex) ?: return

    carsList.remove(car)
    actionsList.removeAll { it.owner == currentUsername && it.carId == car.plate }

    saveData(context)
}

fun deleteActionAt(actionIndex: Int, context: Context) {
    if (actionIndex in actionsList.indices) {
        actionsList.removeAt(actionIndex)
        saveData(context)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadData(this)

        window.statusBarColor = Color(0xFFD1E3FF).toArgb()
        enableEdgeToEdge()

        setContent {
            AutoCareTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { DynamicHomeScreen(navController) }
        composable("cars") { DynamicCarsScreen(navController) }
        composable("addCar") { AddCarScreen(navController) }

        composable("details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            CarDetailsScreen(navController, index)
        }

        composable("pdfViewer/{actionIndex}") { backStackEntry ->
            val actionIndex = backStackEntry.arguments?.getString("actionIndex")?.toIntOrNull() ?: 0
            PdfViewerScreen(navController, actionIndex)
        }

        composable("addAction/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AddActionScreen(navController, index)
        }

        composable("editCar/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            EditCarScreen(navController, index)
        }

        composable("editAction/{actionIndex}") { backStackEntry ->
            val actionIndex = backStackEntry.arguments?.getString("actionIndex")?.toIntOrNull() ?: 0
            EditActionScreen(navController, actionIndex)
        }
    }
}

fun getNextReminder(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val now = Date()

    val futureActions = actionsList.mapNotNull { action ->
        try {
            val date = sdf.parse(action.date)
            if (date != null && date.after(now) && action.owner == currentUsername) action to date else null
        } catch (e: Exception) {
            null
        }
    }.sortedBy { it.second }

    if (futureActions.isEmpty()) return "Καμία εκκρεμότητα"

    val nearestDate = futureActions.first().second

    val sameDayActions = futureActions.filter {
        sdf.format(it.second) == sdf.format(nearestDate)
    }

    return sameDayActions.joinToString("\n") {
        val car = carsList.find { car -> car.owner == currentUsername && car.plate == it.first.carId }
        "${it.first.type} για το ${car?.brand ?: "όχημα"} στις ${it.first.date}"
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun doLogin() {
        val userExists = usersList.any {
            it.username == u && it.password == p
        }

        if (userExists) {
            currentUsername = u
            navController.navigate("home")
        } else {
            Toast.makeText(context, "Λάθος στοιχεία!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "AutoCare",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                value = u,
                onValueChange = { u = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = p,
                onValueChange = { p = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        doLogin()
                    }
                )
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { doLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ΣΥΝΔΕΣΗ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ΔΗΜΙΟΥΡΓΙΑ ΛΟΓΑΡΙΑΣΜΟΥ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun doRegister() {
        when {
            username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                Toast.makeText(context, "Συμπλήρωσε όλα τα πεδία.", Toast.LENGTH_SHORT).show()
            }

            password != confirmPassword -> {
                Toast.makeText(context, "Οι κωδικοί δεν ταιριάζουν.", Toast.LENGTH_SHORT).show()
            }

            usersList.any { it.username == username } -> {
                Toast.makeText(context, "Το username υπάρχει ήδη.", Toast.LENGTH_SHORT).show()
            }

            else -> {
                usersList.add(User(username, password))
                saveData(context)
                Toast.makeText(context, "Η εγγραφή ολοκληρώθηκε.", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Εγγραφή Χρήστη", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Επιβεβαίωση Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        doRegister()
                    }
                )
            )

            Button(
                onClick = { doRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("ΕΓΓΡΑΦΗ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DynamicHomeScreen(navController: NavController) {
    val reminderText = getNextReminder()
    val recentActions = actionsList
        .filter { it.owner == currentUsername }
        .takeLast(3)
        .reversed()

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                "Καλώς ήρθες, $currentUsername!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Επόμενη Υποχρέωση", color = Color.White.copy(alpha = 0.7f))
                        Text(
                            reminderText,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "Πρόσφατη Δραστηριότητα",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))

            recentActions.forEach { action ->
                val car = carsList.find { it.owner == currentUsername && it.plate == action.carId }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (action.isDocument) Icons.Default.Description else Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            "${action.type} στο ${car?.brand ?: "όχημα"}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text("Στις ${action.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            MenuBtn("Τα οχήματά μου", Icons.Default.Garage) {
                navController.navigate("cars")
            }

            Spacer(Modifier.height(12.dp))

            MenuBtn("Προσθήκη Οχήματος", Icons.Default.AddCircle) {
                navController.navigate("addCar")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    currentUsername = ""

                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ΑΠΟΣΥΝΔΕΣΗ", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

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
fun PdfViewerScreen(navController: NavController, actionIndex: Int) {
    val action = actionsList.getOrNull(actionIndex)
    val car = action?.let { carsList.find { c -> c.owner == action.owner && c.plate == action.carId } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προβολή Αρχείου", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                Modifier.fillMaxSize(),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "ΨΗΦΙΑΚΟ ΑΡΧΕΙΟ ΟΧΗΜΑΤΟΣ",
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    HorizontalDivider(Modifier.padding(vertical = 16.dp))

                    Text(
                        action?.type?.uppercase() ?: "ΕΓΓΡΑΦΟ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1565C0)
                    )

                    Spacer(Modifier.height(32.dp))

                    PdfRow("Ημερ/νία:", action?.date ?: "-")
                    PdfRow("Όχημα:", "${car?.brand} ${car?.model}")
                    PdfRow("Πινακίδα:", car?.plate ?: "-")
                    PdfRow("Κόστος:", action?.cost ?: "-")
                    PdfRow("Χιλιόμετρα:", action?.kilometers ?: "-")
                    PdfRow("Σημειώσεις:", action?.notes ?: "-")
                    PdfRow("Status:", "ΕΠΙΚΥΡΩΜΕΝΟ")

                    Spacer(Modifier.height(40.dp))

                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QrCode2,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color.Black
                            )
                            Text("Ψηφιακή Πιστοποίηση", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, Modifier.width(100.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(navController: NavController) {
    val context = LocalContext.current
    var b by remember { mutableStateOf("") }
    var m by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }

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

            Button(
                onClick = {
                    if (b.isNotBlank() && p.isNotBlank()) {
                        carsList.add(Car(currentUsername, b, m, p))
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

@Composable
fun MenuBtn(t: String, i: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(i, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.width(16.dp))

            Text(t, fontWeight = FontWeight.Bold, fontSize = 17.sp)

            Spacer(Modifier.weight(1f))

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}