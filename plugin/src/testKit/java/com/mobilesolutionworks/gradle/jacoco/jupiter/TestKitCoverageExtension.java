package com.mobilesolutionworks.gradle.jacoco.jupiter;

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TestKitCoverageExtension implements GradleRunnerWrapperExtension {

    @Override
    public void configure(GradleRunnerWrapper wrapper) throws GradleRunnerWrapperException {
        try {
            File file = new File(wrapper.getRoot(), "gradle.properties");
            file.createNewFile();

            String jacoco = new TestKitConfiguration("jacoco").agentString;
            Properties properties = new Properties();
            properties.setProperty("org.gradle.jvmargs", jacoco);

            FileOutputStream out = new FileOutputStream(file);
            properties.store(out, "Gradle");
            out.close();
        } catch (IOException e) {
            GradleRunnerWrapperException exception = new GradleRunnerWrapperException();
            exception.initCause(e);

            throw exception;
        }
    }
}
