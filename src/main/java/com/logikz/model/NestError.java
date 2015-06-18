package com.logikz.model;

/**
 * Created by Nick on 6/14/2015.
 */
public class NestError {
    private String error;
    private String error_description;

    public NestError(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public NestError() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
