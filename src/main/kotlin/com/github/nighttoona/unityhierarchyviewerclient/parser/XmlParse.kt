package com.github.nighttoona.unityhierarchyviewerclient.parser

import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.OffsetDateTime
import javax.xml.parsers.DocumentBuilderFactory


data class HierarchyData(
    var sceneName: String,
    var exportTime: OffsetDateTime,
    var roots: MutableList<HierarchyNode> = mutableListOf()
)

data class HierarchyNode(
    var name: String,
    var instance: Int,
    var isActive: Boolean,
    var scriptName: String?,
    var children: MutableList<HierarchyNode> = mutableListOf()
)

class XmlParse {

    // 解析XML字符串为HierarchyData对象
    fun parseData(xml: String): HierarchyData{

        // 解析XML字符串
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(xml)))

        // 获取根元素
        val hierarchyElement = document.documentElement

        // 获取场景名称和导出时间
        val sceneName = hierarchyElement.getAttribute("SceneName")
        val exportTime = hierarchyElement.getAttribute("ExportTime")
        val roots = mutableListOf<HierarchyNode>()


        // 获取根节点
        val childNodes = hierarchyElement.childNodes

        for (index in 0 ..< childNodes.length){

            val child = childNodes.item(index)

            if (child is Element && child.tagName == "GameObject"){

                roots.add(parseNode(child))
            }
        }

        return HierarchyData(sceneName, OffsetDateTime.parse(exportTime), roots)
    }


    private fun parseNode(element: Element): HierarchyNode{

        val node = HierarchyNode(
            name = element.getAttribute("Name"),
            instance = element.getAttribute("ID").toInt(),
            isActive = element.getAttribute("Active").toBoolean(),
            scriptName = element.getAttribute("Script").ifBlank { null },
            children = mutableListOf()
        )

        // 获取子节点
        val childNode = element.childNodes

        // 遍历子节点，递归解析(范围运算符 ..< 表示从0到childNode.length-1)
        for (index in 0 ..< childNode.length){

            val child = childNode.item(index)

            if (child is Element && child.tagName == "GameObject") {
                node.children.add(parseNode(child))
            }
        }

        return node
    }
}