package com.rndeveloper.ultimate.services

import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.utils.Utils.sendNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private var sharedPref: SharedPreferences? = null

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
            getString(R.string.home_text_notification_congratulations_found_place),
            getString(R.string.home_text_notification_you_have_earned_5_credits),
            20
        )
    }
}