package org.apache.maven.plugin.surefire;

import org.testng.annotations.Test;

public class NestedExampleTest {

    @Test(description = "Should pass")
    void test() throws InterruptedException {
        Thread.sleep(100);
    }

    class FirstInnerTest {
        @Test(description = "FirstInnerTest should show up")
        void test() throws InterruptedException {
            Thread.sleep(600);
        }
    }

    class InnerTest {

        @Test(description = "Inner test should pass")
        void test() throws InterruptedException {
            Thread.sleep(200);
        }

        class InnerInnerTest {

            @Test(description = "Inner Inner Test should pass")
            void test() throws InterruptedException {
                Thread.sleep(300);
            }

            class InnerInnerInnerTest {

                @Test(description = "Inner Inner Inner Test should pass")
                void test() throws InterruptedException {
                    Thread.sleep(400);
                }

            }

        }

    }

    @Test(description = "Should pass2")
    void test2() throws InterruptedException {
        Thread.sleep(500);
    }

}
