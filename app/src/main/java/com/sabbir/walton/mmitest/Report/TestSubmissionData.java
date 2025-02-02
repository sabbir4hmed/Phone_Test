package com.sabbir.walton.mmitest.Report;

import java.util.Map;

public class TestSubmissionData {

    private String imei;
    private String srNumber;
    private String remarks;
    private Map<String, String> testResults;

    public TestSubmissionData(String imei, String srNumber, String remarks, Map<String, String> testResults) {
        this.imei = imei;
        this.srNumber = srNumber;
        this.remarks = remarks;
        this.testResults = testResults;
    }

    // Add getters and setters
    public String getImei() { return imei; }
    public String getSrNumber() { return srNumber; }
    public String getRemarks() { return remarks; }
    public Map<String, String> getTestResults() { return testResults; }

}
