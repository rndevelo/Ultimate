package com.rndeveloper.ultimate.notifications

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=xyNfH2zQNwXx-Siahvc-k_wIIkfKMGCKqoJmGoZgtf4", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body token: String
    ) : Response<ResponseBody>
}