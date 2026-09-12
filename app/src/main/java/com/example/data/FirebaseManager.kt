package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    private const val BUS_COLLECTION = "buses"
    private const val DEFAULT_BUS_ID = "bus_1"

    val isFirebaseReady: Boolean
        get() = try {
            FirebaseApp.getInstance()
            true
        } catch (e: Exception) {
            false
        }

    private val _busLocation = MutableStateFlow<BusLocation?>(null)
    val busLocation: StateFlow<BusLocation?> = _busLocation.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    fun updateLocation(location: BusLocation) {
        if (!isFirebaseReady) return
        val db = FirebaseFirestore.getInstance()
        db.collection(BUS_COLLECTION).document(DEFAULT_BUS_ID)
            .set(location)
            .addOnSuccessListener {
                Log.d(TAG, "Location updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating location", e)
            }
    }

    fun startListening() {
        if (!isFirebaseReady) return
        val db = FirebaseFirestore.getInstance()
        listenerRegistration = db.collection(BUS_COLLECTION).document(DEFAULT_BUS_ID)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val location = snapshot.toObject(BusLocation::class.java)
                    _busLocation.value = location
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
