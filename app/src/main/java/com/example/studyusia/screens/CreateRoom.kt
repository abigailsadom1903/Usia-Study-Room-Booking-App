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
import androidx.compose.material.icons.filled.Add
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
fun CreateRoomScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val scrollState = rememberScrollState()

    var roomName by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("4") }
    var facility by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adminKey by remember { mutableStateOf("") }

    var availableFromDate by remember { mutableStateOf("") }
    var availableUntilDate by remember { mutableStateOf("") }
    var openingTime by remember { mutableStateOf("08:00") }
    var closingTime by remember { mutableStateOf("22:00") }

    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageRes by remember { mutableStateOf(R.drawable.room_1) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Create Study Room",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Basic Information
                FormSection(title = "Basic Information") {
                    SectionLabel(icon = "📝", label = "Room Name")
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Room Name") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🖼️", label = "Room Picture")
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
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = if (selectedImageRes != 0) selectedImageRes else R.drawable.room_1),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Change Photo", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                // Section 2: Details
                FormSection(title = "Room Specifications") {
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
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                        }
                                    },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.4f)
                                ) {
                                    capacities.forEach { cap ->
                                        DropdownMenuItem(
                                            text = { Text(cap) },
                                            onClick = {
                                                capacity = cap
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🛠️", label = "Location")
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Level 1, Area B") },
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🔌", label = "Facilities")
                    OutlinedTextField(
                        value = facility,
                        onValueChange = { facility = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("WiFi, AC, Projector...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel(icon = "🗒️", label = "Details / Notes")
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("Extra information about the room") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }

                // Section 3: Availability
                FormSection(title = "Availability Settings") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "📅", label = "From Date")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable {
                                        val calendar = Calendar.getInstance()
                                        DatePickerDialog(context, { _, year, month, day ->
                                            availableFromDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", day)}"
                                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                                    }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(if (availableFromDate.isEmpty()) "Select Start" else availableFromDate, color = if (availableFromDate.isEmpty()) Color.Gray else Color.Black)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "📅", label = "Until Date")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable {
                                        val calendar = Calendar.getInstance()
                                        DatePickerDialog(context, { _, year, month, day ->
                                            availableUntilDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", day)}"
                                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                                    }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(if (availableUntilDate.isEmpty()) "Select End" else availableUntilDate, color = if (availableUntilDate.isEmpty()) Color.Gray else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕙", label = "Opening Time")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable {
                                        TimePickerDialog(context, { _, hour, min ->
                                            openingTime = String.format("%02d:%02d", hour, min)
                                        }, 8, 0, true).show()
                                    }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(openingTime)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel(icon = "🕙", label = "Closing Time")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable {
                                        TimePickerDialog(context, { _, hour, min ->
                                            closingTime = String.format("%02d:%02d", hour, min)
                                        }, 22, 0, true).show()
                                    }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(closingTime)
                            }
                        }
                    }
                }

                // Section 4: Admin Security
                FormSection(title = "Verification") {
                    SectionLabel(icon = "🔑", label = "Admin Access Key")
                    OutlinedTextField(
                        value = adminKey,
                        onValueChange = { adminKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Key to proceed") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color.LightGray)
                    ) {
                        Text("Back", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (adminKey != "ADMIN123") {
                                Toast.makeText(context, "Invalid Admin Key!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (roomName.isBlank() || location.isBlank() || availableFromDate.isBlank() || availableUntilDate.isBlank()) {
                                Toast.makeText(context, "Please fill in required details", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val roomId = dbHelper.generateId("RM")
                            val newRoom = StudyRoom(
                                roomId = roomId,
                                roomName = roomName,
                                capacity = capacity.toIntOrNull() ?: 0,
                                location = location,
                                facilities = facility,
                                details = details,
                                imagePath = selectedImageUri?.toString() ?: "android.resource://${context.packageName}/${R.drawable.room_1}",
                                availableStartDate = availableFromDate,
                                availableEndDate = availableUntilDate,
                                availableStartTime = openingTime,
                                availableEndTime = closingTime
                            )

                            val success = dbHelper.addRoom(newRoom)
                            if (success) {
                                Toast.makeText(context, "Room Created Successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Failed to create room!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Save Room", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
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
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun SectionLabel(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRoomPreview() {
    StudyUsiaTheme {
        CreateRoomScreen(rememberNavController())
    }
}
