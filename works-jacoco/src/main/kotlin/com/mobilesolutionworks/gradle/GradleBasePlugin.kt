package com.mobilesolutionworks.gradle

import com.mobilesolutionworks.gradle.tasks.JacocoOpenReport
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitConfigureRunner
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitSetup
import com.mobilesolutionworks.gradle.tasks.JacocoTestPreparation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.apache.commons.lang3.StringUtils
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.util.GradleVersion
import java.io.File

internal val Project.worksJacoco: GradleBaseOptions
    get() {
        return extensions.getByType(GradleBaseOptions::class.java)
    }

class GradleBasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val options = project.extensions.create("worksJacoco", GradleBaseOptions::class.java)
        options.testKitExecDir = File(project.buildDir, "jacoco").path

        project.afterEvaluate {
            with(it) {
                if (plugins.hasPlugin(JacocoPlugin::class.java)) {
                    setupPreparationTasks()
                    setupTestKitTasks()
                    setupOpenReportTasks()
                }

                tasks.withType(Delete::class.java).whenObjectAdded { delete ->
                    if (delete.name == "cleanTest") {
                        tasks.withType(JacocoTestKitConfigureRunner::class.java).forEach {
                            delete.delete(it.outputs)
                        }
                    }
                }
            }
        }
    }

    private fun Project.setupPreparationTasks() {
        tasks.create("jacocoTestPreparation", JacocoTestPreparation::class.java)
    }

    private fun Project.setupTestKitTasks() {
        if (worksJacoco.hasTestKit) {
            tasks.create("jacocoTestKitSetup", JacocoTestKitSetup::class.java)
            tasks.create("jacocoTestKitConfigureRunner", JacocoTestKitConfigureRunner::class.java) { task ->
                configurations.filter {
                    it.name == when {
                        GradleVersion.current() >= GradleVersion.version("3.4") -> "testRuntimeOnly"
                        else -> "testRuntime"
                    }
                }.map {
                    task.outputFile.parentFile.mkdirs()
                    dependencies.add(it.name, files(task.outputFile.parentFile))
                }
            }
        }
    }

    private fun Project.setupOpenReportTasks() {
        tasks.withType(JacocoReport::class.java).forEach {
            println("found ${it.name} ${it.reports.html.isEnabled}")
            it.reports.html.let { html ->
                if (html.isEnabled) {
                    println(html.entryPoint.absolutePath)
                    tasks.create("open" + StringUtils.capitalize(it.name), JacocoOpenReport::class.java) {
                        it.setReport(html.entryPoint.absolutePath)
                    }
                }
            }
        }
    }
}