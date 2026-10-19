package com.example.studyusia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyusia.screens.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            MainScreen(navController, 0)
        }
        composable("rooms") {
            MainScreen(navController, 0)
        }
        composable("bookings") {
            MainScreen(navController, 2)
        }
        composable("profile") {
            MainScreen(navController, 1)
        }
        composable("create_room") {
            CreateRoomScreen(navController)
        }
        composable("update_room/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            UpdateRoomScreen(navController, roomId)
        }
        composable("view_room/{roomId}?date={date}&startTime={startTime}&endTime={endTime}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val startTime = backStackEntry.arguments?.getString("startTime") ?: ""
            val endTime = backStackEntry.arguments?.getString("endTime") ?: ""
            ViewRoomScreen(navController, roomId, date, startTime, endTime)
        }
        composable("room_availability") {
            RoomAvailabilityScreen(navController)
        }
        composable("new_booking") {
            NewBookingScreen(navController)
        }
        composable("edit_booking/{bookingId}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            EditBookingScreen(navController, bookingId)
        }
        composable("add_feedback/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            FeedbackScreen(navController, roomId = roomId)
        }
        composable("edit_feedback/{feedbackId}") { backStackEntry ->
            val feedbackId = backStackEntry.arguments?.getString("feedbackId") ?: ""
            FeedbackScreen(navController, feedbackId = feedbackId)
        }
    }
}
