package com.mobilesolutionworks.gradle.jacoco.testKits.tasks

import com.mobilesolutionworks.gradle.jacoco.TestKit
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class JacocoTestKitConfigureRunnerTests : TestKit("JacocoTestKitConfigureRunner") {

    @Test
    fun `test with onlyRunCoverageWhenReporting = false`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                hasTestKit = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceExists", "--stacktrace")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }
    }

    @Test
    fun `test with onlyRunCoverageWhenReporting = true`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                hasTestKit = true
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceNotCreated")
                .build()
                .let {
                    assertNull(it.task(":target:jacocoTestKitConfigureRunner"))
                }
    }

    @Test
    fun `test with onlyRunCoverageWhenReporting = true, and with report task`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                hasTestKit = true
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceExists", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }
    }

    @Test
    fun `verify configure works incrementally`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                hasTestKit = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "jacocoTestKitConfigureRunner")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }

        runner.withArguments("jacocoTestKitConfigureRunner")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.UP_TO_DATE)
                }
    }
}
