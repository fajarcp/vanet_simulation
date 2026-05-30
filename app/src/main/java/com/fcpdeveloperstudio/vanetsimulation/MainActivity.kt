package com.fcpdeveloperstudio.vanetsimulation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

data class DashboardState(
    val speedKmh: Int = 0,
    val isEmergencyActive: Boolean = false,
    val distanceToEmergency: Double = 0.0,
    val networkHops: Int = 0
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initialize the ViewModel and collect its state reactively
            val viewModel: VanetViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            MaterialTheme {
                Surface(color = Color(0xFF0A0A0A)) {
                    CarDashboardSimulation(state = uiState)
                }
            }
        }
    }
}


@Composable
fun CarDashboardSimulation(state: DashboardState) {
    Box(modifier = Modifier.fillMaxSize()) {

        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
           
            TelemetryPanel(
                speed = state.speedKmh,
                modifier = Modifier.weight(1f)
            )

            
            NavigationMapPanel(
                distance = state.distanceToEmergency,
                modifier = Modifier.weight(1.5f)
            )
        }

        if (state.isEmergencyActive) {
            EmergencyAlertOverlay(
                distance = state.distanceToEmergency,
                hops = state.networkHops
            )
        }
    }
}


@Composable
fun TelemetryPanel(speed: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(text = "V2V ACTIVE", color = Color(0xFF2E7D32))
                StatusBadge(text = "BAT: 84%", color = Color(0xFF1976D2))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = speed.toString(),
                    fontSize = 80.sp, 
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-2).sp
                )
                Text(text = "km/h", fontSize = 24.sp, color = Color.Gray)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GearText("P", active = false)
                GearText("R", active = false)
                GearText("N", active = false)
                GearText("D", active = true)
            }
        }
    }
}

@Composable
fun NavigationMapPanel(distance: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C2C2C))
        ) {
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2

                
                drawLine(
                    color = Color(0xFF111111),
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, canvasHeight),
                    strokeWidth = 300f
                )

                
                val dashPath = PathEffect.dashPathEffect(floatArrayOf(60f, 60f), 0f)
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, canvasHeight),
                    strokeWidth = 10f,
                    pathEffect = dashPath
                )

                
                val hostCarY = canvasHeight * 0.2f
                drawCircle(
                    color = Color(0xFF2196F3), // Bright Blue
                    radius = 35f,
                    center = Offset(centerX, hostCarY)
                )

                val maxVisibleDistance = 4.0
                if (distance < maxVisibleDistance) {
                    val relativePosition = (distance / maxVisibleDistance).toFloat()
                    val availableTrackLength = canvasHeight - hostCarY

                    val ambulanceY = hostCarY + (availableTrackLength * relativePosition)

                    drawCircle(
                        color = Color.Red,
                        radius = 40f,
                        center = Offset(centerX, ambulanceY)
                    )
                }
            }

   
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Navigation", fontSize = 24.sp, color = Color.Gray)
                Text(
                    text = "Highway 311 - Ajman",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EmergencyAlertOverlay(distance: Double, hops: Int) {
 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(12.dp, Color(0xEEB71C1C))
            .background(Color(0x11FF0000))
    ) {
     
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xDDB71C1C)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Yellow,
                    modifier = Modifier.size(64.dp)
                )
                Column {
                    Text(
                        text = "EMERGENCY VEHICLE APPROACHING",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "$distance km Behind • $hops Network Hops",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
            }
        }
    }
}


@Composable
fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GearText(text: String, active: Boolean) {
    Text(
        text = text,
        fontSize = 40.sp,
        fontWeight = if (active) FontWeight.Black else FontWeight.Normal,
        color = if (active) Color.White else Color.DarkGray
    )
}