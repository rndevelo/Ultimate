package com.rndeveloper.ultimate.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.rndeveloper.ultimate.model.PrivacyPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrivacyPolicyRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: DataStore<Preferences>
) : PrivacyPolicyRepository {

    override suspend fun saveData(privacyPolicy: PrivacyPolicy) {
        userPreferencesRepository.edit { preferences ->
            preferences[stringPreferencesKey(privacyPolicy.id)] =
                Gson().toJson(privacyPolicy, PrivacyPolicy::class.java)
        }
    }

    override fun getData(key: String): Flow<PrivacyPolicy> =
        userPreferencesRepository.data.map { preferences ->
            preferences[stringPreferencesKey(key)].let { json ->
                Gson().fromJson(json, PrivacyPolicy::class.java) ?: PrivacyPolicy(key)
            }
        }
}

interface PrivacyPolicyRepository {

    suspend fun saveData(privacyPolicy: PrivacyPolicy)

    fun getData(key: String): Flow<PrivacyPolicy>
}