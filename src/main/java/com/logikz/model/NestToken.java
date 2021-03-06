package com.logikz.model;

/**
 * Created by Nick on 6/13/2015.
 */
public class NestToken {
    private String access_token;
    private long expires_in;

    public NestToken(String access_token, long expires_in) {
        this.access_token = access_token;
        this.expires_in = expires_in;
    }

    public NestToken() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
