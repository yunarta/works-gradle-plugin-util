package com.mobilesolutionworks.gradle.jacoco.testKits.plugin

import com.mobilesolutionworks.gradle.jacoco.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.*
import org.junit.Test

internal class WorksJacocoPluginTest : TestKitTestCase("PluginTests") {

    @Test
    fun `test with without jacoco`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
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
            worksJacoco {
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
            worksJacoco {
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

    @Test
    fun `test open jacoco report`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
            }

            tasks.withType(JacocoReport) {
                reports {
                    html.enabled = true
                }
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments(":target:tasks")
                .build()
                .let {
                    assertTrue(it.output.contains("openJacocoTestReport"))
                }
    }

    @Test
    fun `test open jacoco report but html disabled`() {
        tempDir.root.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
            }

            tasks.withType(JacocoReport) {
                reports {
                    html.enabled = false
                }
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(tempDir.root)

        runner.withArguments(":target:tasks")
                .build()
                .let {
                    assertFalse(it.output.contains("openJacocoTestReport"))
                }
    }

}
