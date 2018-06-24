package com.mobilesolutionworks.gradle.jacoco.testKits.tasks

import com.mobilesolutionworks.gradle.jacoco.TestKit
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationTests : TestKit("JacocoTestPreparationTests") {

    @Before
    fun clean() {
        File("jacoco.log").delete()
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = false`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                onlyRunCoverageWhenReporting = false
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)
                .withArguments("clean", "test")
                .build()
                .let {
                    Assert.assertEquals("true", File(rootDir, "jacoco.log").readLines().single())
                }
    }

    @Test

    fun `run test with onlyRunCoverageWhenReporting = true`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)
                .withArguments("clean", "test")
                .build()
                .let {
                    Assert.assertEquals("false", File(rootDir, "jacoco.log").readLines().single())
                }
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = true, and with report task`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)
                .withArguments("clean", "test", "jacocoTestReport")
                .build()
                .let {
                    Assert.assertEquals("true", File(rootDir, "jacoco.log").readLines().single())
                }
    }
}
