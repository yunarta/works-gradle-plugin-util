package com.mobilesolutionworks.gradle.jacoco.testUtils

import org.gradle.api.Project

fun Project.createWorkJacoco() {
    val deps = project.tasks.create("jacocoTestKitConfigureDeps", com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestKitConfigureDeps::class.java)
    val kotlinDeps = project.tasks.create("jacocoTestKitKotlinConfigureDeps", com.mobilesolutionworks.gradle.jacoco.tasks.JacocoTestKitKotlinConfigureDeps::class.java)
    val options = com.mobilesolutionworks.gradle.jacoco.WorksJacocoOptionsImpl(deps, kotlinDeps)

    project.extensions.add("worksJacoco", options)
}