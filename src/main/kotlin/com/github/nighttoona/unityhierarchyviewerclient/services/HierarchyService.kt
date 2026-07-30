package com.github.nighttoona.unityhierarchyviewerclient.services

import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyData
import com.github.nighttoona.unityhierarchyviewerclient.parser.XmlParse
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service


// 创建抽象类限制执行方法
fun interface HierarchyListener{
    fun onChange(data: HierarchyData)
}


@Service(Service.Level.PROJECT)
class HierarchyService {

    // 监听列表
    private val listeners = mutableListOf<HierarchyListener>()

    // 添加监听器
    fun addListener(listener: HierarchyListener){
        listeners.add(listener)
    }

    // 移除监听器
    fun removeListener(listener: HierarchyListener){
        listeners.remove(listener)
    }

    // 通知所有监听器
    fun updateHierarchy(xml: String){

        val data = XmlParse().parseData(xml)

        // 转移到UI线程执行，避免阻塞
        ApplicationManager.getApplication().invokeLater{
            // 通知所有监听器执行
            listeners.forEach{ listener -> listener.onChange(data) }
        }
    }

}