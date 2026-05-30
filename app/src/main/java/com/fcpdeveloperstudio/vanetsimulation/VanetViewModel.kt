package com.fcpdeveloperstudio.vanetsimulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VanetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState(speedKmh = 72)) 
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private val myLat = 25.4052
    private val myLon = 55.4390

    init {
        startSimulation()
    }

    private fun startSimulation() {
        viewModelScope.launch {
            var ambulanceLat = 25.4500
            val ambulanceLon = 55.4390
            var hops = 5
            var currentSpeed = 72 

            while (true) {
           
                val speedChange = listOf(-2, -1, 0, 1, 2).random()
                currentSpeed += speedChange
    
                if (currentSpeed < 65) currentSpeed = 65
                if (currentSpeed > 78) currentSpeed = 78

                val incomingPacket = EmergencyMeshPacket(
                    ambulanceLat = ambulanceLat,
                    ambulanceLon = ambulanceLon,
                    hopCount = hops
                )

                val distance = GeoUtils.calculateDistanceKm(
                    lat1 = myLat,
                    lon1 = myLon,
                    lat2 = incomingPacket.ambulanceLat,
                    lon2 = incomingPacket.ambulanceLon
                )

          
                _uiState.value = _uiState.value.copy(
                    speedKmh = currentSpeed,
                    distanceToEmergency = distance,
                    networkHops = hops,
                    isEmergencyActive = distance <= 2.0
                )

                ambulanceLat -= 0.002
                if (hops > 1 && distance < 4.0) hops = 3

                delay(1000)
            }
        }
    }
}