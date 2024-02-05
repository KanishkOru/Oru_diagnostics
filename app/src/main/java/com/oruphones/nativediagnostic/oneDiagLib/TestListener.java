package com.oruphones.nativediagnostic.oneDiagLib;

import org.pervacio.onediaglib.diagtests.TestResult;

public interface TestListener {
    void onTestStart();

    void onTestEnd(TestResult var1);
}

