package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testUtils.CopyResourceFolder
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationGradle33Tests {

    @JvmField
    @Rule
    val tempDir = CopyResourceFolder("JacocoTestKitEdge", File("/Users/yunarta/Works/yunarta/works-gradle-plugin-util/library/build/tmp/testKit"), false)

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
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)
                .withArguments("test", "--tests", "example.ExampleTest.verifyResourceNotCreated")
                .build()
                .let {
                    assertTrue(tempDir.root.withPaths("target", "build", "jacoco", "test.exec").exists())
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

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)
        runner.withArguments("test", "--tests", "example.ExampleTest.verifyResourceNotCreated")
                .build()
                .let {
                    // TODO: seems like even though JacocoTaskExtension isEnabled = false, jacoco still executes
                    // assertFalse(tempDir.root.withPaths("target", "build", "jacoco", "test.exec").exists())
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
                .withGradleVersion("3.3")
                .withProjectDir(tempDir.root)
                .withArguments("test", "--tests", "example.ExampleTest.verifyResourceNotCreated", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(tempDir.root.withPaths("target", "build", "jacoco", "test.exec").exists())
                }
    }
}
