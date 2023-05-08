package com.senshuu.test7

import android.app.Activity
import android.os.Handler
import android.widget.TextView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import android.view.View
import android.widget.*

// 定义 TCPServer 类，构造函数需要一个端口号作为参数
class TCPServer(private val port: Int) {

    // 定义一个可变列表，用于保存所有连接的客户端 Socket
    private val clients = mutableListOf<Socket>()

    // 定义一个可空的 TextView 属性，用于更新接收到的信息
    var receiveTextView: TextView? = null
    var smokeTextView: TextView? = null
    var fireTextView: TextView? = null
    var errorTextView: TextView? = null

    // 定义 start 函数，用于启动服务器
    fun start() {
        GlobalScope.launch {
            // 创建服务器 Socket，并绑定端口号
            val serverSocket = ServerSocket(port)
            println("Server started on port $port")

            // 不断接受客户端的连接
            while (true) {
                val client = serverSocket.accept()
                // 将新连接的客户端 Socket 添加到列表中
                clients.add(client)
                println("Client connected: ${client.inetAddress.hostAddress}:${client.port}")

                // 在 IO 线程中读取客户端发送的消息
                withContext(Dispatchers.IO) {
                    val inputStream = client.getInputStream()
                    val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    var line: String?
                    do {
                        line = reader.readLine()
                        if (line != null) {
                            println("Received from ${client.inetAddress.hostAddress}:${client.port}: $line")
                            // 在主线程中更新 UI，显示接收到的消息
                            when (line) {
                                "UP", "DOWN", "STOP" -> receiveTextView?.text = "运行状态：+ $line"
                                "SMOKE" -> smokeTextView?.text = "烟雾：SMOKE"
                                "FIRE" -> fireTextView?.text = "火焰：FIRE"
                            }
                            if (!smokeTextView?.text.isNullOrEmpty() || !fireTextView?.text.isNullOrEmpty()) {
                                errorTextView?.text = "警报！"
                            }



                        }
                    } while (line != null)
                }
            }
        }
    }

    // 定义 stop 函数，用于停止服务器
    fun stop() {
        // 未实现
    }

    // 定义 broadcast 函数，用于向所有客户端广播消息
    fun broadcast(message: String) {
        GlobalScope.launch {
            // 遍历所有客户端 Socket，向其发送消息
            clients.forEach {
                try {
                    withContext(Dispatchers.IO) {
                        it.getOutputStream().write(message.toByteArray(StandardCharsets.UTF_8))
                    }
                } catch (e: Exception) {
                    println("Failed to send message to ${it.inetAddress.hostAddress}:${it.port}")
                    e.printStackTrace()
                }
            }
        }
    }
}

