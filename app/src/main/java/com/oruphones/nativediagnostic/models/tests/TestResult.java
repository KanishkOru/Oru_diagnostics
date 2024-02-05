package com.oruphones.nativediagnostic.models.tests;

/**
 * Created by Pervacio on 16-08-2017.
 * FAIL,
 * OPTIMIZABLE,
 * OPTIMIZED,
 * PASS,
 * NONE,
 * NOT_FOUND,
 * IN_PROGRESS,
 * SKIPPED,
 * ACCESS_DENIED,
 * INTERRUPTED,
 * NOT_TESTED,
 * COMPLETED;
 */

public interface TestResult {
     String FAIL="FAIL";
     String CANBEIMPROVED = "OPTIMIZABLE";
     String OPTIMIZED = "OPTIMIZED";
     String PASS="PASS";
     String NONE="NONE";
     String NOTEQUIPPED = "NONE";//"NOTEQUIPPED"; //changing this as discuss with prem & rohit, we will change once modification will done
     String SKIPPED="SKIPPED";
     String ACCESSDENIED = "ACCESS_DENIED";//"ACCESSDENIED";
     String USERINPUT = "USERINPUT";
     String TIMEOUT = "TIMEOUT";
     String NOTSUPPORTED = "NOT_SUPPORTED";
     String SHOW_SUGGESTION = "SHOW_SUGGESTION";
}
