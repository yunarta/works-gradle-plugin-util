package com.mobilesolutionworks.gradle.jacoco.jupiter

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.File
import kotlin.reflect.full.createInstance


class GradleJupiterExtension : ParameterResolver {

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return when (parameterContext.parameter.type) {
            GradleRunnerWrapper::class.java -> {
                true
            }

            else -> false
        }
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? {
        val parameter = parameterContext.parameter
        return when (parameter.type) {
            GradleRunnerWrapper::class.java -> {
                val store = extensionContext.getStore(Namespace)
                val type = GradleConstructor::class.java
                store.getOrComputeIfAbsent(type.name, {
                    GradleConstructor()
                }, type).create().also { wrapper ->
                    parameter.getAnnotation(GradleRunnerExtensions::class.java)?.let { options ->
                        options.value.forEach {
                            it.createInstance().configure(wrapper)
                        }
                    }
                }
            }

            else -> null
        }
    }

    companion object {

        private val Namespace: ExtensionContext.Namespace = ExtensionContext.Namespace.create(GradleJupiterExtension::class.java)

        private open class TemporaryDir : ExtensionContext.Store.CloseableResource {

            private val temporaryFolders = mutableListOf<File>()

            fun createTemporaryFolder(): File =
                    File.createTempFile("junit", "").apply {
                        temporaryFolders.add(this)
                        delete()
                        mkdir()
                    }

            override fun close() {
                temporaryFolders.forEach {
                    it.deleteRecursively()
                }
            }
        }

        private class GradleConstructor : TemporaryDir() {

            fun create() = createTemporaryFolder().let {
                val runner = GradleRunner.create()
                        .withProjectDir(it)
                GradleRunnerWrapper(runner)
            }
        }
    }
}