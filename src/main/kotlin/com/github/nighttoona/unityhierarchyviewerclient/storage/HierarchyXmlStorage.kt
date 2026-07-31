package com.github.nighttoona.unityhierarchyviewerclient.storage

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class HierarchyXmlStorage {

    private fun getFilePath(project: Project): Path{
        return Paths.get(
            PathManager.getSystemPath(), "unity-hierarchy-viewer-client", project.name, "lastHierarchy.xml"
        )
    }

    fun saveXml(project: Project, xml: String){

        val file = getFilePath(project)
        Files.createDirectories(file.parent)

        // （需写入文件Path，内容，编码，不存在则创建，覆写）
        Files.writeString(file, xml, Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }

    fun loadXml(project: Project): String?{

        val file = getFilePath(project)

        if (Files.exists(file)){
            return Files.readString(file, Charsets.UTF_8)
        }

        return null
    }

}