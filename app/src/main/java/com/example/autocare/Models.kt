package com.example.autocare

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

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

val mainGradient = Brush.verticalGradient(
    listOf(Color(0xFFD1E3FF), Color.White)
)