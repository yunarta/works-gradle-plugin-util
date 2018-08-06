package com.mobilesolutionworks.gradle.jacoco.jupiter

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class GradleRunnerWrapper(internal val runner: GradleRunner) {

    val root: File
        get() = runner.projectDir

    var output = false

    private var arguments = mutableListOf<String>()

    init {
    }

    fun args(vararg args: String) {
        arguments.addAll(args)
    }

    fun configure(closure: GradleRunnerWrapper.() -> Unit): GradleRunnerWrapper {
        closure()
        return this
    }

    private fun apply() {
        if (output) {
            runner.forwardOutput()
        }

        runner.withArguments(arguments)
    }

    fun build(closure: BuildResult.() -> Unit) {
        apply()
        runner.withPluginClasspath().build().closure()
    }
}
