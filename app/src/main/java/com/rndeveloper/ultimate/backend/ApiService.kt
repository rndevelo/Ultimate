package com.rndeveloper.ultimate.backend

import androidx.compose.foundation.interaction.DragInteraction
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") key: String,
        @Query("start", encoded = true) start: String,
        @Query("end", encoded = true) end: String
    ):retrofit2.Response<RouteResponse>
}