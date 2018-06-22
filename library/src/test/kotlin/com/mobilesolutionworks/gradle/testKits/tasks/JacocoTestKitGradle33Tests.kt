package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.internal.impldep.org.junit.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertTrue

internal class JacocoTestKitGradle33Tests : TestKitTestCase("JacocoTestKitEdge") {

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
                .withPluginClasspath()
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)

        runner.withArguments("test", "--tests", "example.ExampleTest.verifyResourceExists", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitConfigureRunner")?.outcome == TaskOutcome.SUCCESS)
                }
    }
}
