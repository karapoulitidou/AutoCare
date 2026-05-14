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
import androidx.compose.ui.graphics.toArgb
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
import java.text.SimpleDateFormat
import java.util.*

// --- 1. Models & Global State ---
data class Car(val brand: String, val model: String, val plate: String, val isBike: Boolean = false)
data class CarAction(val carIndex: Int, val type: String, val date: String, val isDocument: Boolean = false)

val carsList = mutableStateListOf(
    Car("Toyota", "Yaris", "NIK-1234"),
    Car("Audi", "A3", "KZN-7865"),
    Car("Yamaha", "TDM 900", "BTR-456", isBike = true)
)

val actionsList = mutableStateListOf(
    CarAction(0, "Service Λαδιών", "15/06/2026", false),
    CarAction(0, "Ασφάλεια Anytime", "12/05/2027", true),
    CarAction(1, "ΚΤΕΟ", "20/05/2026", false),
    CarAction(1, "Ασφάλεια Hellas Direct", "14/09/2026", true),
    CarAction(2, "Ασφάλεια Interamerican", "30/10/2026", true),
    CarAction(2, "Έλεγχος Αλυσίδας", "12/08/2026", false)
)

val mainGradient = Brush.verticalGradient(listOf(Color(0xFFD1E3FF), Color.White))

// --- 2. Main Activity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color(0xFFD1E3FF).toArgb()
        enableEdgeToEdge()
        setContent {
            AutoCareTheme {
                MainNavigation()
            }
        }
    }
}

// --- 3. Navigation ---
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
        composable("pdfViewer/{actionIndex}") { backStackEntry ->
            val actionIndex = backStackEntry.arguments?.getString("actionIndex")?.toIntOrNull() ?: 0
            PdfViewerScreen(navController, actionIndex)
        }
        composable("addAction/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AddActionScreen(navController, index)
        }
    }
}

// --- 4. Helper Function ---
fun getNextReminder(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val now = Date()
    val futureActions = actionsList.mapNotNull { action ->
        try {
            val date = sdf.parse(action.date)
            if (date != null && date.after(now)) action to date else null
        } catch (e: Exception) { null }
    }.sortedBy { it.second }

    val next = futureActions.firstOrNull() ?: return "Καμία εκκρεμότητα"
    val car = carsList.getOrNull(next.first.carIndex)
    return "${next.first.type} για το ${car?.brand} στις ${next.first.date}"
}

// --- 5. Screens ---

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
            OutlinedTextField(u, { u = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(p, { p = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { if (u == "nikos" && p == "1234") navController.navigate("home") else Toast.makeText(context, "Λάθος στοιχεία!", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("ΣΥΝΔΕΣΗ", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun DynamicHomeScreen(navController: NavController) {
    val reminderText = remember { getNextReminder() }
    val recentActions = remember { actionsList.takeLast(3).reversed() }

    Scaffold(containerColor = Color.Transparent, modifier = Modifier.background(mainGradient)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text("Καλώς ήρθες, Νίκο!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(24.dp))

            // Κάρτα Υπενθύμισης
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Επόμενη Υποχρέωση", color = Color.White.copy(alpha = 0.7f))
                        Text(reminderText, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.NotificationsActive, null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- Πρόσφατη Δραστηριότητα ---
            Text("Πρόσφατη Δραστηριότητα", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(Modifier.height(12.dp))

            recentActions.forEach { action ->
                val car = carsList.getOrNull(action.carIndex)
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(if (action.isDocument) Icons.Default.Description else Icons.Default.History, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("${action.type} στο ${car?.brand}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Στις ${action.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            MenuBtn("Τα οχήματά μου", Icons.Default.Garage) { navController.navigate("cars") }
            Spacer(Modifier.height(12.dp))
            MenuBtn("Προσθήκη Οχήματος", Icons.Default.AddCircle) { navController.navigate("addCar") }
            Spacer(Modifier.height(24.dp))
        }
    }
}

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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (car.isBike) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary)
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
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Ιστορικό", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate("addAction/$index") }) {
                        Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    itemsIndexed(carActions) { _, a ->
                        val originalIndex = actionsList.indexOf(a)
                        Card(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { if(a.isDocument) navController.navigate("pdfViewer/$originalIndex") },
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                headlineContent = { Text(a.type, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(a.date) },
                                leadingContent = { Icon(if (a.isDocument) Icons.Default.PictureAsPdf else Icons.Default.Build, null, tint = if (a.isDocument) Color(0xFFD32F2F) else Color.Gray) },
                                trailingContent = { if(a.isDocument) Icon(Icons.Default.ChevronRight, null) }
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
    val car = action?.let { carsList.getOrNull(it.carIndex) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Προβολή Αρχείου", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(24.dp)) {
                    Text("ΨΗΦΙΑΚΟ ΑΡΧΕΙΟ ΟΧΗΜΑΤΟΣ", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Gray)
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                    Text(action?.type?.uppercase() ?: "ΕΓΓΡΑΦΟ", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1565C0))
                    Spacer(Modifier.height(32.dp))
                    PdfRow("Ημερ/νία:", action?.date ?: "-")
                    PdfRow("Όχημα:", "${car?.brand} ${car?.model}")
                    PdfRow("Πινακίδα:", car?.plate ?: "-")
                    PdfRow("Status:", "ΕΠΙΚΥΡΩΜΕΝΟ")
                    Spacer(Modifier.height(40.dp))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.QrCode2, null, modifier = Modifier.size(100.dp), tint = Color.Black)
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
    var b by remember { mutableStateOf("") }
    var m by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = { CenterAlignedTopAppBar(title = { Text("Νέο Όχημα", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent), navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(b, { b = it }, label = { Text("Μάρκα") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(m, { m = it }, label = { Text("Μοντέλο") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(p, { p = it }, label = { Text("Πινακίδα") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { if(b.isNotBlank()) { carsList.add(Car(b, m, p)); navController.popBackStack() } }, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("ΑΠΟΘΗΚΕΥΣΗ", fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(navController: NavController, carIndex: Int) {
    var t by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    var isDoc by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(mainGradient),
        topBar = { CenterAlignedTopAppBar(title = { Text("Προσθήκη στο Ιστορικό", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent), navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(t, { t = it }, label = { Text("Τίτλος (π.χ. Service)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(d, { d = it }, label = { Text("Ημερομηνία (ηη/μμ/εεεε)") }, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Είναι αρχείο / PDF;", Modifier.weight(1f))
                Switch(checked = isDoc, onCheckedChange = { isDoc = it })
            }
            Button(onClick = { if(t.isNotBlank()) { actionsList.add(CarAction(carIndex, t, d, isDoc)); navController.popBackStack() } }, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("ΑΠΟΘΗΚΕΥΣΗ", fontWeight = FontWeight.Bold) }
        }
    }
}

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