package com.example.new4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.new4.api.ChatApi
import com.example.new4.api.MessageRequest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatActivity : ComponentActivity() {
    private val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-store.openguet.cn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen(api)
        }
    }
}

@Composable
fun ChatScreen(api: ChatApi) {
    var messageText by remember { mutableStateOf("") }
    val toUserId = 12345L // TODO: 设置接收者的用户ID
    val userId = 67890L // TODO: 设置发送者的用户ID

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            label = { Text("消息内容") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { sendMessage(api, messageText, toUserId, userId) }) {
            Text("发送消息")
        }
    }
}

fun sendMessage(api: ChatApi, content: String, toUserId: Long, userId: Long) {
    val request = MessageRequest(content, toUserId, userId)
    api.sendMessage(request).enqueue(object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            if (response.isSuccessful) {
                // 处理成功
                println("消息发送成功")
            } else {
                // 处理失败
                println("消息发送失败: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            // 处理网络错误
            println("网络错误: ${t.message}")
        }
    })
}