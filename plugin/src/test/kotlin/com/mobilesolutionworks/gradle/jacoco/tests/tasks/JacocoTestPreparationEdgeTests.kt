package com.mobilesolutionworks.gradle.jacoco.tests.tasks

import com.mobilesolutionworks.gradle.jacoco.WorksJacocoOptions
import com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestPreparation
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class JacocoTestPreparationEdgeTests {

    @Test(expected = TaskInstantiationException::class)
    fun `using task without jacoco plugin`() {
        with(ProjectBuilder.builder().build()) {
            project.extensions.create("worksJacoco", WorksJacocoOptions::class.java)
            project.apply {
                it.plugin("java")
            }

            tasks.create("jacocoTestPreparation", JacocoTestPreparation::class.java)
        }
    }

    @Test
    fun `using task with jacoco plugin`() {
        with(ProjectBuilder.builder().build()) {
            project.extensions.create("worksJacoco", WorksJacocoOptions::class.java)
            project.apply {
                it.plugin("java")
                it.plugin("jacoco")
            }

            tasks.create("jacocoTestPreparation", JacocoTestPreparation::class.java)
        }
    }
}