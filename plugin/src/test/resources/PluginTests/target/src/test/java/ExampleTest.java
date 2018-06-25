package example;

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration;
import org.junit.Test;

public class ExampleTest {

    @Test
    public void test() {
        new TestKitConfiguration("javaagent-for-testkit.properties", getClass().getSimpleName());
    }
}