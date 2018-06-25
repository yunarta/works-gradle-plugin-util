package com.mobilesolutionworks.gradle.jacoco;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestKitConfiguration {

    public final String agentString;

    public TestKitConfiguration(String execFile) {
        this("javaagent-for-testkit.properties", execFile);
    }

    public TestKitConfiguration(String agentFile, String execFileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(agentFile);

        String agentString = null;
        if (stream != null) {
            try {
                Properties properties = new Properties();
                properties.load(stream);

                String agentPath = properties.getProperty("agentPath");
                String outputDir = properties.getProperty("outputDir");

                File execFile = new File(outputDir, String.format("%1$s.exec", execFileName));
                agentString = String.format("-javaagent:%1$s=destfile=%2$s",
                        agentPath, execFile.getAbsolutePath());
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        this.agentString = agentString;
    }
}
