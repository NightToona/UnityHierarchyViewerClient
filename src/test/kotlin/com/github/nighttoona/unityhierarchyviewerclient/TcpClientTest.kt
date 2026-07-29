package com.github.nighttoona.unityhierarchyviewerclient

import com.github.nighttoona.unityhierarchyviewerclient.network.HierarchyTcpClient
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TcpClientTest {

    @Test
    fun testTcpClient() = runBlocking {

        val client = HierarchyTcpClient()

        client.run("127.0.0.1", 44571)

    }

}