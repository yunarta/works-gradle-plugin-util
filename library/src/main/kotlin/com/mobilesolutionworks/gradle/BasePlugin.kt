package com.mobilesolutionworks.gradle

import com.mobilesolutionworks.gradle.tasks.JacocoTestPreparation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin

class BasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            with(it) {
                convention.findPlugin(JacocoPlugin::class.java)?.apply {
                    tasks.create("jacocoTestPreparation", JacocoTestPreparation::class.java)
                }
            }
        }
    }
}