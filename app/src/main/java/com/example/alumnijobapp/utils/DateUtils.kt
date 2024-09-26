package com.example.alumnijobapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun isDateExpired(expirationDate: Date): Boolean {
        val currentDate = Calendar.getInstance().time
        return expirationDate.before(currentDate)
    }

    fun getRemainingDays(expirationDate: Date): Int {
        val currentDate = Calendar.getInstance().time
        val diff = expirationDate.time - currentDate.time
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }
}