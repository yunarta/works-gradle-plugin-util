package com.mobilesolutionworks.gradle.jacoco.tests

import com.mobilesolutionworks.gradle.jacoco.util.withPaths
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FileExtensionsTests {

    @Test
    fun `test path creation using withPaths extension`() {
        with(ProjectBuilder.builder().build()) {
            rootDir.withPaths("build", "tmp").let {
                assertEquals(files("${buildDir}/tmp").singleFile, files(it).singleFile)
            }
        }
    }
}

