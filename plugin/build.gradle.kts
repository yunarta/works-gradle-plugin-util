import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.1.0"
}

group = "com.mobilesolutionworks.gradle"
version = "1.1.1"

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

val junitRulesSourceSet = java.sourceSets.create("junitRules") {
    java.srcDir("src/junitRules/java")
}

val junitRules = junitRulesSourceSet.let {
    configurations.getByName(it.compileConfigurationName)
}

val junitRulesJar = tasks.create("junitRulesJar", Jar::class.java) {
    classifier = "testKit"
    dependsOn(junitRulesSourceSet.output)
    from(junitRulesSourceSet.output.classesDirs)
}

val junitRulesProperties = tasks.create("junitRulesProperties", WriteProperties::class.java) {
    dependsOn(junitRulesJar)

    outputFile = file("${junitRulesJar.destinationDir}/testKit.properties")
    property("TestKit", "works-jacoco-${project.version}-testKit.jar")
}

val junitRulesRuntime = tasks.create("junitRulesRuntime", Copy::class.java) {
    dependsOn("junitRulesJar", "junitRulesProperties")
    from(junitRulesJar.archivePath) {
        into("META-INF")
    }
    from(junitRulesProperties.outputFile) {
        into("META-INF")
    }

    destinationDir = file("$buildDir/junitRulesRuntime")
    tasks.withType<Test> {
        dependsOn(this@create)
    }
}

val jar = tasks.getByName<Jar>("jar") {
    dependsOn("junitRulesProperties")
    from(junitRulesJar.archivePath) {
        this.into("META-INF")
    }
    from(junitRulesProperties.outputFile) {
        into("META-INF")
    }
}

tasks.create("checkJar", Copy::class.java) {
    dependsOn("jar")

    from(zipTree(jar.archivePath))
    into("$buildDir/test")
}

dependencies {
    compileOnly(gradleApi())

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.apache.commons:commons-lang3:3.7")

    implementation(files(junitRulesJar.archivePath))
    runtime(files(junitRulesRuntime.destinationDir))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation(junitRulesSourceSet.output)
    testImplementation(files(junitRulesRuntime.destinationDir))

    junitRules("junit:junit:4.12")
    junitRules(kotlin("stdlib-jdk8"))
    junitRules(kotlin("reflect"))
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

tasks.withType<PluginUnderTestMetadata> {

}

tasks.withType<Delete>().whenObjectAdded {
    if (name == "cleanTest") {
        println("Clean up testKit")
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
