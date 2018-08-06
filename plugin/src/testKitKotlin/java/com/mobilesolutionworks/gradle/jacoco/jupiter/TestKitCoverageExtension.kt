package com.mobilesolutionworks.gradle.jacoco.jupiter

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration
import java.io.File
import java.util.*


class TestKitCoverageExtension : GradleRunnerWrapperExtension {

    override fun configure(wrapper: GradleRunnerWrapper) {
        File(wrapper.root, "gradle.properties").apply {
            createNewFile()
            TestKitConfiguration("jacoco").agentString?.let {
                val properties = Properties()
                properties.setProperty("org.gradle.jvmargs", it)
                properties.store(outputStream(), "Gradle")
            }
        }
    }
}