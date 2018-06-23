import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.0.0"
}

group = "com.mobilesolutionworks.gradle"
version = "1.0.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.1"
}

worksJacoco {
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
    compileOnly(gradleApi())
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

pluginBundle {
    website = "https://github.com/yunarta/works-jacoco-gradle-plugin"
    vcsUrl = "https://github.com/yunarta/works-jacoco-gradle-plugin"
    description = "Plugin to reduce code duplication especially when writing Gradle plugin project"
    tags = listOf("jacoco", "works")

    (plugins) {
        "works-jacoco" {
            id = "com.mobilesolutionworks.gradle.jacoco"
            displayName = "Gradle Jacoco task manager"
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

    classDirectories = fileTree(mapOf("dir" to "$buildDir/classes/kotlin/main"))
    sourceDirectories = files(listOf("src/main/kotlin", "src/main/java"))
    executionData = fileTree(mapOf("dir" to buildDir, "include" to "jacoco/*.exec"))
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
