package com.mobilesolutionworks.gradle.jacoco;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class TestKit {

    public final File rootDir;

    public TestKit(String resourcePath) throws IOException {
        this(resourcePath, "javaagent-for-testkit.properties");
    }

    public TestKit(String resourcePath, String agentFile) throws IOException {
        File parent = Paths.get("build", "tmp", "testKit", getClass().getSimpleName()).toFile();
        rootDir = new File(parent, resourcePath);
        rootDir.mkdirs();

        File source = new File(getClass().getClassLoader().getResource(resourcePath).getFile());
        copyFolder(source, rootDir);

        String agentString = new TestKitConfiguration(agentFile, getClass().getSimpleName()).agentString;
        if (agentString != null) {
            Properties properties = new Properties();
            properties.setProperty("org.gradle.jvmargs", agentString);
            properties.setProperty("org.gradle.daemon", "false");
            properties.setProperty("org.gradle.parallel", "false");
            properties.setProperty("org.gradle.daemon.idletimeout", "1000");
            properties.setProperty("org.gradle.workers.max", "1");

            properties.store(new FileOutputStream(new File(rootDir, "gradle.properties")), "Gradle");
        }
    }

    /**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     */
    private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }

            //Get all files from source directory
            String files[] = sourceFolder.list();

            //Iterate over all files and copy them to destinationFolder one by one
            for (String file: files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        } else {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
