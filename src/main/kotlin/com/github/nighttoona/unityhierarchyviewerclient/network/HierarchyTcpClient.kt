package com.github.nighttoona.unityhierarchyviewerclient.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


data class TcpMessage(
    val typeCode: Int,
    val body: String
)


class HierarchyTcpClient {

    private var socket: Socket? = null

    private val runMutex = Mutex()
    private val sendMutex = Mutex()

    // 控制连接状态的标志位，@Volatile允许跨线程访问
    @Volatile
    private var isConnected: Boolean = false

    @Volatile
    private var isRunning: Boolean = false


    // 只用于负责连接操作，避免各类状态干扰造成的锁死或异常
    // 对该方法启用额外的IO线程池，避免阻塞主线程
    private suspend fun connect(host: String, port: Int): Boolean = withContext(Dispatchers.IO) {

        val newSocket = Socket()

        try {
            newSocket.connect(InetSocketAddress(host, port), 5000)

            socket = newSocket
            isConnected = true

            println("Client 启动成功")
            true

        } catch (error: IOException) {

            newSocket.close()
            isConnected = false

            println("Client 启动失败：${error.message}")
            false
        }
    }


    // 运行客户端，保持连接并接收消息
    suspend fun run(host: String, port: Int) {

        // 尝试上互斥锁，如果已锁则返回false，从而避免重复运行
        if (!runMutex.tryLock()) {
            println("Client 已经在运行中")
            return
        }

        isRunning = true
        var failedAttempts = 0

        try {
            while (isRunning) {

                if (connect(host, port)) {

                    failedAttempts = 0
                    receiveLoop()

                } else {
                    failedAttempts++
                    println("连接失败，尝试次数：$failedAttempts")
                    if (failedAttempts >= 3) break
                }


                if (isRunning) {
                    // 等待5秒后重新连接
                    delay(5_000)
                    println("尝试重新连接...")
                }
            }
        } finally {

            println("已停止运行")
            stop()
            runMutex.unlock()
        }

    }


    private suspend fun receiveLoop() {

        withContext(Dispatchers.IO) {

            val currentSocket = socket ?: return@withContext
            val inputStream = currentSocket.getInputStream()

            val input = DataInputStream(BufferedInputStream(inputStream))

            try {

                // 保持长连接
                while (isConnected) {
                    val message = readMessage(input)

                    when (message.typeCode) {
                        MessageType.XML.code -> {
                            println(message.body)
                            println("XML消息")
                        }

                        MessageType.HEARTBEAT_PING.code -> {
                            println("收到心跳响应")
                            sendMessage(MessageType.HEARTBEAT_PONG)
                            println("心跳回复已发送")
                        }

                        MessageType.CLOSE.code -> {
                            println("收到关闭消息，断开连接")
                        }

                        MessageType.NONE.code -> {
                            println("空测试消息，无需处理")
                        }

                        else -> {
                            println("未知消息类型：${message.typeCode}")
                        }
                    }

                }

            } catch (error: EOFException) {
                println("连接已关闭")
            } catch (error: IOException) {
                println("读取异常：${error.message}")
            } finally {
                isConnected = false
                currentSocket.close()
                if (socket == currentSocket) socket = null

                println("连接关闭")
            }

        }

    }


    // 发送消息，使用互斥锁保证线程安全
    private suspend fun sendMessage(type: MessageType, body: String = ""){

        val bodyByte = body.toByteArray(Charsets.UTF_8)
        val headerByte = "[${type.code}][${bodyByte.size}]".toByteArray(Charsets.UTF_8)
        val messageByte = headerByte + bodyByte

        withContext(Dispatchers.IO){
            sendMutex.withLock {

                val currentSocket = socket ?: return@withLock
                val output = currentSocket.getOutputStream()

                output.write(messageByte)
                output.flush()
            }
        }

    }


    // 停止客户端，关闭连接
    private fun stop() {
        isRunning = false
        isConnected = false

        // 关闭socket连接，避免资源泄漏
        val currentSocket = socket
        socket = null
        currentSocket?.close()
    }


    // 读取消息，返回TcpMessage对象
    private fun readMessage(input: DataInputStream): TcpMessage {

        val typeCode = readHeaderNumber(input)
        val bodyLength = readHeaderNumber(input)

        // 防恶意攻击，限制消息体长度在合理范围内
        if (bodyLength !in 0..10_000_000) {
            throw IOException("消息长度异常：$bodyLength")
        }

        val bodyBytes = ByteArray(bodyLength)
        input.readFully(bodyBytes)

        return TcpMessage(typeCode, bodyBytes.toString(Charsets.UTF_8))
    }


    // 读取消息头中的数字，格式为[数字]
    private fun readHeaderNumber(input: DataInputStream): Int {

        // 该方法每次只读取单个字节，但会自动往后移动流的位置（这和字符串处理有很大差别）
        val opening = input.read()

        if (opening == -1) throw EOFException("连接已关闭")
        if (opening != '['.code) throw IOException("消息格式错误")

        val numberText = StringBuilder()

        while (true) {

            val current = input.read()

            if (current == -1) throw EOFException("连接已关闭")
            if (current == ']'.code) break

            numberText.append(current.toChar())
        }

        return numberText.toString().toInt()
    }

}