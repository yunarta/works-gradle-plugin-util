package com.mobilesolutionworks.gradle.jacoco

import com.mobilesolutionworks.gradle.jacoco.tasks.ConfigureDeps
import org.gradle.api.artifacts.Dependency

interface WorksJacocoOptions {

    var hasTestKit: Boolean

    var testKitExecDir: String

    var testKitTmpDir: String

    var onlyRunCoverageWhenReporting: Boolean

    var agentPropertiesName: String

    var useTestKitLib: Boolean

    fun testKitLib(): Dependency

    fun testKitLibKotlin(): Dependency
}

open class WorksJacocoOptionsImpl(val testKibLib: ConfigureDeps, val testKibLibKotlin: ConfigureDeps) : WorksJacocoOptions {

    override var hasTestKit = false

    override var testKitExecDir = ""

    override var testKitTmpDir = ""

    override var onlyRunCoverageWhenReporting = false

    override var agentPropertiesName = "javaagent-for-testkit.properties"

    override var useTestKitLib = true

    override fun testKitLib(): Dependency {
//        testKibLib.extract()
        return testKibLib.project.dependencies.create(testKibLib.outputs.files)
    }

    override fun testKitLibKotlin(): Dependency {
//        testKibLibKotlin.extract()
        return testKibLibKotlin.project.dependencies.create(testKibLibKotlin.outputs.files)
    }
}
