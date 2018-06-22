package com.mobilesolutionworks.gradle

import org.gradle.api.Project
import java.io.File

open class GradleBaseOptions(project: Project) {

    var hasTestKit = false

    var testKitExecDir = File(project.buildDir, "jacoco").path

    var onlyRunCoverageWhenReporting = false
}
