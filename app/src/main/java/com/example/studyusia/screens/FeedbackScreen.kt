package com.example.studyusia.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyusia.database.DatabaseHelper
import com.example.studyusia.models.Feedback
import com.example.studyusia.models.StudyRoom
import com.example.studyusia.ui.theme.StudyUsiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController, roomId: String = "", feedbackId: String = "") {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val sharedPref = context.getSharedPreferences("StudyUSIA_Pref", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", "") ?: ""

    var rooms by remember { mutableStateOf(listOf<StudyRoom>()) }
    var selectedRoom by remember { mutableStateOf<StudyRoom?>(null) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    
    var roomExpanded by remember { mutableStateOf(false) }
    val isEditMode = feedbackId.isNotEmpty()

    LaunchedEffect(Unit) {
        rooms = dbHelper.getAllRooms()
        if (roomId.isNotEmpty()) {
            selectedRoom = rooms.find { it.roomId == roomId }
        }
        if (isEditMode) {
            dbHelper.getFeedbackById(feedbackId)?.let {
                rating = it.rating
                comment = it.comment ?: ""
                if (selectedRoom == null) selectedRoom = rooms.find { r -> r.roomId == it.roomId }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Share your Feedback",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Thank you for using StudyUsia!\nLeave us some feedback regarding of your room below for future improvement.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Select Study Room Label
            Text(
                text = "Select Study Room",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Dropdown Box
            Box(modifier = Modifier.width(240.dp)) {
                OutlinedTextField(
                    value = selectedRoom?.roomName ?: "",
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray,
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Black
                    ),
                    enabled = false
                )
                // Click overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = !isEditMode && roomId.isEmpty()) { roomExpanded = true }
                )
                DropdownMenu(
                    expanded = roomExpanded,
                    onDismissRequest = { roomExpanded = false },
                    modifier = Modifier.width(240.dp)
                ) {
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = { Text(room.roomName) },
                            onClick = {
                                selectedRoom = room
                                roomExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Star Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    IconButton(
                        onClick = { rating = starIndex },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "$starIndex Stars",
                            tint = if (starIndex <= rating) Color(0xFFFFC107) else Color.Black,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Comment Box
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Comment", color = Color.Gray) },
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Back", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        if (selectedRoom == null) {
                            Toast.makeText(context, "Please select a room", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (rating == 0) {
                            Toast.makeText(context, "Please give a rating", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        val feedback = Feedback(
                            feedbackId = if (isEditMode) feedbackId else dbHelper.generateId("FB"),
                            userId = userId,
                            roomId = selectedRoom!!.roomId,
                            rating = rating,
                            comment = comment
                        )
                        
                        val success = if (isEditMode) dbHelper.updateFeedback(feedback) else dbHelper.addFeedback(feedback)
                        
                        if (success) {
                            Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Error saving feedback", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(if (isEditMode) "Update" else "Create", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackScreenPreview() {
    StudyUsiaTheme {
        FeedbackScreen(rememberNavController())
    }
}
