package com.mobilesolutionworks.gradle.testKits.plugin

import com.mobilesolutionworks.gradle.testKits.TestKitTestCase
import com.mobilesolutionworks.gradle.util.withPaths
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertFalse
import org.junit.Test

internal class GradleBasePluginTest : TestKitTestCase("PluginTests") {

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

        runner.withArguments("test", "cleanTest")
                .build()
                .let {
                    assertFalse(tempDir.root.withPaths("target", "build", "testKit", "gradle", "javaagent-for-testkit.properties").exists())
                }
    }
}
