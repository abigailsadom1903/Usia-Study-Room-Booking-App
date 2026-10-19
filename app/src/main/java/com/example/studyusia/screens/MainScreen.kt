package com.example.studyusia.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyusia.ui.theme.StudyUsiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, initialTab: Int = 0) {
    var selectedTab by remember { mutableStateOf(initialTab) } // 0=Rooms, 1=Profile, 2=Bookings

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📚", fontSize = 24.sp)
                            Text("Rooms", fontSize = 11.sp)
                        }
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👤", fontSize = 24.sp)
                            Text("My Profile", fontSize = 11.sp)
                        }
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", fontSize = 24.sp)
                            Text("Bookings", fontSize = 11.sp)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> StudyRoomListScreen(navController, paddingValues)
            1 -> MyProfileScreen(navController, paddingValues)
            2 -> BookingsScreen(navController, paddingValues)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    StudyUsiaTheme {
        MainScreen(navController = rememberNavController())
    }
}