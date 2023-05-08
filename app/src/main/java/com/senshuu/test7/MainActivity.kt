package com.senshuu.test7

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


class MainActivity : AppCompatActivity() {

    private lateinit var tcpServer: TCPServer
    private lateinit var receiveTextView: TextView
    private lateinit var smokeTextView: TextView
    private lateinit var fireTextView: TextView
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 TextView 对象
        receiveTextView = findViewById(R.id.receive_text_view)
        smokeTextView = findViewById(R.id.smoke_text_view)
        fireTextView = findViewById(R.id.fire_text_view)
        errorTextView = findViewById(R.id.error)


        // 初始化 TCPServer 对象
        tcpServer = TCPServer(8080)

        // 将 TextView 对象赋值给 TCPServer 的 receiveTextView 属性
        tcpServer.receiveTextView = receiveTextView
        tcpServer.smokeTextView = smokeTextView
        tcpServer.fireTextView = fireTextView

        // 启动 TCPServer
        tcpServer.start()
    }
}



