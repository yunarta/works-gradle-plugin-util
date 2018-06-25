package com.mobilesolutionworks.gradle.jacoco.tasks

import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

open class JacocoTestKitConfigureDeps : DefaultTask() {

    @OutputFile
    val library: File

    init {
        val loader = JacocoTestKitConfigureDeps::class.java.classLoader
        val name = Properties().apply {
            load(loader.getResourceAsStream("META-INF/testKit.properties"))
        }.getOrDefault("TestKit", "").toString()

        library = project.buildDir.withPaths("testKit", "libs", name)
        outputs.file(library)
    }

    @TaskAction
    fun extract() {
        library.parentFile.mkdirs()
        val resource = "META-INF/${library.name}"
        JacocoTestKitConfigureDeps::class.java.classLoader.getResourceAsStream(resource)
                .copyTo(library.outputStream())
    }
}