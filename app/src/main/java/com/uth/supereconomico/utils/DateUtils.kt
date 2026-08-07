package com.uth.supereconomico.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatSupabaseDate(dateString: String?): String {
        if (dateString == null) return ""
        return try {
            // Supabase format usually includes microseconds and Z: 2026-08-07T05:38:41.732Z
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}
