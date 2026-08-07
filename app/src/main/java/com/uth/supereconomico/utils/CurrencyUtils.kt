package com.uth.supereconomico.utils

import java.util.Locale

object CurrencyUtils {
    fun formatGTQ(amount: Double?): String {
        return String.format(Locale.getDefault(), "Q%.2f", amount ?: 0.0)
    }
}
