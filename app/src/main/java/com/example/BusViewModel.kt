package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BusLocation
import com.example.data.FirebaseManager
import com.example.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusViewModel(application: Application) : AndroidViewModel(application) {

    private val locationHelper = LocationHelper(application)

    private val _isDriverSharing = MutableStateFlow(false)
    val isDriverSharing: StateFlow<Boolean> = _isDriverSharing.asStateFlow()

    val parentBusLocation = FirebaseManager.busLocation
    val isFirebaseReady = FirebaseManager.isFirebaseReady

    // Driver controls
    fun toggleDriverSharing() {
        if (_isDriverSharing.value) {
            stopDriverSharing()
        } else {
            startDriverSharing()
        }
    }

    private fun startDriverSharing() {
        _isDriverSharing.value = true
        locationHelper.startLocationUpdates { location ->
            val busLocation = BusLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = System.currentTimeMillis(),
                speed = location.speed,
                isMoving = location.speed > 1.0f
            )
            FirebaseManager.updateLocation(busLocation)
        }
    }

    private fun stopDriverSharing() {
        _isDriverSharing.value = false
        locationHelper.stopLocationUpdates()
        // Update Firestore to show not moving
        val lastLocation = parentBusLocation.value ?: BusLocation()
        FirebaseManager.updateLocation(lastLocation.copy(isMoving = false, speed = 0f))
    }

    // Parent controls
    fun startParentListening() {
        FirebaseManager.startListening()
    }

    fun stopParentListening() {
        FirebaseManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        locationHelper.stopLocationUpdates()
        FirebaseManager.stopListening()
    }
}
