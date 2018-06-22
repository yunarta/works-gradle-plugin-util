package com.mobilesolutionworks.gradle.jacoco.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.Exec
import javax.inject.Inject

open class JacocoOpenReport @Inject constructor() : Exec() {

    private val executionCommands = mapOf(
            Os.FAMILY_WINDOWS to listOf("cmd", "/c", "start"),
            Os.FAMILY_MAC to listOf("open")
    )

    init {
        group = "works-jacoco"
        description = "Open jacoco report"
    }

    fun setReport(report: String) {
        listOf(Os.FAMILY_WINDOWS, Os.FAMILY_MAC).filter {
            Os.isFamily(it)
        }.map {
            // default should not be returned as we only select from existing keys
            executionCommands.getOrDefault(it, listOf())
        }.map {
            it.toMutableList<Any?>()
        }.last().let {
            it.add(report)
            commandLine(it)
//            commandLine = it as List<*>?
        }
    }
}