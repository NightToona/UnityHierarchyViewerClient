package com.github.nighttoona.unityhierarchyviewerclient.services

import com.github.nighttoona.unityhierarchyviewerclient.network.HierarchyTcpClient
import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyData
import com.github.nighttoona.unityhierarchyviewerclient.parser.XmlParse
import com.github.nighttoona.unityhierarchyviewerclient.storage.HierarchyXmlStorage
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// 创建抽象类限制执行方法
fun interface HierarchyListener {
    fun onChange(data: HierarchyData)
}


@Service(Service.Level.PROJECT)
class HierarchyService(private val project: Project, private val scope: CoroutineScope) {

    // 监听列表
    private val listeners = mutableListOf<HierarchyListener>()
    private val xmlStorage = HierarchyXmlStorage()

    // 添加监听器
    fun addListener(listener: HierarchyListener) {
        listeners.add(listener)

        // 加载本地数据并通知监听器
        val data = loadLastHierarchy()
        if (data != null) listener.onChange(data)
    }

    // 移除监听器
    fun removeListener(listener: HierarchyListener) {
        listeners.remove(listener)
    }

    // 通知所有监听器
    fun updateHierarchy(xml: String) {

        val data = XmlParse().parseData(xml)

        xmlStorage.saveXml(project, xml)


        // 转移到UI线程执行，避免阻塞
        ApplicationManager.getApplication().invokeLater {
            // 通知所有监听器执行
            listeners.forEach { listener -> listener.onChange(data) }
        }
    }


    // 读取本地数据，恢复上次的层级结构
    private fun loadLastHierarchy(): HierarchyData? {

        try {
            val xml = xmlStorage.loadXml(project)

            if (xml == null) {
                return null
            }
            return XmlParse().parseData(xml)
        } catch (error: Exception) {
            println("读取本地数据异常: ${error.message}")
            return null
        }

    }


    // 连接、断开连接、发送气泡消息等操作都在这个类里执行

    private val tcpClient = HierarchyTcpClient(this)

    fun runClient() = scope.launch {
        tcpClient.run("127.0.0.1", 44571)
    }

    fun showNotification(message: String, type: NotificationType) {
        Notification("Unity Hierarchy Viewer", message, type).notify(project)
    }


}