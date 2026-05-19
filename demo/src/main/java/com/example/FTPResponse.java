package com.example;

public class FTPResponse {
    public final int code;
    public final String message;

    public FTPResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return code >= 100 && code < 400;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }

    public static FTPResponse parse(String raw) {
        if (raw == null || raw.length() < 3) return new FTPResponse(-1, raw);
        try {
            int code = Integer.parseInt(raw.substring(0, 3));
            String msg = raw.length() > 4 ? raw.substring(4) : "";
            return new FTPResponse(code, msg);
        } catch (NumberFormatException e) {
            return new FTPResponse(-1, raw);
        }
    }
}