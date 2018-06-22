package com.mobilesolutionworks.gradle.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

class JacocoOpenReport(private val report: String) : Exec() {

    private val executionCommands = mapOf(
            Os.FAMILY_WINDOWS to listOf("cmd", "/c", "start"),
            Os.FAMILY_MAC to listOf("open")
    )

    @TaskAction
    fun open() {
        listOf(Os.FAMILY_WINDOWS, Os.FAMILY_MAC).filter {
            Os.isFamily(it)
        }.mapNotNull {
            executionCommands[it]
        }.map {
            it.toMutableList()
        }.map {
            it.add(report)
        }.let {
            commandLine = it
        }
    }
}