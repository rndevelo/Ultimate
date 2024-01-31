package com.rndeveloper.ultimate.extensions

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Int.fixApi31(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this or PendingIntent.FLAG_MUTABLE
    } else {
        this
    }
}

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}




//FIXME: THIS
@SuppressLint("SimpleDateFormat")
fun Long.getFormattedPrettyTime(): String {
    val format = SimpleDateFormat("HH:mm:ss\ndd-MM-yyyy", Locale.getDefault())
    val date: Date? = format.parse(format.format(this))
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(date)
}