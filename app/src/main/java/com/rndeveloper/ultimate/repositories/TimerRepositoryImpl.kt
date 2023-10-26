package com.rndeveloper.ultimate.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.rndeveloper.ultimate.model.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: DataStore<Preferences>
) : TimerRepository {


    /** This is for set CustomCountDownTimer data in DataStore */

//    TODO: Refactorice timers

    override suspend fun saveTimer(timer: Timer) {
        userPreferencesRepository.edit { preferences ->
            preferences[stringPreferencesKey(timer.id)] =
                Gson().toJson(timer, Timer::class.java)
        }
    }

    override fun getTimer(key: String): Flow<Timer> =
        userPreferencesRepository.data.map { preferences ->
            preferences[stringPreferencesKey(key)].let { json ->
                Gson().fromJson(json, Timer::class.java) ?: Timer(key)
            }
        }
}

interface TimerRepository {

    suspend fun saveTimer(timer: Timer)

    fun getTimer(key: String): Flow<Timer>
}