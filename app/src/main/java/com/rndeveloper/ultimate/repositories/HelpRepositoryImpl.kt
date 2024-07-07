package com.rndeveloper.ultimate.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.rndeveloper.ultimate.model.Help
import com.rndeveloper.ultimate.model.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HelpRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: DataStore<Preferences>
) : HelpRepository {

    override suspend fun saveData(help: Help) {
        userPreferencesRepository.edit { preferences ->
            preferences[stringPreferencesKey(help.id)] =
                Gson().toJson(help, Help::class.java)
        }
    }

    override fun getData(key: String): Flow<Help> =
        userPreferencesRepository.data.map { preferences ->
            preferences[stringPreferencesKey(key)].let { json ->
                Gson().fromJson(json, Help::class.java) ?: Help(key)
            }
        }
}

interface HelpRepository {

    suspend fun saveData(help: Help)

    fun getData(key: String): Flow<Help>
}