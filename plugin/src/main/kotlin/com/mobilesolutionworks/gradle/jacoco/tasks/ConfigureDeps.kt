package com.mobilesolutionworks.gradle.jacoco.tasks

import org.gradle.api.DefaultTask

abstract class ConfigureDeps: DefaultTask() {

     abstract fun extract()
}