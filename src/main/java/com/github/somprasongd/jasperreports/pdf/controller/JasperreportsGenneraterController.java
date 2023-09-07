package com.github.somprasongd.jasperreports.pdf.controller;

import com.github.somprasongd.jasperreports.pdf.dto.ReportDto;
import com.github.somprasongd.jasperreports.pdf.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("v1/jasper")
public class JasperreportsGenneraterController {

    @Autowired
    private ReportService reportService;


    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void createPost(@RequestBody ReportDto reportDto, HttpServletResponse response) throws IOException, JRException {

        JasperPrint jasperPrint = reportService.generateReport(reportDto);

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "inline; filename=" + reportDto.getMainReport().getName() + ".pdf;");
    }

}
