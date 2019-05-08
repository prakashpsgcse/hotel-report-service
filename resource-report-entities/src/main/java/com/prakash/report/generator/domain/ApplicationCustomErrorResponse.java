package com.prakash.report.generator.domain;

public class ApplicationCustomErrorResponse {

    private int errorCode;
    private String errorDetails;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

}
