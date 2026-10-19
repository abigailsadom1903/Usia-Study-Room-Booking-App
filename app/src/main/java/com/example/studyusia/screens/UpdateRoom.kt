package com.example.studyusia.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun UpdateRoomScreen(navController: NavController, roomId: String) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val scrollState = rememberScrollState()

    var roomName by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("1") }
    var facility by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adminKey by remember { mutableStateOf("") }
    var currentImagePath by remember { mutableStateOf("") }

    var availableFromDate by remember { mutableStateOf("") }
    var availableUntilDate by remember { mutableStateOf("") }
    var openingTime by remember { mutableStateOf("08:00") }
    var closingTime by remember { mutableStateOf("22:00") }

    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageRes by remember { mutableStateOf(R.drawable.room_1) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roomId) {
        val room = dbHelper.getRoomById(roomId)
        room?.let {
            roomName = it.roomName
            capacity = it.capacity.toString()
            facility = it.facilities ?: ""
            details = it.details ?: ""
            location = it.location
            availableFromDate = it.availableStartDate
            availableUntilDate = it.availableEndDate
            openingTime = it.availableStartTime
            closingTime = it.availableEndTime
            currentImagePath = it.imagePath ?: ""
            if (it.imagePath?.startsWith("content://") == true) {
                selectedImageUri = Uri.parse(it.imagePath)
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            selectedImageRes = 0
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val capacities = listOf("1", "2", "4", "6", "8", "10", "20", "30")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update Room",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormSection(title = "Room Information") {
                    SectionLabel(icon = "📝", label = "Room Name")
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🖼️", label = "Room Image")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE9ECEF))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .clickable { showImageDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else if (currentImagePath.startsWith("content://")) {
                            AsyncImage(model = currentImagePath, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Image(painter = painterResource(id = R.drawable.room_1), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                }

                FormSection(title = "Specifications & Location") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "👥", label = "Capacity")
                            Box {
                                OutlinedTextField(
                                    value = capacity,
                                    onValueChange = { },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.4f)) {
                                    capacities.forEach { cap ->
                                        DropdownMenuItem(text = { Text(cap) }, onClick = { capacity = cap; expanded = false })
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "📍", label = "Location")
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🔌", label = "Facilities")
                    OutlinedTextField(
                        value = facility,
                        onValueChange = { facility = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🗒️", label = "Notes")
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                    )
                }

                FormSection(title = "Availability") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "📅", label = "From Date")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d -> availableFromDate = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}" }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) {
                                Text(if (availableFromDate.isEmpty()) "Select Date" else availableFromDate, color = if (availableFromDate.isEmpty()) Color.Gray else Color.Black)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "📅", label = "Until Date")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d -> availableUntilDate = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}" }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) {
                                Text(if (availableUntilDate.isEmpty()) "Select Date" else availableUntilDate, color = if (availableUntilDate.isEmpty()) Color.Gray else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕙", label = "Opening Time")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                val current = openingTime.split(":")
                                TimePickerDialog(context, { _, h, m -> openingTime = String.format("%02d:%02d", h, m) }, current.getOrNull(0)?.toInt() ?: 8, current.getOrNull(1)?.toInt() ?: 0, true).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) { Text(openingTime) }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕙", label = "Closing Time")
                            Box(modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable {
                                val current = closingTime.split(":")
                                TimePickerDialog(context, { _, h, m -> closingTime = String.format("%02d:%02d", h, m) }, current.getOrNull(0)?.toInt() ?: 22, current.getOrNull(1)?.toInt() ?: 0, true).show()
                            }.padding(12.dp), contentAlignment = Alignment.CenterStart) { Text(closingTime) }
                        }
                    }
                }

                FormSection(title = "Verify Admin") {
                    SectionLabel(icon = "🔑", label = "Admin Key")
                    OutlinedTextField(
                        value = adminKey,
                        onValueChange = { adminKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Key") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.5.dp, Color.LightGray)) {
                        Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = {
                        if (adminKey != "ADMIN123") { Toast.makeText(context, "Invalid Key!", Toast.LENGTH_SHORT).show(); return@Button }
                        val updatedRoom = StudyRoom(roomId, roomName, capacity.toIntOrNull() ?: 0, location, facility, details, selectedImageUri?.toString() ?: currentImagePath, availableFromDate, availableUntilDate, openingTime, closingTime)
                        if (dbHelper.updateRoom(updatedRoom)) { Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show(); navController.popBackStack() }
                    }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete Room?") }, text = { Text("This action cannot be undone.") },
            confirmButton = { TextButton(onClick = { if (adminKey == "ADMIN123") { dbHelper.deleteRoom(roomId); navController.popBackStack() } else { Toast.makeText(context, "Admin Key Required!", Toast.LENGTH_SHORT).show() } }) { Text("Delete", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Select Room Picture") },
            text = { Text("Choose a photo source for the room image.") },
            confirmButton = {
                TextButton(onClick = {
                    imagePickerLauncher.launch("image/*")
                    showImageDialog = false
                }) {
                    Text("Gallery")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedImageRes = R.drawable.room_1
                    selectedImageUri = null
                    showImageDialog = false
                }) {
                    Text("Default")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UpdateRoomScreenPreview() {
    StudyUsiaTheme {
        UpdateRoomScreen(navController = rememberNavController(), roomId = "RM001")
    }
}
