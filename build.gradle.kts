buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.mobilesolutionworks.gradle.reporting") version "1.0.8"
    id("jacoco")
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}