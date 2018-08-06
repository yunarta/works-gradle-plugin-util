package com.mobilesolutionworks.gradle.jacoco.jupiter

import java.io.File

class FileEditor(private val file: File) {

    operator fun String.not() {
        file.createNewFile()
        file.writeText(this)
    }

    operator fun String.unaryPlus() {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.appendText(this)
    }
}

fun File.readTextFrom(name: String) = File(this, name).readText()

fun File.getFile(name: String) = File(this, name)

fun File.editor(name: String, closure: FileEditor.() -> Unit) {
    mkdirs()
    FileEditor(File(this, name)).closure()
}