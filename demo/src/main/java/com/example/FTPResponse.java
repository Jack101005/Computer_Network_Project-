package com.example;

public class FTPResponse {
    public final int code;
    public final String message;

    public FTPResponse(int code, String message) {
        this.code = code;
        this.message = message;

    }
    @Override
    public String toString() {
        return code + " " + message;
    }


}
