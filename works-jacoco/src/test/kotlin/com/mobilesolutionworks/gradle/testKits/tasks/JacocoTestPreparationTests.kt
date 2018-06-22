package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationTests : TestKitTestCase("JacocoTestPreparationTests") {

    @Before
    fun clean() {
        File("jacoco.log").delete()
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = false`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                onlyRunCoverageWhenReporting = false
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)
                .withArguments("test")
                .build()
                .let {
                    Assert.assertEquals("true", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }

    @Test

    fun `run test with onlyRunCoverageWhenReporting = true`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)
                .withArguments("test")
                .build()
                .let {
                    Assert.assertEquals("false", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = true, and with report task`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)
                .withArguments("test", "jacocoTestReport", "openJacocoTestReport")
                .build()
                .let {
                    Assert.assertEquals("true", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }
}
