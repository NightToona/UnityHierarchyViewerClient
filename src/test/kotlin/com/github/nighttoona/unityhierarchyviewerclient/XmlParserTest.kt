package com.github.nighttoona.unityhierarchyviewerclient

import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyNode
import com.github.nighttoona.unityhierarchyviewerclient.parser.XmlParse
import org.junit.Test

class XmlParserTest {

    @Test
    fun xmlParserTest() {

        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <Hierarchy xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" SceneName="SampleScene" ExportTime="2026-07-28T12:39:31.2736052+08:00">
              <GameObject Name="Main Camera" ID="28420" Active="true" Script="UniversalAdditionalCameraData" />
              <GameObject Name="Global Light 2D" ID="28430" Active="true" Script="Light2D" />
              <GameObject Name="test test" ID="28436" Active="true" Script="">
                <GameObject Name="Cube" ID="28440" Active="true" Script="">
                  <GameObject Name="Cylinder" ID="28392" Active="true" Script="" />
                </GameObject>
                <GameObject Name="Point Light" ID="28402" Active="true" Script="UniversalAdditionalLightData" />
              </GameObject>
              <GameObject Name="Plane" ID="28410" Active="false" Script="">
                <GameObject Name="Sphere" ID="28450" Active="true" Script="" />
              </GameObject>
            </Hierarchy>
        """.trimIndent()

        val data = XmlParse().parseData(xml)

        println("Scene Name: ${data.sceneName}")
        println("Export Time: ${data.exportTime}")

        for (root in data.roots){
            printNode(root)
        }

    }


    // 递归打印节点及其子节点
    private fun printNode(node: HierarchyNode, depth: Int = 0){

        val indent = "    ".repeat(depth)
        println("$indent${node.name}")

        for (child in node.children){
            printNode(child, depth + 1)
        }

    }


}