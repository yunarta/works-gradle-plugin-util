package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test

internal class JacocoTestKitConfigureRunnerGradle33Tests : TestKitTestCase("JacocoTestKitGradle33") {

    @Test
    fun `test with onlyRunCoverageWhenReporting = false`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                hasTestKit = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withGradleVersion("3.3")
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceExists")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }
    }

    @Test
    fun `test with onlyRunCoverageWhenReporting = true`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                hasTestKit = true
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withGradleVersion("3.3")
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceNotCreated")
                .build()
                .let {
                    Assert.assertNull(it.task(":target:jacocoTestKitConfigureRunner"))
                }
    }

    @Test
    fun `test with onlyRunCoverageWhenReporting = true, and with report task`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksOptions {
                hasTestKit = true
                onlyRunCoverageWhenReporting = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withGradleVersion("3.3")
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("clean", "test", "--tests", "example.ExampleTest.verifyResourceExists", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }
    }
}
