package com.mobilesolutionworks.gradle.test.utils

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.junit.rules.TemporaryFolder
import java.io.File

class CopyResourceFolder(private val name: String, folder: File? = null) : TemporaryFolder(folder) {

    override fun before() {
        super.before()
        FileUtils.copyDirectory(File(javaClass.classLoader.getResource(name).file), root)
    }
}