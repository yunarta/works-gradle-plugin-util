package com.mobilesolutionworks.gradle.jacoco.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GradleRunnerExtensions {

    Class<GradleRunnerWrapperExtension>[] values();
}
