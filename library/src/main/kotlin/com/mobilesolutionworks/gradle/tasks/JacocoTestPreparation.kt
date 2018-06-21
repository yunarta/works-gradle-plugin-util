package com.mobilesolutionworks.gradle.tasks

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
        with(project) {
            tasks.withType(Test::class.java).forEach {
                it.shouldRunAfter(this@JacocoTestPreparation)
                it.extensions.findByType(JacocoTaskExtension::class.java)?.apply {
                    isEnabled = false
                }
            }

            tasks.withType(JacocoReportBase::class.java) {
                it.dependsOn(this@JacocoTestPreparation.name)
            }
        }
    }

    @TaskAction
    fun enableJacoco() {
        with(project) {
            tasks.withType(Test::class.java) {
                it.extensions.findByType(JacocoTaskExtension::class.java)?.apply {
                    isEnabled = true
                }
            }
        }
    }
}
