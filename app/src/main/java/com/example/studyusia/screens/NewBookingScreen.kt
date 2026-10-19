package com.example.studyusia.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyusia.database.DatabaseHelper
import com.example.studyusia.models.Booking
import com.example.studyusia.models.StudyRoom
import com.example.studyusia.ui.theme.StudyUsiaTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBookingScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val sharedPref = context.getSharedPreferences("StudyUSIA_Pref", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", "") ?: ""

    var rooms by remember { mutableStateOf(listOf<StudyRoom>()) }
    var selectedRoom by remember { mutableStateOf<StudyRoom?>(null) }
    var bookingDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    
    var roomExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        rooms = dbHelper.getAllRooms()
    }

    val calendar = Calendar.getInstance()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "New Booking",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormSection(title = "Booking Details") {
                    SectionLabel(icon = "🏫", label = "Select Study Room")
                    Box {
                        OutlinedTextField(
                            value = selectedRoom?.roomName ?: "",
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Pick a room") },
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { roomExpanded = true })
                        DropdownMenu(expanded = roomExpanded, onDismissRequest = { roomExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f)) {
                            rooms.forEach { room ->
                                DropdownMenuItem(text = { Text(room.roomName) }, onClick = { selectedRoom = room; roomExpanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "📅", label = "Booking Date")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .clickable {
                                DatePickerDialog(context, { _, y, m, d -> bookingDate = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}" }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (bookingDate.isEmpty()) "YYYY-MM-DD" else bookingDate, color = if (bookingDate.isEmpty()) Color.Gray else Color.Black)
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕘", label = "Start Time")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                TimePickerDialog(context, { _, h, m -> startTime = String.format("%02d:%02d", h, m) }, 14, 0, true).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) { Text(if (startTime.isEmpty()) "00:00" else startTime) }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕘", label = "End Time")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                TimePickerDialog(context, { _, h, m -> endTime = String.format("%02d:%02d", h, m) }, 16, 0, true).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) { Text(if (endTime.isEmpty()) "00:00" else endTime) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (selectedRoom == null || bookingDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                            Toast.makeText(context, "Fill in all details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!dbHelper.isRoomAvailable(selectedRoom!!.roomId, bookingDate, startTime, endTime)) {
                            Toast.makeText(context, "Room occupied or closed!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val success = dbHelper.createBooking(Booking(dbHelper.generateId("BK"), userId, selectedRoom!!.roomId, bookingDate, startTime, endTime))
                        if (success) { Toast.makeText(context, "Reserved!", Toast.LENGTH_SHORT).show(); navController.navigate("bookings") }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Confirm Reservation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
