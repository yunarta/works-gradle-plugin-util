# Works Jacoco - Gradle Plugin
[![Build Status](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-jacoco-gradle-plugin/job/master/badge/icon)](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-jacoco-gradle-plugin/job/master/)
[![codecov](https://codecov.io/gh/yunarta/works-jacoco-gradle-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yunarta/works-jacoco-gradle-plugin)

Plugin to simplify speed up compile process and helping making TestKit development easy. 

## Fact

Gradle Jacoco Plugin

- When the plugin is added in project, all test will always runs in coverage mode
- This would lead to slower testing process in overall

## Works Jacoco

### Basic Configuration

```groovy
plugins {
    id "com.mobilesolutionworks.gradle.jacoco" version "1.1.3"
}

worksJacoco {
    // (default: false)
    onlyRunCoverageWhenReporting = [true|false]
}
```

When onlyRunCoverageWhenReporting is set to true, the plugin will disable coverage during testing if jacoco report task 
is not executed along in the queue.

Hence, execution of ```./gradlew test``` will not produce coverage, while ```./gradlew test jacocoReport``` will.
This will also include your own custom task derived from JacocoReport.

Setting the value to false will not change any behaviour. 

### Gradle Plugin TestKit

```groovy
plugins {
    id "com.mobilesolutionworks.gradle.jacoco" version "1.1.3"
}

worksJacoco {
    // when set to true, additional task for Gradle plugin development is added
    // see below for more detail
    hasTestKit = [true|false]

    
    // directory to place exec file generated from GradleRunner execution
    testKitExecDir = "$buildDir/jacoco"
    
    // additional property to be based to TestKit
    testKitTmpDir = "$buildDir/tmp/testKit"
}
```

This plugin will help you ease Gradle Plugin development in ways below

**Works TestKit Library**

This plugin will add works-jacoco-testKit.jar into your testImplementation dependencies.
The library contains two class

```java
class TestKitConfiguration {
    
    public final String agentString
    
    public TestKitConfiguration(String execFile)
}
```

The instance of this class will produce Jacoco agentString with destination file pointed to ```execFile``` name within
```testKitExecDir``` directory

Within your test case, you can use the ```agentString``` in anyway your desire.

```java
public class TestKit {

    public final File rootDir;

    public TestKit(String resourcePath) throws IOException
}
```

Usage:
```
public class TestCaseClass extends TestKit {
    
    public TestCaseClass() {
        super("GradleTest-1")
    }
}
```

This simple helper class that will copy a folder from test resources directory, specified with the resourcePath into
project ```build/tmp/testKit/$className/$resourcePath``` where the className will be ```TestCaseClass``` and the resourcePath will be ```GradleTest-1```.

Then it will create a gradle.properties file containing ```org.gradle.jvmargs``` with TestKitConfiguration agentString
where the exec file output name will be ```TestCaseClass.exec```
  
**Note**

The existence of gradle.properties ```org.gradle.jvmargs``` is also controlled by ```worksJacoco.onlyRunCoverageWhenReporting``` which means coverage of TestKit execution can be controlled as well.

While the improvement of optional coverage might not be very visible and significant, but as the number of test classes
grow, this would shave a significant portion of running all test. 