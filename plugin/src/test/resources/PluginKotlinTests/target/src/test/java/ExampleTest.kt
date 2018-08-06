package example

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration
import org.junit.Test

class ExampleTest {

    @Test
    fun test() {
        TestKitConfiguration("javaagent-for-testkit.properties", "jacoco")
    }
}