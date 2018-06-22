package com.mobilesolutionworks.gradle

import com.mobilesolutionworks.gradle.tasks.JacocoOpenReport
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitConfigureRunner
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitSetup
import com.mobilesolutionworks.gradle.tasks.JacocoTestPreparation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.util.GradleVersion

internal val Project.worksOptions: GradleBaseOptions
    get() {
        return extensions.getByType(GradleBaseOptions::class.java)
    }

class GradleBasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("worksOptions", GradleBaseOptions::class.java, project)
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
        if (worksOptions.hasTestKit) {
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
            it.reports.html.let { html ->
                tasks.create("open" + StringUtils.capitalize(it.name), JacocoOpenReport::class.java,
                        html.entryPoint.absolutePath)
            }
        }
    }
}