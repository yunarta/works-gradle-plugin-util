import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("works-publish")
}

group = "com.mobilesolutionworks"
version = "1.0.0"

repositories {
    mavenCentral()
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


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    doFirst {
        logger.quiet("Test with max $maxParallelForks parallel forks")
    }
}

tasks.withType<Test> {
    doFirst {
        logger.quiet("Test JVM Arguments")
        allJvmArgs.forEach {
            logger.quiet(" $it")
        }
    }
}

tasks.createLater("openJacocoReport", Exec::class.java) {
    group = "jacoco"

    tasks.withType<JacocoReport> {
        val index = reports.html.entryPoint.absolutePath
        setCommandLine("open", index)
    }
}
