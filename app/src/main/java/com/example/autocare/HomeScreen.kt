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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun getNextReminder(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val todayText = sdf.format(Date())
    val today = sdf.parse(todayText)

    val futureActions = actionsList.mapNotNull { action ->
        try {
            val actionDate = sdf.parse(action.date)

            if (
                actionDate != null &&
                today != null &&
                !actionDate.before(today) &&
                action.owner == currentUsername
            ) {
                action to actionDate
            } else {
                null
            }

        } catch (e: Exception) {
            null
        }
    }.sortedBy { it.second }

    if (futureActions.isEmpty()) {
        return "Καμία εκκρεμότητα"
    }

    val nearestDate = futureActions.first().second

    val sameDayActions = futureActions.filter {
        sdf.format(it.second) == sdf.format(nearestDate)
    }

    return sameDayActions.joinToString("\n") {
        val car = carsList.find { car ->
            car.owner == currentUsername && car.plate == it.first.carId
        }

        "${it.first.type} για το ${car?.brand ?: "όχημα"} στις ${it.first.date}"
    }
}