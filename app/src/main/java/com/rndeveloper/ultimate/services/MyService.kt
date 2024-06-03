package com.rndeveloper.ultimate.services

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.common.base.Stopwatch
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.utils.Utils.sendNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

//@AndroidEntryPoint
class MyService : LifecycleService() {

//    @Inject
//    lateinit var userRepository: UserRepository
//
//    @Inject
//    lateinit var locationClient: LocationClient

    @Inject
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        setSpotData()
        return START_NOT_STICKY
    }

    private fun setSpotData() =
        lifecycleScope.launch {



        }
}