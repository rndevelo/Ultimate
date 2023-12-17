package com.rndeveloper.ultimate.extensions

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.text.format.DateUtils
import com.google.android.gms.maps.model.LatLng
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

fun Long.toTime(): String = DateUtils.formatElapsedTime(this.div(1000))


fun Long.getFormattedTime(): String =
    SimpleDateFormat("HH:mm:ss\ndd-MM-yyyy", Locale.getDefault()).format(this)

@SuppressLint("SimpleDateFormat")
fun Long.getFormattedPrettyTime(): String {

    val inputFormat = SimpleDateFormat("HH:mm:ss\ndd-MM-yyyy")
    val date: Date? = inputFormat.parse(this.getFormattedTime())
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(date)
}

fun LatLng.getAddressList(geocoder: Geocoder?, callback: (MutableList<Address>) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder?.getFromLocation(
            this.latitude,
            this.longitude,
            1
        ) { addressList ->
            callback(addressList)
        }
    } else {
        geocoder?.getFromLocation(this.latitude, this.longitude, 1)
            ?.let { addressList ->
                callback(addressList)
            }
    }
}