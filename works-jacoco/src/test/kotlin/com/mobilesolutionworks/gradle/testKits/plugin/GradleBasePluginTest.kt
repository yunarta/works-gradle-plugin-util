package com.mobilesolutionworks.gradle.testKits.plugin

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GradleBasePluginTest : TestKitTestCase("PluginTests") {

    @Test
    fun `test with without jacoco`() {
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
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("test", "cleanTest")
                .build()
                .let {
                    assertFalse(tempDir.root.withPaths("target", "build", "testKit", "gradle", "javaagent-for-testkit.properties").exists())
                }
    }

    @Test
    fun `test with jacoco`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksOptions {
                hasTestKit = true
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("test", "cleanTest")
                .build()
                .let {
                    assertFalse(tempDir.root.withPaths("target", "build", "testKit", "gradle", "javaagent-for-testkit.properties").exists())
                }
    }

    @Test
    fun `test with jacoco with different output`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksOptions {
                hasTestKit = true
                testKitExecDir = "${'$'}buildDir/customJacoco"
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments("test")
                .build()
                .let {
                    assertTrue(tempDir.root.withPaths("target", "build", "testKit", "gradle", "javaagent-for-testkit.properties").readLines()
                            .map { it.contains("customJacoco") }.reduce { a, b -> a || b })
                }
    }
}
