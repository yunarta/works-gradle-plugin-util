package com.mobilesolutionworks.gradle.tests.tasks

import com.mobilesolutionworks.gradle.GradleBaseOptions
import com.mobilesolutionworks.gradle.tasks.JacocoTestKitSetup
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class JacocoTestKitSetupEdgeTests {

    @Test(expected = TaskInstantiationException::class)
    fun `using task without jacoco plugin`() {
        with(ProjectBuilder.builder().build()) {
            project.extensions.create("worksJacoco", GradleBaseOptions::class.java)
            project.apply {
                it.plugin("java")
            }

            tasks.create("jacocoTestPreparation", JacocoTestKitSetup::class.java)
        }
    }

    @Test
    fun `using task with jacoco plugin`() {
        with(ProjectBuilder.builder().build()) {
            project.extensions.create("worksJacoco", GradleBaseOptions::class.java)
            project.apply {
                it.plugin("java")
                it.plugin("jacoco")
            }

            with(project.repositories) {
                add(jcenter())
            }
            tasks.create("jacocoTestPreparation", JacocoTestKitSetup::class.java)
        }
    }
}