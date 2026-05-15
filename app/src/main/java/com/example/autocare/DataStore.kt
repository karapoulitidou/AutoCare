package com.example.autocare

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveData(context: Context) {

    val prefs =
        context.getSharedPreferences(
            "autocare",
            Context.MODE_PRIVATE
        )

    val gson = Gson()

    prefs.edit()
        .putString("cars", gson.toJson(carsList))
        .putString("actions", gson.toJson(actionsList))
        .putString("users", gson.toJson(usersList))
        .apply()
}

fun loadData(context: Context) {

    val prefs =
        context.getSharedPreferences(
            "autocare",
            Context.MODE_PRIVATE
        )

    val gson = Gson()

    val carsJson =
        prefs.getString("cars", null)

    val actionsJson =
        prefs.getString("actions", null)

    val usersJson =
        prefs.getString("users", null)

    carsList.clear()
    actionsList.clear()
    usersList.clear()

    val carsType =
        object : TypeToken<List<Car>>() {}.type

    val actionsType =
        object : TypeToken<List<CarAction>>() {}.type

    val usersType =
        object : TypeToken<List<User>>() {}.type

    if (carsJson != null) {

        carsList.addAll(
            gson.fromJson(carsJson, carsType)
        )
    }

    if (actionsJson != null) {

        actionsList.addAll(
            gson.fromJson(actionsJson, actionsType)
        )
    }

    if (usersJson != null) {

        usersList.addAll(
            gson.fromJson(usersJson, usersType)
        )
    }

    saveData(context)
}

fun getUserCars(): List<Car> {

    return carsList.filter {
        it.owner == currentUsername
    }
}

fun deleteCarAt(
    userCarIndex: Int,
    context: Context
) {

    val userCars = getUserCars()

    val car =
        userCars.getOrNull(userCarIndex)
            ?: return

    carsList.remove(car)

    actionsList.removeAll {

        it.owner == currentUsername &&
                it.carId == car.plate
    }

    saveData(context)
}

fun deleteActionAt(
    actionIndex: Int,
    context: Context
) {

    if (actionIndex in actionsList.indices) {

        actionsList.removeAt(actionIndex)

        saveData(context)
    }
}