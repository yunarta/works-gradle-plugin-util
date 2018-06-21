package com.mobilesolutionworks.gradle.tasks

import org.gradle.api.tasks.Copy
import java.nio.file.Paths

class JacocoTestKitSetup : Copy() {

    init {
        with(project) {
            project.configurations.findByName("jacocoAgent")?.let {
                from(it.asPath)
                into(file(Paths.get(buildDir.name, "testKit", "jacocoAgent").toFile()))
            }
        }
    }
}