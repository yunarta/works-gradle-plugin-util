package com.mobilesolutionworks.gradle.jacoco.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.Exec
import javax.inject.Inject

open class JacocoOpenReport @Inject constructor() : Exec() {

    companion object {
        private val executionCommands = mapOf(
                Os.FAMILY_WINDOWS to listOf("cmd", "/c", "start"),
                Os.FAMILY_MAC to listOf("open"),
                Os.FAMILY_UNIX to listOf("xdg-open")
        )

        val commands: List<List<String>>
            get() {
                return listOf(Os.FAMILY_WINDOWS, Os.FAMILY_MAC, Os.FAMILY_UNIX).filter {
                    Os.isFamily(it)
                }.map {
                    // default should not be returned as we only select from existing keys
                    executionCommands.getOrDefault(it, listOf())
                }
            }
    }

    init {
        group = "works-jacoco"
        description = "Open jacoco report"
    }

    fun setReport(commands: List<List<String>>, report: String) {
        commands.lastOrNull()?.let {
            commandLine(it.toMutableList().apply {
                add(report)
            })
        }
    }
}