package com.github.somprasongd.jasperreports.pdf.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("healthz")
public class HealthcheckController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void healthz()  {
        return;
    }
}
