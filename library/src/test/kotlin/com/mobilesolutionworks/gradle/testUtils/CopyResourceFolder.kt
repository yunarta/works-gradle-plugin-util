package com.mobilesolutionworks.gradle.testUtils

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.junit.rules.TemporaryFolder
import java.io.File

class CopyResourceFolder(private val name: String, folder: File? = null, private val clearOnExit: Boolean = false) : TemporaryFolder(folder) {

    init {
        folder?.mkdirs()
    }

    override fun create() {
        super.create()
        FileUtils.copyDirectory(File(javaClass.classLoader.getResource(name).file), root)
    }

    override fun delete() {
        if (clearOnExit) {
            super.delete()
        }
    }
}