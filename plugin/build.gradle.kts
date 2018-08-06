import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC7-2"
    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.1.3"
    id("com.mobilesolutionworks.gradle.reporting")
}

group = "com.mobilesolutionworks.gradle"
version = "1.1.3"

jacoco {
    toolVersion = "0.8.1"
}

worksJacoco {
    hasTestKit = true
    useTestKitLib = false
}

worksReporting  {
    checkstyleTasks.add("detektCheck")
    checkstyleFiles = files("build/reports/detekt")
}

detekt {
    version = "1.0.0.RC7-2"

    profile("main", Action {
        input = "src/main/kotlin"
        filters = ".*/resources/.*,.*/build/.*"
        config = file("default-detekt-config.yml")
        output = "$buildDir/reports/detekt"
        outputName = "detekt-report"
        baseline = "reports/baseline.xml"
    })
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    reports {
        html.isEnabled = false
    }
}

val testKit by configurations.creating
java.sourceSets.create("testKit") {
    java.srcDir("src/testKit/java")
}.let { sourceSet ->
    dependencies {
        testKit(gradleTestKit())
        testKit("org.junit.jupiter:junit-jupiter-api:5.3.0-M1")
        testImplementation(sourceSet.output)
    }

    afterEvaluate {
        sourceSet.compileClasspath = files(testKit.files)
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
            into("META-INF")
        }

        from(testKitProperties.outputFile) {
            into("META-INF")
        }
    }
}

val testKitKotlin by configurations.creating
java.sourceSets.create("testKitKotlin") {
    java.srcDir("src/testKitKotlin/java")
}.let { sourceSet ->
    dependencies {
        testKitKotlin(kotlin("stdlib-jdk8"))
        testKitKotlin(kotlin("reflect"))
        testKitKotlin(gradleTestKit())
        testKitKotlin("org.junit.jupiter:junit-jupiter-api:5.3.0-M1")
        testImplementation(sourceSet.output)
    }

    afterEvaluate {
        sourceSet.compileClasspath = files(testKitKotlin.files)
    }

    val jarTestKit = tasks.create("jarTestKitKotlin", Jar::class.java) {
        group = "testKit"
        classifier = "testKitKotlin"

        dependsOn(sourceSet.output)
        from(sourceSet.output.classesDirs)

        dependencies {
            implementation(files(archivePath))
        }
    }

    val testKitProperties = tasks.create("testKitKotlinProperties", WriteProperties::class.java) {
        group = "testKit"
        dependsOn(jarTestKit)

        outputFile = file("${jarTestKit.destinationDir}/testKitKotlin.properties")
        property("TestKitKotlin", "works-jacoco-${project.version}-testKitKotlin.jar")
    }

    tasks.create("testKitKotlinInjectRuntime", Copy::class.java) {
        group = "testKit"

        dependsOn(jarTestKit, testKitProperties)
        from(jarTestKit.archivePath) {
            into("META-INF")
        }
        from(testKitProperties.outputFile) {
            into("META-INF")
        }

        destinationDir = file("$buildDir/testKitKotlin/runtime")
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
    compileOnly(gradleTestKit())

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.apache.commons:commons-lang3:3.7")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.0-M1")

    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.0-M1")
    testRuntime("org.junit.vintage:junit-vintage-engine:5.3.0-M1")
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

    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    doFirst {
        logger.quiet("Test with max $maxParallelForks parallel forks")
    }
    finalizedBy("jacocoTestReport")
}
