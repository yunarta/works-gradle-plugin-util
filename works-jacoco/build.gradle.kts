import com.mobilesolutionworks.gradle.tasks.JacocoTestKitConfigureRunner
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

//    id("works-publish")
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    reports {
        html.isEnabled = false
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.apache.commons:commons-lang3:3.7")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    (plugins) {
        "works-jacoco" {
            id = "com.mobilesolutionworks.gradle.jacoco"
            implementationClass = "com.mobilesolutionworks.gradle.jacoco.WorksJacocoPlugin"
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

tasks.withType<Test> {
    reports {
        html.isEnabled = false
    }

    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    doFirst {
        logger.quiet("Test with max $maxParallelForks parallel forks")
    }
    finalizedBy("jacocoTestReport")
}

tasks.createLater("openJacocoReport", Exec::class.java) {
    group = "works-jacoco"

    tasks.withType<JacocoReport> {
        val index = reports.html.entryPoint.absolutePath
        setCommandLine("open", index)
    }
}
