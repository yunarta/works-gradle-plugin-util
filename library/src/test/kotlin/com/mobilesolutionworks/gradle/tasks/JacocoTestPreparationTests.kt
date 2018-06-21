package com.mobilesolutionworks.gradle.tasks

import com.mobilesolutionworks.gradle.test.utils.CopyResourceFolder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

internal class JacocoTestPreparationTests {

    @JvmField
    @Rule
    val tempDir = CopyResourceFolder("JacocoTestPreparationTests")

    @Test
    fun `run test without coverage`() {
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
    fun `run test with coverage`() {
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
