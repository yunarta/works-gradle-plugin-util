package com.mobilesolutionworks.gradle.jacoco.testKits.plugin

import com.mobilesolutionworks.gradle.jacoco.TestKit
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.*
import org.junit.Test

internal class WorksJacocoPluginTest : TestKit("PluginTests") {

    @Test
    fun `test with without jacoco`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""
            worksJacoco {
                hasTestKit = true
                agentPropertiesName = "agent.properties"
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "cleanTest", "cleanBuild", "test")
                .build()
                .let {
                    assertEquals(false, rootDir.withPaths("target", "build", "testKit", "gradle", "agent.properties").exists())
                }
    }

    @Test
    fun `test with jacoco`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
                agentPropertiesName = "agent.properties"
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "cleanTest", "test", "--stacktrace")
                .build()
                .let {
                    assertEquals(true, rootDir.withPaths("target", "build", "testKit", "gradle", "agent.properties").exists())
                }
    }

    @Test
    fun `test with jacoco with different output`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
                agentPropertiesName = "agent.properties"
                testKitExecDir = "${'$'}buildDir/customJacoco"
            }
        """.trimMargin())
        }

        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(rootDir)

        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertEquals(true, rootDir.withPaths("target", "build", "testKit", "gradle", "agent.properties").readLines()
                            .map { it.contains("customJacoco") }.reduce { a, b -> a || b })
                }
    }

    @Test
    fun `test open jacoco report`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
                agentPropertiesName = "agent.properties"
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
                .withProjectDir(rootDir)

        runner.withArguments("clean", ":target:tasks")
                .build()
                .let {
                    assertTrue(it.output.contains("openJacocoTestReport"))
                }
    }

    @Test
    fun `test open jacoco report but html disabled`() {
        rootDir.withPaths("target", "build.gradle").apply {
            appendText("")
            appendText("""

            apply plugin: "jacoco"
            worksJacoco {
                hasTestKit = true
                agentPropertiesName = "agent.properties"
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
                .withProjectDir(rootDir)

        runner.withArguments("clean", ":target:tasks")
                .build()
                .let {
                    assertFalse(it.output.contains("openJacocoTestReport"))
                }
    }
}
