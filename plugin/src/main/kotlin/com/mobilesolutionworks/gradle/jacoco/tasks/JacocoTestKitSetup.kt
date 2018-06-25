package com.mobilesolutionworks.gradle.jacoco.tasks

import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.api.tasks.Copy
import java.nio.file.Paths

internal open class JacocoTestKitSetup : Copy() {

    val agentPath = project.buildDir.withPaths("testKit", "jacocoAgent", "jacocoagent.jar")

    init {
        group = "works-others"
        description = "Extract the jacocoagent for testKit coverage"

        with(project) {
            val configuration = project.configurations.findByName("jacocoAgent")
            if (configuration != null) {
                from(zipTree(configuration.asPath))
                into(file(Paths.get(buildDir.name, "testKit", "jacocoAgent").toFile()))
            } else throw IllegalStateException("Task can only be used with presence of Jacoco plugin")
        }
    }
}