package org.apache.maven.plugin.surefire;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ParameterizedExampleTest {

    @Test(description = "1 + 1 = 2")
    void addsTwoNumbers() {
        Calculator calculator = new Calculator();
        Assert.assertEquals(calculator.add(1, 1), 2, "1 + 1 should equal 2");
    }

    @DataProvider(name = "add")
    public static Object[][] add() {
        return new Object[][] {
          { 1,    2,   3 },
          { 49,  51, 100 },
          { 1,  100, 101 }
        };
    }

    @Test(description = "{0} + {1} = {2}", dataProvider = "add")
    void add(int first, int second, int expectedResult) {
        Calculator calculator = new Calculator();
        Assert.assertEquals(expectedResult, calculator.add(first, second),
          first + " + " + second + " should equal " + expectedResult);
    }

    static class Calculator {
        public int add(int a, int b) {
            return a + b;
        }
    }

}
