package com.github.somprasongd.jasperreports.pdf.controller;

import com.github.somprasongd.jasperreports.pdf.dto.ReportDto;
import com.github.somprasongd.jasperreports.pdf.exception.ReportGenerationException;
import com.github.somprasongd.jasperreports.pdf.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("v1/jasper")
public class JasperreportsGenneraterController {


    private final ReportService reportService;
    @Autowired
    public JasperreportsGenneraterController(ReportService reportService) {
        this.reportService = reportService;
    }


    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void generatePDF(@RequestBody ReportDto reportDto, HttpServletResponse response)  {

        JasperPrint jasperPrint = reportService.generateReport(reportDto);

        try {
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } catch (JRException | IOException e) {
            throw new ReportGenerationException("Failed to export pdf stream", e);
        }
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "inline; filename=" + reportDto.getMainReport().getName() + ".pdf;");
    }

}
