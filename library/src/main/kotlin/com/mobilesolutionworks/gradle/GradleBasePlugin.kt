package com.mobilesolutionworks.gradle

import com.mobilesolutionworks.gradle.tasks.JacocoTestKitConfigureRunner
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitSetup
import com.mobilesolutionworks.gradle.tasks.JacocoTestPreparation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.testing.jacoco.plugins.JacocoPlugin
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
                plugins.findPlugin(JacocoPlugin::class.java)?.apply {
                    tasks.create("jacocoTestPreparation", JacocoTestPreparation::class.java)
                }

                if (worksOptions.hasTestKit) {
                    val currentTestRuntime = when {
                        GradleVersion.current() >= GradleVersion.version("3.4") -> "testRuntimeOnly"
                        else -> "testRuntime"
                    }

                    tasks.create("jacocoTestKitSetup", JacocoTestKitSetup::class.java)
                    tasks.create("jacocoTestKitConfigureRunner", JacocoTestKitConfigureRunner::class.java) { task ->
                        configurations.singleOrNull {
                            it.name == currentTestRuntime
                        }?.let { configuration ->
                            task.outputFile.parentFile.mkdirs()
                            dependencies.add(configuration.name, files(task.outputFile.parentFile))
                        }
                    }
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
}