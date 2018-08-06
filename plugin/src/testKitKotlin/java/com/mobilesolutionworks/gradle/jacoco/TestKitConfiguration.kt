package com.mobilesolutionworks.gradle.jacoco

import java.io.File
import java.io.IOException
import java.util.*

class TestKitConfiguration(agentFile: String, execFileName: String) {

    val agentString: String?

    constructor(execFile: String) : this("javaagent-for-testkit.properties", execFile)

    init {
        val classLoader = javaClass.classLoader
        val stream = classLoader.getResourceAsStream(agentFile)

        var agentString: String? = null
        if (stream != null) {
            try {
                val properties = Properties()
                properties.load(stream)

                val agentPath = properties.getProperty("agentPath")
                val outputDir = properties.getProperty("outputDir")

                val execFile = File(outputDir, String.format("%1\$s.exec", execFileName))
                agentString = String.format("-javaagent:%1\$s=destfile=%2\$s",
                        agentPath, execFile.absolutePath)
            } catch (e: IOException) {
                // e.printStackTrace();
            }
        }

        this.agentString = agentString
    }
}
