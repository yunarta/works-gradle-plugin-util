package com.mobilesolutionworks.gradle.jacoco.jupiter

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class GradleRunnerExtensions(
        vararg val value: KClass<out GradleRunnerWrapperExtension>
)