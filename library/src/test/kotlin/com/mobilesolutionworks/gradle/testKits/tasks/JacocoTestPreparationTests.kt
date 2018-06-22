package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationTests : TestKitTestCase("JacocoTestPreparationTests") {

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
                    assertFalse(File(tempDir.root, "jvmargs.log")
                            .readLines()
                            .singleOrNull { it.startsWith("-javaagent") }
                            .isNullOrBlank()
                    )
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
                    assertTrue(File(tempDir.root, "jvmargs.log")
                            .readLines()
                            .singleOrNull { it.startsWith("-javaagent") }
                            .isNullOrBlank()
                    )
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
                .withArguments("test", "jacocoTestReport")
                .build()
                .let {
                    assertFalse(File(tempDir.root, "jvmargs.log")
                            .readLines()
                            .singleOrNull { it.startsWith("-javaagent") }
                            .isNullOrBlank()
                    )
                }
    }
}
