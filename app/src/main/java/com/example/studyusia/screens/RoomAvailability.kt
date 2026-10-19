package com.example.studyusia.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.studyusia.R
import com.example.studyusia.database.DatabaseHelper
import com.example.studyusia.models.StudyRoom
import com.example.studyusia.ui.theme.StudyUsiaTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAvailabilityScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    
    var selectedDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    
    var rooms by remember { mutableStateOf(listOf<StudyRoom>()) }
    var availabilityResults by remember { mutableStateOf(mapOf<String, Boolean>()) }

    LaunchedEffect(Unit) {
        rooms = dbHelper.getAllRooms()
    }

    val calendar = Calendar.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Availability",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Modern Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterBox(
                        label = "Date",
                        value = selectedDate,
                        icon = Icons.Default.DateRange,
                        modifier = Modifier.weight(1.2f),
                        onClick = {
                            DatePickerDialog(context, { _, y, m, d ->
                                selectedDate = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}"
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                        }
                    )
                    FilterBox(
                        label = "Start",
                        value = startTime,
                        icon = Icons.Default.Info,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            TimePickerDialog(context, { _, h, m ->
                                startTime = String.format("%02d:%02d", h, m)
                            }, 14, 0, true).show()
                        }
                    )
                    FilterBox(
                        label = "End",
                        value = endTime,
                        icon = Icons.Default.Info,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            TimePickerDialog(context, { _, h, m ->
                                endTime = String.format("%02d:%02d", h, m)
                            }, 16, 0, true).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                            val results = mutableMapOf<String, Boolean>()
                            rooms.forEach { room ->
                                results[room.roomId] = dbHelper.isRoomAvailable(room.roomId, selectedDate, startTime, endTime)
                            }
                            availabilityResults = results
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Check Availability", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (availabilityResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Set date and time to see availability", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(rooms) { room ->
                    val isAvailable = availabilityResults[room.roomId]
                    EnhancedAvailabilityCard(
                        room = room,
                        isAvailable = isAvailable,
                        onBookClick = {
                            navController.navigate("view_room/${room.roomId}?date=$selectedDate&startTime=$startTime&endTime=$endTime")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp).clickable { onClick() },
            color = Color(0xFFF1F3F4),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                Text(
                    text = value.ifEmpty { "--" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EnhancedAvailabilityCard(room: StudyRoom, isAvailable: Boolean?, onBookClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            if (room.imagePath != null && room.imagePath.startsWith("content://")) {
                AsyncImage(
                    model = room.imagePath,
                    contentDescription = null,
                    modifier = Modifier.width(100.dp).fillMaxHeight(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.room_1)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.room_1),
                    contentDescription = null,
                    modifier = Modifier.width(100.dp).fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text(text = room.roomName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Hours: ${room.availableStartTime} - ${room.availableEndTime}", fontSize = 12.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (isAvailable == true) Color(0xFF28A745) else Color.Red
                    val statusText = if (isAvailable == true) "Available" else "Not Available"
                    
                    Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = statusText, color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.fillMaxHeight().padding(16.dp), contentAlignment = Alignment.BottomEnd) {
                Button(
                    onClick = onBookClick,
                    enabled = isAvailable == true,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
