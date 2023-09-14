package com.github.somprasongd.jasperreports.pdf.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Object> handleReportGenerationException(ReportGenerationException ex) {
        // Construct the JSON error response
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Report generation failed",
                ex.getMessage() + ": " + ex.getCause().getMessage()
        );
        // Send the JSON error response with the appropriate HTTP status code
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
