package com.rndeveloper.ultimate.services

import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rndeveloper.ultimate.utils.Utils.sendNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        sendNotification(
            this,
            "¡Enhorabuena!, Alguien encontró una plaza gracias a ti.",
            "Has ganado 5 créditos",
            32
        )
    }
}