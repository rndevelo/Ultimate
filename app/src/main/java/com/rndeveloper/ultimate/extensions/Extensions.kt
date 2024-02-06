package com.rndeveloper.ultimate.extensions

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.os.Build
import androidx.activity.ComponentActivity
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.theme.blue_place_icon
import com.rndeveloper.ultimate.ui.theme.green_place_icon
import com.rndeveloper.ultimate.ui.theme.red_place_icon
import com.rndeveloper.ultimate.ui.theme.yellow_place_icon
import com.rndeveloper.ultimate.utils.BitmapHelper
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Utils
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

// SORT SPOTS AND AREAS
fun List<Spot>.sortItems(
    context: Context,
    positions: Pair<Position, Position>
) = this.filter { spot ->
    GeoFireUtils.getDistanceBetween(
        GeoLocation(spot.position.lat, spot.position.lng),
        GeoLocation(positions.first.lat, positions.first.lng)
    ) <= Constants.SPOTS_RADIUS_TARGET
}.sortedWith { d1, d2 ->
    val currentLoc = Location("currentLoc")
    currentLoc.latitude = positions.second.lat
    currentLoc.longitude = positions.second.lng

    val targets1 = Location("target")
    targets1.latitude = d1.position.lat
    targets1.longitude = d1.position.lng

    val targets2 = Location("location")
    targets2.latitude = d2.position.lat
    targets2.longitude = d2.position.lng

    val distanceOne = currentLoc.distanceTo(targets1)
    val distanceTwo = currentLoc.distanceTo(targets2)

    distanceOne.compareTo(distanceTwo)
}.map { spot ->
    val distance = FloatArray(2)
    Location.distanceBetween(
        spot.position.lat, spot.position.lng,
        positions.second.lat, positions.second.lng, distance
    )

    val timeResult = Utils.currentTime() - spot.timestamp

    val color = when {
        timeResult < 0 -> blue_place_icon
        timeResult < Constants.MINUTE * 10 -> green_place_icon
        timeResult < Constants.MINUTE * 20 -> yellow_place_icon
        else -> red_place_icon
    }
    val drawable = when {
        timeResult < 0 -> R.drawable.ic_spot_round_marker
        timeResult < Constants.MINUTE * 10 -> R.drawable.ic_spot_round_marker
        timeResult < Constants.MINUTE * 20 -> R.drawable.ic_spot_round_yellow_marker
        else -> R.drawable.ic_spot_round_red_marker
    }

    val icon = BitmapHelper.vectorToBitmap(context = context, id = drawable)

    spot.copy(distance = "${distance[0].toInt()}m", color = color, icon = icon)
}


//FIXME: THIS
@SuppressLint("SimpleDateFormat")
fun Long.getFormattedPrettyTime(): String {
    val format = SimpleDateFormat("HH:mm:ss\ndd-MM-yyyy", Locale.getDefault())
    val date: Date? = format.parse(format.format(this))
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(date)
}