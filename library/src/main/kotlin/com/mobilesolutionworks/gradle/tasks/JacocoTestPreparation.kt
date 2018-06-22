package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.worksOptions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoBase

/**
 * Task that monitor whether coverage should be executed when jacoco report tasks is included in the queue.
 */
internal open class JacocoTestPreparation : DefaultTask() {

    init {
        group = "works-basic"
        description = "Handler to control when coverage should be executed even jacoco plugin is active"

        logger.info("""
            Jacoco Test Preparation
            -----------------------
            This task will disable jacoco if you don't have report task in Gradle execution task list
        """.trimIndent())

        with(project) {
            tasks.withType(Test::class.java).forEach { test ->
                test.shouldRunAfter(this@JacocoTestPreparation)
                val extension = test.extensions.findByType(JacocoTaskExtension::class.java)
                if (extension != null) {
                    extension.isEnabled = !project.worksOptions.onlyRunCoverageWhenReporting
                    println("disabling jacoco, isEnabled = ${isEnabled}")
                } else throw IllegalStateException("Task can only be used with presence of Jacoco plugin")
            }


            tasks.withType(JacocoBase::class.java) { jacocoReport ->
                jacocoReport.dependsOn(this@JacocoTestPreparation.name)
            }
        }
    }

    @TaskAction
    fun enableJacoco() {
        with(project) {
            tasks.withType(Test::class.java) { test ->
                test.extensions.getByType(JacocoTaskExtension::class.java).apply {
                    println("enabling jacoco")
                    isEnabled = true
                }
            }
        }
    }
}