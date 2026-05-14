package com.example.autocare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autocare.ui.theme.AutoCareTheme

// --- Models ---
data class Car(val brand: String, val model: String, val plate: String)
data class CarAction(val carIndex: Int, val type: String, val date: String)

// Global State
val carsList = mutableStateListOf(
    Car("Toyota", "Yaris", "NIK-1234"),
    Car("Audi", "A3", "KZN-7865")
)
val actionsList = mutableStateListOf(
    CarAction(0, "Service", "15/06/2026")
)

// Το κοινό Gradient που χρησιμοποιούμε σε όλες τις οθόνες
val mainGradient = Brush.verticalGradient(listOf(Color(0xFFD1E3FF), Color.White))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        composable("home") { DynamicHomeScreen(navController) }
        composable("cars") { DynamicCarsScreen(navController) }
        composable("addCar") { AddCarScreen(navController) }
        composable("details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            CarDetailsScreen(navController, index)
        }
        composable("pdfViewer/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PdfViewerScreen(navController, index)
        }
        composable("addAction/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AddActionScreen(navController, index)
        }
    }
}

// --- 1. LOGIN SCREEN (Συντονισμένη με το υπόλοιπο design) ---
@Composable
fun LoginScreen(navController: NavController) {
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(mainGradient)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Text("AutoCare", color = MaterialTheme.colorScheme.primary, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                value = u, onValueChange = { u = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = p, onValueChange = { p = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (u == "nikos" && p == "1234") navController.navigate("home")
                    else Toast.makeText(context, "Λάθος στοιχεία!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ΣΥΝΔΕΣΗ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- 2. HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicHomeScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text("Καλώς ήρθες, Νίκο!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(24.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Υπενθύμιση", color = Color.White.copy(alpha = 0.7f))
                        Text("Service σε 14 μέρες", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.NotificationsActive, null, tint = Color.White)
                }
            }
            Spacer(Modifier.height(32.dp))
            MenuBtn("Τα οχήματά μου", Icons.Default.Garage) { navController.navigate("cars") }
            Spacer(Modifier.height(12.dp))
            MenuBtn("Προσθήκη Οχήματος", Icons.Default.AddCircle) { navController.navigate("addCar") }
        }
    }
}

// --- 3. CAR LIST SCREEN (Λευκές Κάρτες) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicCarsScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Τα οχήματά μου", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            itemsIndexed(carsList) { index, car ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { navController.navigate("details/$index") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White), // Λευκό φόντο
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("${car.brand} ${car.model}", fontWeight = FontWeight.Bold)
                            Text(car.plate, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// --- 4. CAR DETAILS SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(navController: NavController, index: Int) {
    val car = carsList.getOrNull(index)
    val carActions = actionsList.filter { it.carIndex == index }
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Λεπτομέρειες", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            car?.let {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Column(Modifier.padding(24.dp)) {
                        Text("${it.brand} ${it.model}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Πινακίδα: ${it.plate}", color = Color.White.copy(0.8f))
                    }
                }
                Spacer(Modifier.height(20.dp))

                Surface(Modifier.fillMaxWidth().height(70.dp).clickable { navController.navigate("pdfViewer/$index") }, shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFFD32F2F))
                        Spacer(Modifier.width(16.dp))
                        Text("Προβολή Ασφάλειας (PDF)", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ιστορικό Service", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate("addAction/$index") }) { Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary) }
                }
                LazyColumn {
                    itemsIndexed(carActions) { _, a -> ListItem(headlineContent = { Text(a.type, fontWeight = FontWeight.Bold) }, supportingContent = { Text(a.date) }) }
                }
            }
        }
    }
}

// --- 5. PDF VIEWER SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(navController: NavController, index: Int) {
    val car = carsList.getOrNull(index)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προβολή Εγγράφου", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(24.dp)) {
                    Text("ΕΛΛΗΝΙΚΗ ΔΗΜΟΚΡΑΤΙΑ", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Divider(Modifier.padding(vertical = 16.dp))
                    Text("ΑΣΦΑΛΙΣΤΙΚΟ ΣΥΜΒΟΛΑΙΟ", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1565C0))
                    Spacer(Modifier.height(32.dp))
                    PdfRow("Ιδιοκτήτης:", "ΝΙΚΟΣ ΠΑΠΑΔΟΠΟΥΛΟΣ")
                    PdfRow("Όχημα:", "${car?.brand} ${car?.model}")
                    PdfRow("Πινακίδα:", car?.plate ?: "")
                    PdfRow("Λήξη:", "14/05/2027")
                    Spacer(Modifier.height(40.dp))
                    Box(Modifier.size(100.dp).background(Color(0xFFE8F5E9), CircleShape).align(Alignment.End), contentAlignment = Alignment.Center) {
                        Text("ΕΓΚΥΡΟ", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PdfRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(100.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

// --- 6. ADD CAR SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(navController: NavController) {
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
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(b, { b = it }, label = { Text("Μάρκα") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(m, { m = it }, label = { Text("Μοντέλο") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(p, { p = it }, label = { Text("Πινακίδα") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Button(onClick = { if(b.isNotBlank()) { carsList.add(Car(b, m, p)); navController.popBackStack() } }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("ΑΠΟΘΗΚΕΥΣΗ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- 7. ADD ACTION SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(navController: NavController, carIndex: Int) {
    var t by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Καταγραφή Service", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(t, { t = it }, label = { Text("Εργασία") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(d, { d = it }, label = { Text("Ημερομηνία") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { if(t.isNotBlank()) { actionsList.add(CarAction(carIndex, t, d)); navController.popBackStack() } }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Αποθήκευση", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- COMPONENTS ---
@Composable
fun MenuBtn(t: String, i: ImageVector, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(70.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(i, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Text(t, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}