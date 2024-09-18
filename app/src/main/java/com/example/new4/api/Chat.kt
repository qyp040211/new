package com.example.new4.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class MessageRequest(
    val content: String,
    val toUserId: Long,  // 或者 String
    val userId: Long // 或者 String
)

interface ChatApi {
    @Headers("appId: 635139ffbf9045be9e94bae941d0a15d", "appSecret: 68753e30f7e9bdc6f475784fa4a49c90be15a")
    @POST("api/member/tran/chat")
    fun sendMessage(@Body messageRequest: MessageRequest): Call<Unit>
}