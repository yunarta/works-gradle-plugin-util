package com.mobilesolutionworks.gradle.jacoco.jupiter;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradleRunnerWrapper {

    public final GradleRunner runner;

    public boolean output = false;

    private List<String> arguments = new ArrayList<>();

    public GradleRunnerWrapper(GradleRunner runner) {
        this.runner = runner;
    }

    void setArgs(String... args) {
        arguments.addAll(Arrays.asList(args));
    }

    private void apply() {
        if (output) {
            runner.forwardOutput();
        }

        runner.withArguments(arguments);
    }

    public BuildResult build() {
        apply();
        return runner.withPluginClasspath().build();
    }

    public File getRoot() {
        return runner.getProjectDir();
    }
}
