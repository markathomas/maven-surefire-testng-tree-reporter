package org.apache.maven.plugin.surefire;

import org.testng.Assert;
import org.testng.annotations.Test;

class ExampleTest {

    @Test(description = "Should pass")
    void main() {
    }

    @Test(description = "Should pass again")
    void main2() {
    }

    class InnerTest {

        @Test(description = "My first inner test should not pass", enabled = false)
        void test() {
            Assert.assertTrue(false);
        }

        @Test(description = "My second inner test should be skipped", enabled = false)
        void test2() {

        }
    }

    @Test(description = "Should pass for the 3rd time")
    void main3() {
    }

    @Test(description = "Should pass for the 4th time")
    void main4() {
    }
}