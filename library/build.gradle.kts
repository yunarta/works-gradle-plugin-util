import com.mobilesolutionworks.gradle.tasks.JacocoTestKitConfigureRunner
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("works-publish")
    id("com.mobilesolutionworks.gradle.basic")
}

group = "com.mobilesolutionworks.gradle"
version = "1.0.0-BUILD-2"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.1"
}

worksOptions {
    hasTestKit = true
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    (plugins) {
        "worksBasePlugin" {
            id = "com.mobilesolutionworks.gradle.basic"
            implementationClass = "com.mobilesolutionworks.gradle.GradleBasePlugin"
        }
    }
}

tasks.withType<Delete>().whenObjectAdded {
    if (name == "cleanTest") {
        delete(file("$buildDir/tmp/testKit"))
    }
}

tasks.withType<JacocoReport> {

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    // generated classes
    classDirectories = fileTree(mapOf(
            "dir" to "$buildDir/classes/kotlin/main")
    )

    // sources
    sourceDirectories = files(listOf("src/main/kotlin", "src/main/java"))
    executionData = fileTree(mapOf("dir" to buildDir, "include" to "jacoco/*.exec"))
}

tasks.create("automateTest") {
    group = "automation"

    dependsOn("cleanTest", "test", "jacocoTestReport")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    doFirst {
        logger.quiet("Test with max $maxParallelForks parallel forks")
    }
    finalizedBy("jacocoTestReport")
}

//tasks.withType<Test> {
//    doFirst {
//        logger.quiet("Test JVM Arguments")
//        allJvmArgs.forEach {
//            logger.quiet(" $it")
//        }
//    }
//}

tasks.createLater("openJacocoReport", Exec::class.java) {
    group = "works-jacoco"

    tasks.withType<JacocoReport> {
        val index = reports.html.entryPoint.absolutePath
        setCommandLine("open", index)
    }
}
