package com.example.listycity
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.mutableStateListOf

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities") //pointer to location in database
    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )


    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
            .addOnFailureListener { e -> Log.e("CityRepo", "addCity failed", e) }

    }

    fun updateCity(oldCity: City, updatedCity: City) {
        citiesRef.document(oldCity.name).set(updatedCity)
            .addOnFailureListener { e -> Log.e("CityRepo", "updateCity failed", e) }
    }

    fun deleteCity(city: City){
        citiesRef.document(city.name).delete()
            .addOnFailureListener { e -> Log.e("CityRepo", "deleteCity failed", e) }
    }

    //snapshot is a copy of the data as it exists in real time, SNAPSHOT -- current data, snapshot listeniner listens to the db pointed at for change
    init{ //runs automatically when CityRepository is made
        citiesRef.addSnapshotListener { snapshot, error -> // start listening to cities collection, take both snapshot (current data) and error (if sm wrong)
            if (error != null) {
                return@addSnapshotListener //if error, exit this lambda not the whole class
            }

                _cities.clear() //empty local list

                snapshot?.documents?.forEach { document -> //loop through each document in the selected section
                    val city = document.toObject(City::class.java) //convert the raw firestore doc to city object
                    if (city != null) {
                        _cities.add(city) //if the conversionnworked addd to local list
                    }
                }
            }
        }

    }