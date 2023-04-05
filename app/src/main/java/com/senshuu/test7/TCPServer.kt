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

class TCPServer(private val port: Int) {

    private val clients = mutableListOf<Socket>()

    // 添加一个公共的 TextView 属性，用于更新接收到的信息
    var receiveTextView: TextView? = null

    fun start() {
        GlobalScope.launch {
            val serverSocket = ServerSocket(port)
            println("Server started on port $port")
            while (true) {
                val client = serverSocket.accept()
                clients.add(client)
                println("Client connected: ${client.inetAddress.hostAddress}:${client.port}")
                withContext(Dispatchers.IO) {
                    val inputStream = client.getInputStream()
                    val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    var line: String?
                    do {
                        line = reader.readLine()
                        if (line != null) {
                            println("Received from ${client.inetAddress.hostAddress}:${client.port}: $line")
                            // 在主线程更新 UI
                            withContext(Dispatchers.Main) {
                                receiveTextView?.text = line
                            }
                        }
                    } while (line != null)
                }
            }
        }
    }

    fun stop() {
        // not implemented
    }

    fun broadcast(message: String) {
        GlobalScope.launch {
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

