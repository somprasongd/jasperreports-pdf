package com.github.somprasongd.jasperreports.pdf.exception;

import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Object> handleReportGenerationException(ReportGenerationException ex, HttpServletRequest request) {
//        // Get the request body from the cached wrapper
//        try {
//            String requestBody = getRequestBody(request);
//            System.out.println("requestBody:" + requestBody);
//            if (requestBody != null) {
//                // Send the exception to Sentry with the request body
//                Sentry.captureException(ex, scope -> {
//                    scope.setExtra("request_body", requestBody); // Add request body to the error event
//                });
//            }
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }

        // Construct the JSON error response
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Report generation failed",
                ex.getMessage() + ": " + ex.getCause().getMessage()
        );
        // Send the JSON error response with the appropriate HTTP status code
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getRequestBody(HttpServletRequest request) throws UnsupportedEncodingException {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, wrapper.getCharacterEncoding());
            }
        }
        return null;
    }
}
