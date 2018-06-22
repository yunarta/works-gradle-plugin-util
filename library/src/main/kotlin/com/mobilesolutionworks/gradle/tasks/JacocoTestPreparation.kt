package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.worksOptions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReportBase

/**
 * Task that monitor whether coverage should be executed when jacoco report tasks is included in the queue.
 */
open class JacocoTestPreparation : DefaultTask() {

    init {
        logger.info("""
            Jacoco Test Preparation
            -----------------------
            This task will disable jacoco if you don't have report task in Gradle execution task list
        """.trimIndent())

        with(project) {
            tasks.withType(Test::class.java).forEach { test ->
                test.shouldRunAfter(this@JacocoTestPreparation)
                test.extensions.findByType(JacocoTaskExtension::class.java)?.apply {
                    isEnabled = !project.worksOptions.onlyRunCoverageWhenReporting
                }
            }

            tasks.withType(JacocoReportBase::class.java) { jacocoReport ->
                jacocoReport.dependsOn(this@JacocoTestPreparation.name)
            }
        }
    }

    @TaskAction
    fun enableJacoco() {
        with(project) {
            tasks.withType(Test::class.java) { test ->
                test.extensions.findByType(JacocoTaskExtension::class.java)?.apply {
                    isEnabled = true
                }
            }
        }
    }
}