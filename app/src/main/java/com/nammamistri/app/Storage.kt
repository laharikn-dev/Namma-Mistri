package com.nammamistri.app

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

object Storage {
    @PublishedApi
    internal const val PREF = "namma_mistri"

    @PublishedApi
    internal val gson = Gson()

    fun <T> save(ctx: Context, key: String, value: T) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putString(key, gson.toJson(value))
        }
    }

    inline fun <reified T> load(ctx: Context, key: String, default: T): T {
        val raw = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(key, null) ?: return default

        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson<T>(raw, type) ?: default
        } catch (_: Exception) {
            default
        }
    }

    fun inr(amount: Double): String =
        "₹" + String.format(Locale.US, "%,.0f", amount)
}

data class Rates(
    var brick: Double = 8.0,
    var cementBag: Double = 350.0,
    var sandCft: Double = 50.0
)

data class Worker(
    val id: String,
    var name: String,
    var dailyWage: Double,
    var attendance: MutableMap<String, String> = mutableMapOf(), // date -> P/A/H
    var advances: MutableList<Advance> = mutableListOf()
) {
    fun earned(): Double {
        var days = 0.0
        for (s in attendance.values) when (s) {
            "P" -> days += 1.0
            "H" -> days += 0.5
        }
        return days * dailyWage
    }
    fun advanceTotal(): Double = advances.sumOf { it.amount }
    fun balance(): Double = earned() - advanceTotal()
}

data class Advance(val date: String, val amount: Double)

data class Photo(val id: String, val uri: String, val caption: String, val date: String)
