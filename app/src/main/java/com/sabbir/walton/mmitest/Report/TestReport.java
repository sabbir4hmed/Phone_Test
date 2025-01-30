package com.sabbir.walton.mmitest.Report;

public class TestReport {
    private String testName;
    private String status;

    public TestReport(String testName) {
        this.testName = testName;
        this.status = "unchecked";
    }

    public String getTestName() {
        return testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}