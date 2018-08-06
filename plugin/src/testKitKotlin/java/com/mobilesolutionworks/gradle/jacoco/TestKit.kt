package com.mobilesolutionworks.gradle.jacoco

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

open class TestKit @Throws(IOException::class)
@JvmOverloads constructor(resourcePath: String, agentFile: String = "javaagent-for-testkit.properties") {

    val rootDir: File

    init {
        val parent = Paths.get("build", "tmp", "testKit", javaClass.simpleName).toFile()
        rootDir = File(parent, resourcePath)
        rootDir.mkdirs()

        val source = File(javaClass.classLoader.getResource(resourcePath)!!.file)
        copyFolder(source, rootDir)

        val agentString = TestKitConfiguration(agentFile, javaClass.simpleName).agentString
        if (agentString != null) {
            val properties = Properties()
            properties.setProperty("org.gradle.jvmargs", agentString)
            properties.store(FileOutputStream(File(rootDir, "gradle.properties")), "Gradle")
        }
    }

    /**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     */
    @Throws(IOException::class)
    private fun copyFolder(sourceFolder: File, destinationFolder: File) {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir()
            }

            //Get all files from source directory
            val files = sourceFolder.list()

            //Iterate over all files and copy them to destinationFolder one by one
            for (file in files!!) {
                val srcFile = File(sourceFolder, file)
                val destFile = File(destinationFolder, file)

                //Recursive function call
                copyFolder(srcFile, destFile)
            }
        } else {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
