package com.sabbir.walton.mmitest.Report;

import java.util.HashMap;
import java.util.Map;

public class TestReportManager {
    private static TestReportManager instance;
    private Map<String, String> testResults = new HashMap<>();

    private TestReportManager() {
        initializeTests();
    }

    public static TestReportManager getInstance() {
        if (instance == null) {
            instance = new TestReportManager();
        }
        return instance;
    }

    private void initializeTests() {
        String[] testNames = {
                "Sim Card Test", "SD Card Test", "LCD Test",
                "Multi Touch Test", "Single Touch Test", "Flash Light Test",
                "Key Test", "Speaker Test", "Receiver Test",
                "Earphone Test", "MIC Test", "Call Test",
                "Vibration Test", "Location Test", "Wi-Fi Test",
                "Bluetooth Test", "Fingerprint Test", "Battery Status Test",
                "Rear Camera Test", "Front Camera Test", "Proximity Sensor Test",
                "Charging Test"
        };

        for (String testName : testNames) {
            testResults.put(testName, "unchecked");
        }
    }

    public void updateTestStatus(String testName, boolean passed) {
        testResults.put(testName, passed ? "PASSED" : "FAILED");
    }

    public Map<String, String> getTestResults() {
        return testResults;
    }
}
