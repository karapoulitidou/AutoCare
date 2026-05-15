package com.example.autocare

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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