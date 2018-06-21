buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/release")
        }
    }

    dependencies {
        classpath("com.mobilesolutionworks:works-publish:1.5.2")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}