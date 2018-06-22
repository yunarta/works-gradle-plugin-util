package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.util.withPaths
import com.mobilesolutionworks.gradle.worksOptions
import org.gradle.api.tasks.WriteProperties
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoBase
import org.gradle.util.GradleVersion

internal open class JacocoTestKitConfigureRunner : WriteProperties() {

    init {
        group = "works-basic"
        description = "Prepare javaagent-for-testkit.properties in build/testKit/gradle for testKit coverage"

        with(project) {
            fun target() = when (worksOptions.onlyRunCoverageWhenReporting) {
                true -> JacocoBase::class.java
                else -> Test::class.java
            }

            tasks.withType(target()).forEach { it ->
                it.dependsOn(this@JacocoTestKitConfigureRunner)
            }
            tasks.withType(Test::class.java).forEach { it ->
                it.shouldRunAfter(this@JacocoTestKitConfigureRunner)
            }

            tasks.withType(JacocoTestKitSetup::class.java).forEach { setup ->
                dependsOn(setup)
                setProperties(mapOf(
                        "agentPath" to setup.agentPath.absolutePath,
                        "outputDir" to file(worksOptions.testKitExecDir).absolutePath
                ))
            }

        }

        setOutput()
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun setOutput() {
        val file = project.buildDir.withPaths("testKit", "gradle", "javaagent-for-testkit.properties")
        if (GradleVersion.current() >= GradleVersion.version("3.4")) {
            outputFile = file
        } else {
            setOutputFile(file.absolutePath)
        }
    }
}