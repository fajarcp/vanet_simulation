package com.fcpdeveloperstudio.vanetsimulation

import java.util.UUID

data class EmergencyMeshPacket(
    val messageId: String = UUID.randomUUID().toString(),
    val ambulanceLat: Double,
    val ambulanceLon: Double,
    val hopCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)