package com.mobilesolutionworks.gradle.testKits.tasks

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.*
import org.junit.Test

internal class JacocoTestKitSetupTests : TestKitTestCase("JacocoTestKitSetup") {

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
                .withPluginClasspath()
                .withProjectDir(tempDir.root)
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    tempDir.root.withPaths(
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
                .withProjectDir(tempDir.root)
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertNull(it.task(":target:jacocoTestKitSetup"))
                    tempDir.root.withPaths(
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
                .withProjectDir(tempDir.root)
        runner.withArguments("clean", "test", "jacocoTestReport")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    tempDir.root.withPaths(
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
        runner.withArguments("clean", "test")
                .build()
                .let {
                    assertTrue(it.task(":target:jacocoTestKitSetup")?.outcome == TaskOutcome.SUCCESS)
                    tempDir.root.withPaths(
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
