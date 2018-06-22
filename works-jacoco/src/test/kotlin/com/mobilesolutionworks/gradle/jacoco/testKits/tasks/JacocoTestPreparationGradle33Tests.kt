package com.mobilesolutionworks.gradle.jacoco.testKits.tasks

import com.mobilesolutionworks.gradle.jacoco.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationGradle33Tests : TestKitTestCase("JacocoTestKitGradle33") {

    @Before
    fun clean() {
        File("jacoco.log").delete()
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = false`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
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
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)
                .withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceNotCreated")
                .build()
                .let {
                    assertEquals("true", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = true`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
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
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root).withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceNotCreated", "--stacktrace")
                .build()
                .let {
                    assertEquals("false", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }

    @Test
    fun `run test with onlyRunCoverageWhenReporting = true, and with report task`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
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
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)
                .withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceNotCreated", "jacocoTestReport")
                .build()
                .let {
                    assertEquals("true", File(tempDir.root, "jacoco.log").readLines().single())
                }
    }
}
