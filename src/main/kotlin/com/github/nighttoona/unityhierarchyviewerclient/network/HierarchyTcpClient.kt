package com.github.nighttoona.unityhierarchyviewerclient.network

import kotlinx.coroutines.Dispatchers
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

    private val connectMutex = Mutex()

    // 控制连接状态的标志位，@Volatile允许跨线程访问
    @Volatile
    private var isConnected: Boolean = false



    suspend fun connect(host: String, port: Int){

        // 使用互斥锁来确保同一时间只有一个协程可以执行连接操作
        connectMutex.withLock {

            if (isConnected) return@withLock
            val newSocket = Socket()

            try {
                // 只对连接操作进行IO调度，避免阻塞主线程
                withContext(Dispatchers.IO){
                    newSocket.connect(InetSocketAddress(host, port), 5000)
                }

                socket = newSocket
                isConnected = true

                println("Client 启动成功")
            }
            catch (e: Exception){
                newSocket.close()
                isConnected = false
                throw e
            }
        }
    }


    suspend fun receiveLoop(){

        withContext(Dispatchers.IO){

            val currentSocket = socket ?: return@withContext
            val inputStream = currentSocket.getInputStream()

            val input = DataInputStream(BufferedInputStream(inputStream))

            try {

                // 保持长连接
                while (isConnected){
                    val message = readMessage(input)

                    when(message.typeCode){
                        MessageType.XML.code ->{
                            println(message.body)
                            println("XML消息")
                        }
                        MessageType.HEARTBEAT_PONG.code ->{
                            println("收到心跳响应")
                        }
                        MessageType.CLOSE.code ->{
                            println("收到关闭消息，断开连接")
                        }
                        MessageType.NONE.code ->{
                            println("空测试消息，无需处理")
                        }
                        else ->{
                            println("未知消息类型：${message.typeCode}")
                        }
                    }

                }

            }
            catch (error: IOException){
                println("读取异常：${error.message}")
            }
            catch (error: EOFException){
                println("连接已关闭")
            }
            finally {
                isConnected = false
                currentSocket.close()
                if (socket == currentSocket) socket = null

                println("连接关闭")
            }

        }

    }

    // 读取消息，返回TcpMessage对象
    private fun readMessage(input: DataInputStream): TcpMessage{

        val typeCode = readHeaderNumber(input)
        val bodyLength = readHeaderNumber(input)

        // 防恶意攻击，限制消息体长度在合理范围内
        if (bodyLength !in 0 .. 10_000_000){
            throw IOException("消息长度异常：$bodyLength")
        }

        val bodyBytes = ByteArray(bodyLength)
        input.readFully(bodyBytes)

        return TcpMessage(typeCode, bodyBytes.toString(Charsets.UTF_8))
    }


    // 读取消息头中的数字，格式为[数字]
    private fun readHeaderNumber(input: DataInputStream): Int{

        // 该方法每次只读取单个字节，但会自动往后移动流的位置（这和字符串处理有很大差别）
        val opening = input.read()

        if (opening == -1) throw EOFException("连接已关闭")
        if (opening != '['.code) throw IOException("消息格式错误")

        val numberText = StringBuilder()

        while (true){

            val current = input.read()

            if (current == -1) throw EOFException("连接已关闭")
            if (current == ']'.code) break

            numberText.append(current.toChar())
        }

        return numberText.toString().toInt()
    }

}