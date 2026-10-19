package com.example.studyusia.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.studyusia.models.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "studyusia.db"
        private const val DATABASE_VERSION = 9

        // Table names
        const val TABLE_USER = "users"
        const val TABLE_STUDY_ROOM = "study_rooms"
        const val TABLE_BOOKING = "bookings"
        const val TABLE_SCHEDULE = "schedules"
        const val TABLE_FEEDBACK = "feedbacks"

        // User table columns
        const val COL_USER_ID = "user_id"
        const val COL_FULL_NAME = "full_name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_PHONE = "phone"
        const val COL_CREATED_AT = "created_at"

        // StudyRoom table columns
        const val COL_ROOM_ID = "room_id"
        const val COL_ROOM_NAME = "room_name"
        const val COL_CAPACITY = "capacity"
        const val COL_LOCATION = "location"
        const val COL_FACILITIES = "facilities"
        const val COL_DETAILS = "details"
        const val COL_IMAGE_PATH = "image_path"
        const val COL_AVAILABLE_START_DATE = "available_start_date"
        const val COL_AVAILABLE_END_DATE = "available_end_date"
        const val COL_AVAILABLE_START_TIME = "available_start_time"
        const val COL_AVAILABLE_END_TIME = "available_end_time"

        // Booking table columns
        const val COL_BOOKING_ID = "booking_id"
        const val COL_BOOKING_DATE = "booking_date"
        const val COL_START_TIME = "start_time"
        const val COL_END_TIME = "end_time"
        const val COL_STATUS = "status"

        // Schedule table columns
        const val COL_SCHEDULE_ID = "schedule_id"
        const val COL_DATE = "date"
        const val COL_IS_AVAILABLE = "is_available"

        // Feedback table columns
        const val COL_FEEDBACK_ID = "feedback_id"
        const val COL_RATING = "rating"
        const val COL_COMMENT = "comment"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users Table
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_USER_ID TEXT PRIMARY KEY,
                $COL_FULL_NAME TEXT NOT NULL,
                $COL_EMAIL TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_PHONE TEXT,
                $COL_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Create StudyRooms Table
        val createStudyRoomTable = """
            CREATE TABLE $TABLE_STUDY_ROOM (
                $COL_ROOM_ID TEXT PRIMARY KEY,
                $COL_ROOM_NAME TEXT NOT NULL,
                $COL_CAPACITY INTEGER NOT NULL,
                $COL_LOCATION TEXT NOT NULL,
                $COL_FACILITIES TEXT,
                $COL_DETAILS TEXT,
                $COL_IMAGE_PATH TEXT,
                $COL_AVAILABLE_START_DATE TEXT DEFAULT '',
                $COL_AVAILABLE_END_DATE TEXT DEFAULT '',
                $COL_AVAILABLE_START_TIME TEXT DEFAULT '08:00',
                $COL_AVAILABLE_END_TIME TEXT DEFAULT '22:00'
            )
        """.trimIndent()

        // Create Bookings Table
        val createBookingTable = """
            CREATE TABLE $TABLE_BOOKING (
                $COL_BOOKING_ID TEXT PRIMARY KEY,
                $COL_USER_ID TEXT NOT NULL,
                $COL_ROOM_ID TEXT NOT NULL,
                $COL_BOOKING_DATE DATE NOT NULL,
                $COL_START_TIME TIME NOT NULL,
                $COL_END_TIME TIME NOT NULL,
                $COL_STATUS TEXT DEFAULT 'confirmed',
                FOREIGN KEY ($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID),
                FOREIGN KEY ($COL_ROOM_ID) REFERENCES $TABLE_STUDY_ROOM($COL_ROOM_ID)
            )
        """.trimIndent()

        // Create Schedules Table
        val createScheduleTable = """
            CREATE TABLE $TABLE_SCHEDULE (
                $COL_SCHEDULE_ID TEXT PRIMARY KEY,
                $COL_ROOM_ID TEXT NOT NULL,
                $COL_DATE DATE NOT NULL,
                $COL_START_TIME TIME NOT NULL,
                $COL_END_TIME TIME NOT NULL,
                $COL_IS_AVAILABLE INTEGER DEFAULT 1,
                FOREIGN KEY ($COL_ROOM_ID) REFERENCES $TABLE_STUDY_ROOM($COL_ROOM_ID)
            )
        """.trimIndent()

        // Create Feedbacks Table
        val createFeedbackTable = """
            CREATE TABLE $TABLE_FEEDBACK (
                $COL_FEEDBACK_ID TEXT PRIMARY KEY,
                $COL_USER_ID TEXT NOT NULL,
                $COL_ROOM_ID TEXT NOT NULL,
                $COL_RATING INTEGER NOT NULL CHECK ($COL_RATING >= 1 AND $COL_RATING <= 5),
                $COL_COMMENT TEXT,
                $COL_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID),
                FOREIGN KEY ($COL_ROOM_ID) REFERENCES $TABLE_STUDY_ROOM($COL_ROOM_ID)
            )
        """.trimIndent()

        // Execute all create table statements
        db.execSQL(createUserTable)
        db.execSQL(createStudyRoomTable)
        db.execSQL(createBookingTable)
        db.execSQL(createScheduleTable)
        db.execSQL(createFeedbackTable)

        // Add a test user for debugging
        val testValues = ContentValues().apply {
            put(COL_USER_ID, "TEST001")
            put(COL_FULL_NAME, "Test User")
            put(COL_EMAIL, "test@test.com")
            put(COL_PASSWORD, "123456")
            put(COL_PHONE, "0123456789")
        }
        db.insert(TABLE_USER, null, testValues)

        // Add a default study room
        val defaultRoom = ContentValues().apply {
            put(COL_ROOM_ID, "RM001")
            put(COL_ROOM_NAME, "General Study Hall")
            put(COL_CAPACITY, 50)
            put(COL_LOCATION, "Main Library, Level 1")
            put(COL_FACILITIES, "Aircond, High-speed Wi-Fi, Power outlets")
            put(COL_DETAILS, "A large open space for everyone to study together.")
            put(COL_AVAILABLE_START_DATE, "2023-01-01")
            put(COL_AVAILABLE_END_DATE, "2025-12-31")
            put(COL_AVAILABLE_START_TIME, "08:00")
            put(COL_AVAILABLE_END_TIME, "22:00")
        }
        db.insert(TABLE_STUDY_ROOM, null, defaultRoom)

        Log.d("DATABASE", "Database created with test user: test@test.com / 123456")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FEEDBACK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKING")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDY_ROOM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // ==================== USER METHODS ====================

    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_ID, user.userId)
            put(COL_FULL_NAME, user.fullName)
            put(COL_EMAIL, user.email)
            put(COL_PASSWORD, user.password)
            put(COL_PHONE, user.phone)
        }
        val result = db.insert(TABLE_USER, null, values)
        db.close()
        Log.d("DATABASE", "Register user: $result, Email: ${user.email}, Password: ${user.password}")
        return result != -1L
    }

    fun loginUser(email: String, password: String): User? {
        val db = readableDatabase
        Log.d("DATABASE", "Login attempt - Email: '$email', Password: '$password'")

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(email, password)
        )

        Log.d("DATABASE", "Cursor count: ${cursor.count}")

        val user = if (cursor.moveToFirst()) {
            Log.d("DATABASE", "User FOUND in database!")
            User(
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            )
        } else {
            Log.d("DATABASE", "User NOT FOUND in database!")
            null
        }

        cursor.close()
        db.close()
        return user
    }

    fun getUserById(userId: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_USER_ID = ?",
            arrayOf(userId)
        )

        val user = if (cursor.moveToFirst()) {
            User(
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            )
        } else null

        cursor.close()
        db.close()
        return user
    }

    fun updateUserProfile(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FULL_NAME, user.fullName)
            put(COL_PHONE, user.phone)
        }
        val result = db.update(TABLE_USER, values, "$COL_USER_ID = ?", arrayOf(user.userId))
        db.close()
        return result > 0
    }

    fun updateUserPassword(userId: String, newPassword: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PASSWORD, newPassword)
        }
        val result = db.update(TABLE_USER, values, "$COL_USER_ID = ?", arrayOf(userId))
        db.close()
        return result > 0
    }

    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM $TABLE_USER WHERE $COL_EMAIL = ?",
            arrayOf(email)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER", null)

        while (cursor.moveToNext()) {
            users.add(
                User(
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
                )
            )
        }
        cursor.close()
        db.close()
        return users
    }

    // ==================== STUDY ROOM METHODS ====================

    fun getAllRooms(): List<StudyRoom> {
        val rooms = mutableListOf<StudyRoom>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_STUDY_ROOM ORDER BY $COL_ROOM_ID DESC", null)

        while (cursor.moveToNext()) {
            rooms.add(
                StudyRoom(
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                    roomName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_NAME)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CAPACITY)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    facilities = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITIES)),
                    details = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETAILS)),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_PATH)),
                    availableStartDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_START_DATE)),
                    availableEndDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_END_DATE)),
                    availableStartTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_START_TIME)),
                    availableEndTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_END_TIME))
                )
            )
        }
        cursor.close()
        db.close()
        return rooms
    }

    fun getRoomById(roomId: String): StudyRoom? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_STUDY_ROOM WHERE $COL_ROOM_ID = ?",
            arrayOf(roomId)
        )

        val room = if (cursor.moveToFirst()) {
            StudyRoom(
                roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                roomName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_NAME)),
                capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CAPACITY)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                facilities = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITIES)),
                details = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETAILS)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_PATH)),
                availableStartDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_START_DATE)),
                availableEndDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_END_DATE)),
                availableStartTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_START_TIME)),
                availableEndTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABLE_END_TIME))
            )
        } else null

        cursor.close()
        db.close()
        return room
    }

    fun addRoom(room: StudyRoom): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ROOM_ID, room.roomId)
            put(COL_ROOM_NAME, room.roomName)
            put(COL_CAPACITY, room.capacity)
            put(COL_LOCATION, room.location)
            put(COL_FACILITIES, room.facilities)
            put(COL_DETAILS, room.details)
            put(COL_IMAGE_PATH, room.imagePath)
            put(COL_AVAILABLE_START_DATE, room.availableStartDate)
            put(COL_AVAILABLE_END_DATE, room.availableEndDate)
            put(COL_AVAILABLE_START_TIME, room.availableStartTime)
            put(COL_AVAILABLE_END_TIME, room.availableEndTime)
        }
        val result = db.insert(TABLE_STUDY_ROOM, null, values)
        db.close()
        return result != -1L
    }

    fun updateRoom(room: StudyRoom): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ROOM_NAME, room.roomName)
            put(COL_CAPACITY, room.capacity)
            put(COL_LOCATION, room.location)
            put(COL_FACILITIES, room.facilities)
            put(COL_DETAILS, room.details)
            put(COL_IMAGE_PATH, room.imagePath)
            put(COL_AVAILABLE_START_DATE, room.availableStartDate)
            put(COL_AVAILABLE_END_DATE, room.availableEndDate)
            put(COL_AVAILABLE_START_TIME, room.availableStartTime)
            put(COL_AVAILABLE_END_TIME, room.availableEndTime)
        }
        val result = db.update(TABLE_STUDY_ROOM, values, "$COL_ROOM_ID = ?", arrayOf(room.roomId))
        db.close()
        return result > 0
    }

    fun deleteRoom(roomId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_STUDY_ROOM, "$COL_ROOM_ID = ?", arrayOf(roomId))
        db.close()
        return result > 0
    }

    // ==================== BOOKING METHODS ====================

    fun createBooking(booking: Booking): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_BOOKING_ID, booking.bookingId)
            put(COL_USER_ID, booking.userId)
            put(COL_ROOM_ID, booking.roomId)
            put(COL_BOOKING_DATE, booking.bookingDate)
            put(COL_START_TIME, booking.startTime)
            put(COL_END_TIME, booking.endTime)
            put(COL_STATUS, booking.status)
        }
        val result = db.insert(TABLE_BOOKING, null, values)
        db.close()
        return result != -1L
    }

    fun getUserBookings(userId: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BOOKING WHERE $COL_USER_ID = ? ORDER BY $COL_BOOKING_DATE DESC",
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            bookings.add(
                Booking(
                    bookingId = cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_ID)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                    bookingDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DATE)),
                    startTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)),
                    endTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS))
                )
            )
        }
        cursor.close()
        db.close()
        return bookings
    }

    fun getBookingById(bookingId: String): Booking? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BOOKING WHERE $COL_BOOKING_ID = ?",
            arrayOf(bookingId)
        )

        val booking = if (cursor.moveToFirst()) {
            Booking(
                bookingId = cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_ID)),
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                bookingDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DATE)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS))
            )
        } else null

        cursor.close()
        db.close()
        return booking
    }

    fun updateBooking(booking: Booking): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ROOM_ID, booking.roomId)
            put(COL_BOOKING_DATE, booking.bookingDate)
            put(COL_START_TIME, booking.startTime)
            put(COL_END_TIME, booking.endTime)
            put(COL_STATUS, booking.status)
        }
        val result = db.update(TABLE_BOOKING, values, "$COL_BOOKING_ID = ?", arrayOf(booking.bookingId))
        db.close()
        return result > 0
    }

    fun cancelBooking(bookingId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_BOOKING, "$COL_BOOKING_ID = ?", arrayOf(bookingId))
        db.close()
        return result > 0
    }

    fun isRoomAvailable(roomId: String, date: String, startTime: String, endTime: String): Boolean {
        val db = readableDatabase
        
        // Check if within operating hours and matches date range
        val roomCursor = db.rawQuery(
            "SELECT $COL_AVAILABLE_START_DATE, $COL_AVAILABLE_END_DATE, $COL_AVAILABLE_START_TIME, $COL_AVAILABLE_END_TIME FROM $TABLE_STUDY_ROOM WHERE $COL_ROOM_ID = ?",
            arrayOf(roomId)
        )
        if (roomCursor.moveToFirst()) {
            val startDate = roomCursor.getString(0)
            val endDate = roomCursor.getString(1)
            val opStart = roomCursor.getString(2)
            val opEnd = roomCursor.getString(3)
            
            // Check if searched date is within the available range
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                if (date < startDate || date > endDate) {
                    roomCursor.close()
                    return false
                }
            } else if (startDate.isNotEmpty() && date != startDate) {
                // Legacy support for single date if only start is set
                roomCursor.close()
                return false
            }
            
            if (startTime < opStart || endTime > opEnd) {
                roomCursor.close()
                return false
            }
        }
        roomCursor.close()

        val cursor = db.rawQuery(
            """
            SELECT 1 FROM $TABLE_BOOKING 
            WHERE $COL_ROOM_ID = ? 
            AND $COL_BOOKING_DATE = ? 
            AND $COL_STATUS = 'confirmed'
            AND (
                ($COL_START_TIME <= ? AND $COL_END_TIME > ?) OR
                ($COL_START_TIME < ? AND $COL_END_TIME >= ?) OR
                ($COL_START_TIME >= ? AND $COL_END_TIME <= ?)
            )
            """.trimIndent(),
            arrayOf(roomId, date, endTime, startTime, endTime, startTime, startTime, endTime)
        )
        val isAvailable = cursor.count == 0
        cursor.close()
        db.close()
        return isAvailable
    }

    // ==================== FEEDBACK METHODS ====================

    fun addFeedback(feedback: Feedback): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FEEDBACK_ID, feedback.feedbackId)
            put(COL_USER_ID, feedback.userId)
            put(COL_ROOM_ID, feedback.roomId)
            put(COL_RATING, feedback.rating)
            put(COL_COMMENT, feedback.comment)
        }
        val result = db.insert(TABLE_FEEDBACK, null, values)
        db.close()
        return result != -1L
    }

    fun getFeedbacksForRoom(roomId: String): List<Feedback> {
        val feedbacks = mutableListOf<Feedback>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT f.*, u.$COL_FULL_NAME 
            FROM $TABLE_FEEDBACK f
            JOIN $TABLE_USER u ON f.$COL_USER_ID = u.$COL_USER_ID
            WHERE f.$COL_ROOM_ID = ?
            ORDER BY f.$COL_CREATED_AT DESC
            """.trimIndent(),
            arrayOf(roomId)
        )

        while (cursor.moveToNext()) {
            feedbacks.add(
                Feedback(
                    feedbackId = cursor.getString(cursor.getColumnIndexOrThrow(COL_FEEDBACK_ID)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RATING)),
                    comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_COMMENT)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME))
                )
            )
        }
        cursor.close()
        db.close()
        return feedbacks
    }

    fun updateFeedback(feedback: Feedback): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_RATING, feedback.rating)
            put(COL_COMMENT, feedback.comment)
        }
        val result = db.update(TABLE_FEEDBACK, values, "$COL_FEEDBACK_ID = ?", arrayOf(feedback.feedbackId))
        db.close()
        return result > 0
    }

    fun deleteFeedback(feedbackId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_FEEDBACK, "$COL_FEEDBACK_ID = ?", arrayOf(feedbackId))
        db.close()
        return result > 0
    }

    fun getFeedbackById(feedbackId: String): Feedback? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT f.*, u.$COL_FULL_NAME 
            FROM $TABLE_FEEDBACK f
            JOIN $TABLE_USER u ON f.$COL_USER_ID = u.$COL_USER_ID
            WHERE f.$COL_FEEDBACK_ID = ?
            """.trimIndent(),
            arrayOf(feedbackId)
        )

        val feedback = if (cursor.moveToFirst()) {
            Feedback(
                feedbackId = cursor.getString(cursor.getColumnIndexOrThrow(COL_FEEDBACK_ID)),
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                roomId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_ID)),
                rating = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RATING)),
                comment = cursor.getString(cursor.getColumnIndexOrThrow(COL_COMMENT)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME))
            )
        } else null

        cursor.close()
        db.close()
        return feedback
    }

    // Generate unique ID (e.g., USR001, BK001)
    fun generateId(prefix: String): String {
        val db = readableDatabase
        val tableName = when (prefix) {
            "USR" -> TABLE_USER
            "RM" -> TABLE_STUDY_ROOM
            "BK" -> TABLE_BOOKING
            "FB" -> TABLE_FEEDBACK
            "SCH" -> TABLE_SCHEDULE
            else -> return "${prefix}001"
        }

        val columnName = when (prefix) {
            "USR" -> COL_USER_ID
            "RM" -> COL_ROOM_ID
            "BK" -> COL_BOOKING_ID
            "FB" -> COL_FEEDBACK_ID
            "SCH" -> COL_SCHEDULE_ID
            else -> return "${prefix}001"
        }

        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName DESC LIMIT 1", null)
        var newId = "${prefix}001"

        if (cursor.moveToFirst()) {
            val lastId = cursor.getString(0)
            val lastNum = lastId.substring(3).toIntOrNull() ?: 0
            val newNum = lastNum + 1
            newId = String.format("%s%03d", prefix, newNum)
        }

        cursor.close()
        db.close()
        return newId
    }
}