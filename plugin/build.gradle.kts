import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.1.3"
}

group = "com.mobilesolutionworks.gradle"
version = "1.1.3"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.1"
}

worksJacoco {
    hasTestKit = true
    useTestKitLib = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    reports {
        html.isEnabled = false
    }
}

java.sourceSets.create("testKit") {
    java.srcDir("src/testKit/java")
}.let { sourceSet ->
    dependencies {
        testImplementation(sourceSet.output)
    }

    val jarTestKit = tasks.create("jarTestKit", Jar::class.java) {
        group = "testKit"
        classifier = "testKit"

        dependsOn(sourceSet.output)
        from(sourceSet.output.classesDirs)

        dependencies {
            implementation(files(archivePath))
        }
    }

    val testKitProperties = tasks.create("testKitProperties", WriteProperties::class.java) {
        group = "testKit"
        dependsOn(jarTestKit)

        outputFile = file("${jarTestKit.destinationDir}/testKit.properties")
        property("TestKit", "works-jacoco-${project.version}-testKit.jar")
    }

    tasks.create("testKitInjectRuntime", Copy::class.java) {
        group = "testKit"

        dependsOn(jarTestKit, testKitProperties)
        from(jarTestKit.archivePath) {
            into("META-INF")
        }
        from(testKitProperties.outputFile) {
            into("META-INF")
        }

        destinationDir = file("$buildDir/testKit/runtime")
        tasks.withType<Test> {
            dependsOn(this@create)
        }

        dependencies {
            val files = files(destinationDir)

            runtime(files)
            testImplementation(files)
        }
    }

    tasks.getByName<Jar>("jar") {
        dependsOn(jarTestKit, testKitProperties)
        from(jarTestKit.archivePath) {
            this.into("META-INF")
        }

        from(testKitProperties.outputFile) {
            into("META-INF")
        }
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
    description = "Plugin to reduce code duplication when writing Gradle plugin project"
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

    classDirectories = fileTree(mapOf("dir" to "$buildDir/classes/kotlin/main")) +
            fileTree(mapOf("dir" to "$buildDir/classes/kotlin/junitRules"))
    sourceDirectories = files(listOf("src/main/kotlin", "src/junitRules/kotlin"))
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
