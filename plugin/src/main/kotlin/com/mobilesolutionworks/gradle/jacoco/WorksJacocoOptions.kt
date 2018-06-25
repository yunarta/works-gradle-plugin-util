package com.mobilesolutionworks.gradle.jacoco

open class WorksJacocoOptions {

    var hasTestKit = false

    var testKitExecDir = ""

    var testKitTmpDir = ""

    var onlyRunCoverageWhenReporting = false

    var agentPropertiesName = "javaagent-for-testkit.properties"

    var useTestKitLib = true
}
