package com.example.studyusia.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.studyusia.R
import com.example.studyusia.database.DatabaseHelper
import com.example.studyusia.models.Booking
import com.example.studyusia.models.Feedback
import com.example.studyusia.models.StudyRoom
import com.example.studyusia.ui.theme.StudyUsiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRoomScreen(navController: NavController, roomId: String, date: String = "", startTime: String = "", endTime: String = "") {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val sharedPref = context.getSharedPreferences("StudyUSIA_Pref", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", "") ?: ""
    
    var room by remember { mutableStateOf<StudyRoom?>(null) }
    var feedbacks by remember { mutableStateOf(listOf<Feedback>()) }

    LaunchedEffect(roomId) {
        room = dbHelper.getRoomById(roomId)
        feedbacks = dbHelper.getFeedbacksForRoom(roomId)
    }

    if (room == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Black)
        }
        return
    }

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
                Text(
                    text = "Room Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Room Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.height(220.dp)) {
                                if (room?.imagePath != null) {
                                    AsyncImage(
                                        model = room?.imagePath,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        error = painterResource(id = R.drawable.room_1)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.room_1),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Surface(
                                    modifier = Modifier.padding(16.dp).align(Alignment.TopEnd),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = room?.roomId?.let { if (it.startsWith("RM")) "R" + it.drop(3) else it } ?: "",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = room?.roomName ?: "",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = room?.location ?: "",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                                Divider(color = Color(0xFFF1F3F4))
                                Spacer(modifier = Modifier.height(20.dp))

                                DetailRow(label = "Capacity", value = "${room?.capacity} People")
                                DetailRow(label = "Facilities", value = room?.facilities?.lines()?.joinToString(", ") { it.trim().removePrefix("•").trim() } ?: "None")
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("About this room", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    text = room?.details ?: "No extra notes available.",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Reviews Section
                item {
                    Text(
                        text = "Recent Reviews",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (feedbacks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
                        ) {
                            Text(
                                "No reviews yet. Be the first to book and share your thoughts!",
                                modifier = Modifier.padding(24.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(feedbacks) { feedback ->
                        ReviewCard(feedback)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Bottom Actions
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Button(
                        onClick = {
                            if (date.isEmpty() || startTime.isEmpty()) {
                                navController.navigate("new_booking")
                            } else {
                                val bookingId = dbHelper.generateId("BK")
                                val newBooking = Booking(
                                    bookingId = bookingId,
                                    userId = userId,
                                    roomId = roomId,
                                    bookingDate = date,
                                    startTime = startTime,
                                    endTime = endTime,
                                    status = "confirmed"
                                )
                                if (dbHelper.createBooking(newBooking)) {
                                    Toast.makeText(context, "Room Booked Successfully!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("bookings") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                } else {
                                    Toast.makeText(context, "Booking Failed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Book Now", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        Text(text = value, color = Color.DarkGray, fontSize = 14.sp)
    }
}

@Composable
fun ReviewCard(feedback: Feedback) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = feedback.userName ?: "User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < feedback.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < feedback.rating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Text(
                text = "\"${feedback.comment}\"",
                fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewRoomScreenPreview() {
    StudyUsiaTheme {
        ViewRoomScreen(navController = rememberNavController(), roomId = "RM001")
    }
}
