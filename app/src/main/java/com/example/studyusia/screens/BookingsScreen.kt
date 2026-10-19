package com.example.studyusia.screens

import android.content.Context
import androidx.compose.foundation.background
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(navController: NavController, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val sharedPref = context.getSharedPreferences("StudyUSIA_Pref", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", "") ?: ""

    var bookings by remember { mutableStateOf(listOf<Booking>()) }
    var rooms by remember { mutableStateOf(mapOf<String, StudyRoom>()) }
    var userFeedbacks by remember { mutableStateOf(mapOf<String, com.example.studyusia.models.Feedback>()) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            bookings = dbHelper.getUserBookings(userId)
            val roomsMap = mutableMapOf<String, StudyRoom>()
            val feedbacksMap = mutableMapOf<String, com.example.studyusia.models.Feedback>()
            
            bookings.forEach { booking ->
                dbHelper.getRoomById(booking.roomId)?.let { room ->
                    roomsMap[booking.roomId] = room
                }
                val roomFeedbacks = dbHelper.getFeedbacksForRoom(booking.roomId)
                val userFeedback = roomFeedbacks.find { it.userId == userId }
                if (userFeedback != null) {
                    feedbacksMap[booking.roomId] = userFeedback
                }
            }
            rooms = roomsMap
            userFeedbacks = feedbacksMap
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF8F9FA))
    ) {
        // Header Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Bookings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                IconButton(
                    onClick = { navController.navigate("new_booking") },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Booking",
                        tint = Color.White
                    )
                }
            }
        }

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📅", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No bookings yet.", color = Color.Gray, fontSize = 16.sp)
                    TextButton(onClick = { navController.navigate("room_availability") }) {
                        Text("Find a room to book", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(bookings) { booking ->
                    val currentFeedback = userFeedbacks[booking.roomId]
                    EnhancedBookingCard(
                        booking = booking,
                        room = rooms[booking.roomId],
                        feedback = currentFeedback,
                        onEditClick = { 
                            navController.navigate("edit_booking/${booking.bookingId}")
                        },
                        onDeleteClick = { 
                            if (dbHelper.cancelBooking(booking.bookingId)) {
                                bookings = bookings.filter { it.bookingId != booking.bookingId }
                            }
                        },
                        onFeedbackClick = { 
                            navController.navigate("add_feedback/${booking.roomId}")
                        },
                        onEditFeedbackClick = { 
                            currentFeedback?.let {
                                navController.navigate("edit_feedback/${it.feedbackId}")
                            }
                        },
                        onDeleteFeedbackClick = { 
                            currentFeedback?.let { fb ->
                                if (dbHelper.deleteFeedback(fb.feedbackId)) {
                                    val newFeedbacks = userFeedbacks.toMutableMap()
                                    newFeedbacks.remove(booking.roomId)
                                    userFeedbacks = newFeedbacks
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedBookingCard(
    booking: Booking,
    room: StudyRoom?,
    feedback: com.example.studyusia.models.Feedback?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onEditFeedbackClick: () -> Unit,
    onDeleteFeedbackClick: () -> Unit
) {
    val currentTime = Calendar.getInstance().time
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val bookingEndTimeStr = "${booking.bookingDate} ${booking.endTime}"
    val bookingEndTime = try { sdf.parse(bookingEndTimeStr) } catch (e: Exception) { null }
    val isSessionEnded = bookingEndTime?.before(currentTime) ?: false

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room?.roomName ?: "Loading Room...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = room?.location ?: "Location pending",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text("Date", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF28A745), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = booking.bookingDate, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Column {
                    Text("Time Slot", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF28A745), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${booking.startTime} - ${booking.endTime}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSessionEnded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Session Completed", color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (feedback == null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onFeedbackClick,
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Share Feedback", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row {
                                            repeat(5) { index ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (index < feedback.rating) Color(0xFFFFC107) else Color.LightGray,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Row {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp).clickable { onEditFeedbackClick() }, tint = Color.Gray)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp).clickable { onDeleteFeedbackClick() }, tint = Color.Gray)
                                        }
                                    }
                                    Text(text = feedback.comment ?: "", fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                }
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Upcoming: ${booking.status.uppercase()}", color = Color(0xFFE65100), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingsScreenPreview() {
    StudyUsiaTheme {
        BookingsScreen(navController = rememberNavController(), paddingValues = PaddingValues(0.dp))
    }
}
