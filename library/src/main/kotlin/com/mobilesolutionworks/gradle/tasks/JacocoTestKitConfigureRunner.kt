package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.util.withPaths
import com.mobilesolutionworks.gradle.worksOptions
import org.gradle.api.tasks.WriteProperties
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReportBase

open class JacocoTestKitConfigureRunner : WriteProperties() {

    init {
        with(project) {
            fun target() = when (worksOptions.onlyRunCoverageWhenReporting) {
                true -> JacocoReportBase::class.java
                false -> Test::class.java
            }

            tasks.withType(target()).forEach { it ->
                it.dependsOn(this@JacocoTestKitConfigureRunner)
            }
            tasks.withType(Test::class.java).forEach { it ->
                it.shouldRunAfter(this@JacocoTestKitConfigureRunner)
            }

            tasks.withType(JacocoTestKitSetup::class.java).forEach { setup ->
                dependsOn(setup)

                property("agentPath", setup.agentPath.absolutePath)
            }

            property("outputDir", file(worksOptions.testKitExecDir).absolutePath)
        }

        outputFile = project.buildDir.withPaths("testKit", "gradle", "javaagent-for-testkit.properties")
    }
}