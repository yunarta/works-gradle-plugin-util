package example;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

public class ExampleTest {

    @Test
    public void verifyResourceExists() {
        URL resource = getClass().getClassLoader().getResource("javaagent-for-testkit.properties");
        Assert.assertNotNull(resource);
    }

    @Test
    public void verifyResourceNotCreated() {
        URL resource = getClass().getClassLoader().getResource("javaagent-for-testkit.properties");
        Assert.assertNull(resource);
    }
}