buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/snapshot")
        }
    }

    dependencies {
        classpath("com.mobilesolutionworks:works-publish:1.5.3-BUILD-2")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}