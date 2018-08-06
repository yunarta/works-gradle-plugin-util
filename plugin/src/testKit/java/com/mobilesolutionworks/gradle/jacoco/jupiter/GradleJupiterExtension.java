package com.mobilesolutionworks.gradle.jacoco.jupiter;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class GradleJupiterExtension implements ParameterResolver {

    private ExtensionContext.Namespace Namespace = ExtensionContext.Namespace.create(GradleJupiterExtension.class);

    class TemporaryDir implements ExtensionContext.Store.CloseableResource {

        List<File> temporaryFolders = new ArrayList<>();

        @SuppressWarnings("ResultOfMethodCallIgnored")
        protected File createTemporaryFolder() throws IOException {
            File tempDir = File.createTempFile("junit", "");
            tempDir.delete();
            tempDir.mkdir();
            temporaryFolders.add(tempDir);

            return tempDir;
        }

        @Override
        public void close() {
            for (File temporaryFolder : temporaryFolders) {
                temporaryFolder.delete();
            }
        }
    }

    class GradleConstructor extends TemporaryDir {

        GradleRunnerWrapper create() throws IOException {
            File projectDir = createTemporaryFolder();
            GradleRunner runner = GradleRunner.create().withProjectDir(projectDir);
            return new GradleRunnerWrapper(runner);
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == GradleRunnerWrapper.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        if (parameter.getType() == GradleRunnerWrapper.class) {
            ExtensionContext.Store store;
            Class<GradleConstructor> type;
            GradleRunnerWrapper wrapper;

            try {
                store = extensionContext.getStore(Namespace);
                type = GradleConstructor.class;
                wrapper = store.getOrComputeIfAbsent(type.getName(), param -> new GradleConstructor(), type).create();

                GradleRunnerExtensions annotation = parameter.getAnnotation(GradleRunnerExtensions.class);
                if (annotation != null) {
                    for (Class<GradleRunnerWrapperExtension> extensionClass : annotation.values()) {
                        GradleRunnerWrapperExtension extension = extensionClass.getDeclaredConstructor().newInstance();
                        extension.configure(wrapper);
                    }
                }
                return wrapper;
            } catch (GradleRunnerWrapperException | IOException | ReflectiveOperationException e) {
                throw new ParameterResolutionException("Unable to create GradleRunnerWrapper", e);
            }
        }

        return null;
    }
}
