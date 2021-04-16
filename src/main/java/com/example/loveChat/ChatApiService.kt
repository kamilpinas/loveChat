package com.example.loveChat

import retrofit2.Call
import retrofit2.http.*

interface ChatApiService {

    @GET("shoutbox/messages")
    fun getMessages(): Call<ArrayList<ChatItem>>

    @POST("shoutbox/message")
    fun postMessage(@Body chatitem: ChatItem): Call<ChatItem>

    @PUT("shoutbox/message/{id}")
    fun putMessage(
        @Path("id") id: String,
        @Body chatitem: ChatItem
    ): Call<ChatItem>

    @DELETE("shoutbox/message/{id}")
    fun deleteMessage(@Path("id") id: String): Call<ChatItem>
}