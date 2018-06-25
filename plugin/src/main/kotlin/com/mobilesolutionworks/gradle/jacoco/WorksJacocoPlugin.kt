package com.mobilesolutionworks.gradle.jacoco

import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoOpenReport
import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestKitConfigureDeps
import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestKitConfigureRunner
import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestKitSetup
import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestPreparation
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.apache.commons.lang3.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.util.GradleVersion
import java.io.File

internal val Project.worksJacoco: WorksJacocoOptions
    get() {
        return extensions.getByType(WorksJacocoOptions::class.java)
    }

class WorksJacocoPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val options = project.extensions.create("worksJacoco", WorksJacocoOptions::class.java)
        options.testKitExecDir = File(project.buildDir, "jacoco").path
        options.testKitTmpDir = project.buildDir.withPaths("tmp", "testKit").path

        with(project) {
            setupClean()
        }

        project.afterEvaluate {
            with(it) {
                if (plugins.hasPlugin(JacocoPlugin::class.java)) {
                    setupDeps()
                    setupPreparationTasks()
                    setupTestKitTasks()
                    setupOpenReportTasks()
                }
            }
        }
    }

    private fun Project.setupDeps() {
        if (worksJacoco.useTestKitLib) {
            tasks.create("jacocoTestKitConfigureDeps", JacocoTestKitConfigureDeps::class.java) { task ->
                configurations.filter {
                    when {
                        GradleVersion.current() >= GradleVersion.version("3.4") ->
                            listOf("testImplementation", "testRuntimeOnly")
                        else ->
                            listOf("testCompile", "testRuntime")
                    }.contains(it.name)
                }.map {
                    dependencies.add(it.name, files(task.outputs))
                }

                // ensure creation of dependency file during project loading
                project.buildDir.withPaths("testKit", "libs").apply {
                    if (!exists()) {
                        task.extract()
                    }
                }
            }
        }
    }

    private fun Project.setupClean() {
        tasks.withType(Delete::class.java).whenObjectAdded { delete ->
            if (delete.name == "cleanTest") {
                tasks.withType(JacocoTestKitConfigureRunner::class.java).forEach {
                    delete.delete(it.outputs)
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
            it.reports.html.let { html ->
                if (html.isEnabled) {
                    tasks.create("open" + StringUtils.capitalize(it.name), JacocoOpenReport::class.java) {
                        it.setReport(html.entryPoint.absolutePath)
                    }
                }
            }
        }
    }
}