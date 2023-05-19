package com.example.myhome;


import com.example.myhome.controller.AccountController;
import com.example.myhome.controller.AdminController;
import com.example.myhome.controllers.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccountControllerTest.class, AdminControllerTest.class,
        InvoiceControllerTest.class, MeterControllerTest.class, RequestControllerTest.class,
        ServiceControllerTest.class, SettingsControllerTest.class, TariffControllerTest.class,
        WebsiteControllerTest.class
})
public class TestRunner {

//    public static void main(String[] args) {
//
//        Result result = JUnitCore.runClasses();
//
//        System.out.println("Total number of tests: " + result.getRunCount());
//        System.out.println("Total number of tests failed: " + result.getFailureCount());
//
//        for(Failure failure : result.getFailures())
//        {
//            System.out.println(failure.getMessage());
//        }
//        System.out.println(result.wasSuccessful());
//
//    }

}
