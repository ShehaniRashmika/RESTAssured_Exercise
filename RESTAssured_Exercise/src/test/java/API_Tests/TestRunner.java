package com.example.restassured_exercise;

import org.testng.TestNG;

public class TestRunner {

    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[] { AuthorizationTests.class, GetUserDetails.class, CreateUsers.class });
        testNG.run();
    }
}
