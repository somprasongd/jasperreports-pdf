package com.github.somprasongd.jasperreports.pdf.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private int statusCode;
    private String error;
    private String message;

    // Constructors, getters, and setters

    public ErrorMessage(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }
}