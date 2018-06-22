package com.mobilesolutionworks.gradle.tests.tasks

import com.mobilesolutionworks.gradle.tasks.JacocoOpenReport
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class JacocoOpenReportTests {

    @Test
    fun `task creation`() {
        with(ProjectBuilder.builder().build()) {
            val create = tasks.create("openReport", JacocoOpenReport::class.java) {
                it.setReport("index.html")
            }

            val executionCommands = mapOf(
                    Os.FAMILY_WINDOWS to listOf("cmd", "/c", "start"),
                    Os.FAMILY_MAC to listOf("open")
            )

            listOf(Os.FAMILY_WINDOWS, Os.FAMILY_MAC).filter {
                println("$it = ${Os.isFamily(it)}")
                Os.isFamily(it)
            }.mapNotNull {
                executionCommands[it]
            }.map {
                it.toMutableList<Any?>()
            }.map {
                create.commandLine == it
            }
        }
    }
}