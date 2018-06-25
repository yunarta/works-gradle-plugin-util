package com.mobilesolutionworks.gradle.jacoco.testKits.tasks

import com.mobilesolutionworks.gradle.jacoco.TestKit
import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.*
import org.junit.Test

internal class JacocoTestKitSetupTests : TestKit("JacocoTestKitSetup") {

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
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    rootDir.withPaths(
                            "target",
                            "build", "testKit", "jacocoAgent",
                            "jacocoagent.jar"
                    ).let {
                        assertTrue(it.exists())
                    }
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
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertNull(it.task(":target:jacocoTestKitSetup"))
                    rootDir.withPaths(
                            "target",
                            "build", "testKit", "jacocoAgent",
                            "jacocoagent.jar"
                    ).let {
                        assertFalse(it.exists())
                    }
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
        runner.withArguments("clean", "test", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    rootDir.withPaths(
                            "target",
                            "build", "testKit", "jacocoAgent",
                            "jacocoagent.jar"
                    ).let {
                        assertTrue(it.exists())
                    }
                }
    }


    @Test
    fun `verify extraction works incrementally`() {
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
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    rootDir.withPaths(
                            "target",
                            "build", "testKit", "jacocoAgent",
                            "jacocoagent.jar"
                    ).let {
                        assertTrue(it.exists())
                    }
                }
        runner.withArguments("test")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.UP_TO_DATE)
                }
    }
}
