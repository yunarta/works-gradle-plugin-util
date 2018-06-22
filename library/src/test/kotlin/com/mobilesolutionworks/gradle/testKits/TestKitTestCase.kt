package com.mobilesolutionworks.gradle.testKits

import com.mobilesolutionworks.gradle.testUtils.CopyResourceFolder
import org.junit.After
import org.junit.Before
import java.io.File
import java.util.*

open class TestKitTestCase(folder: String) {

    val tempDir = CopyResourceFolder(File("build/tmp/testKit"), folder)

    @Before
    fun createJavaAgent() {
        tempDir.create()
        javaClass.classLoader.getResourceAsStream("javaagent-for-testkit.properties")?.let {
            Properties().apply {
                load(it)
            }.let {
                val agentPath = it.getProperty("agentPath")
                val outputDir = it.getProperty("outputDir")

                val execFile = File(outputDir, "${javaClass.name}.exec")
                val agentString = "org.gradle.jvmargs=-javaagent\\:${agentPath}\\=destfile\\=${execFile.absolutePath}"

                File(tempDir.root, "gradle.properties").writeText(agentString)
            }
        }
    }

    @After
    fun tearDown() {
        // tempDir.delete()
    }
}
