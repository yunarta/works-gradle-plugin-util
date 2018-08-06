package com.mobilesolutionworks.gradle.jacoco.tasks

import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

open class JacocoTestKitKotlinConfigureDeps : ConfigureDeps() {

    @OutputFile
    val library: File

    init {
        val loader = JacocoTestKitKotlinConfigureDeps::class.java.classLoader
        val name = Properties().apply {
            load(loader.getResourceAsStream("META-INF/testKitKotlin.properties"))
        }.getOrDefault("TestKitKotlin", "").toString()

        library = project.buildDir.withPaths("testKitKotlin", "libs", name)
        outputs.file(library)
    }

    @TaskAction
    override fun extract() {
        library.parentFile.mkdirs()
        val resource = "META-INF/${library.name}"
        JacocoTestKitKotlinConfigureDeps::class.java.classLoader.getResourceAsStream(resource)
                .copyTo(library.outputStream())
    }
}