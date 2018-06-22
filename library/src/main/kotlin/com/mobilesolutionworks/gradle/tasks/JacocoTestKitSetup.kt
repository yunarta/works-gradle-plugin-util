package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.api.tasks.Copy
import java.nio.file.Paths

open class JacocoTestKitSetup : Copy() {

    var agentPath = project.buildDir.withPaths("testKit", "jacocoAgent", "jacocoagent.jar")

    init {
        with(project) {
//            if (worksOptions.onlyRunCoverageWhenReporting) {
//                tasks.withType(JacocoTestPreparation::class.java).forEach { it ->
//                    shouldRunAfter(it)
//                }
//
//                tasks.withType(JacocoReportBase::class.java).forEach { it ->
//                    dependsOn(it)
//                }
//            } else {
//                tasks.withType(Test::class.java).forEach { test ->
//                    test.dependsOn(this@JacocoTestKitSetup)
//                    test.shouldRunAfter(this@JacocoTestKitSetup)
//                }
//            }

            project.configurations.findByName("jacocoAgent")?.let {
                from(zipTree(it.asPath))
                into(file(Paths.get(buildDir.name, "testKit", "jacocoAgent").toFile()))
            }
        }
    }
}