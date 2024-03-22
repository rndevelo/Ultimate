package com.rndeveloper.ultimate.notifications

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers(
        "Authorization: key=AAAAkt91Ewk:APA91bHSSzdhe12abF2TGxKj6M1HNe-sCudgXuKBKmjSavX3AtEw2JUElvfWM6l709VKGRiZk5JW7WLFRbFLfB2zd5C_hoQ2UGBkRogqNEPgXh16EqvQfBmgDlHwsR_TYLPASTz3kmqn",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}