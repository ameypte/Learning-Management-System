package com.example.staffdashboard.notification.api

import com.example.staffdashboard.notification.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA2Mju2kk:APA91bGNkoTvNrSKayvx1YAZ6OkMQzHpypLeevFKCkurHVqdncZQznAPFof206Uf49atHsd19uWGV7eKsI_rLCxtwepDIIDL35s8i62qGbn2CeH0Qvs6LVFtL0ca_Bdv8HtbON1K4Uts"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification): Call<PushNotification>
}