package com.github.somprasongd.jasperreports.pdf.service;

import com.github.somprasongd.jasperreports.pdf.dto.JasperDto;
import com.github.somprasongd.jasperreports.pdf.dto.ParameterDto;
import com.github.somprasongd.jasperreports.pdf.dto.ReportDto;
import com.github.somprasongd.jasperreports.pdf.exception.ReportGenerationException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final String JASPER_DIR = System.getProperty("user.dir") + File.separator + "jaspers";
    private final JdbcTemplate jdbcTemplateOPD; // Specify the desired JdbcTemplate
    private final JdbcTemplate jdbcTemplateIPD; // Specify the desired JdbcTemplate


    @Autowired
    public ReportService(JdbcTemplate jdbcTemplateOPD, JdbcTemplate jdbcTemplateIPD) {
        this.jdbcTemplateOPD = jdbcTemplateOPD;
        this.jdbcTemplateIPD = jdbcTemplateIPD;
    }


    private String getReportNameFromUrl(String url) throws IOException {
        URL reportUrl = new URL(url);
        return reportUrl.getPath().substring(reportUrl.getPath().lastIndexOf("/") + 1, reportUrl.getPath().lastIndexOf("."));
    }

    private JasperReport loadJasperReport(String sourceFile) {
        File file = new File(sourceFile);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            return jasperReport;
        } catch (JRException ex) {
            return null;
        }

    }

    public JasperPrint generateReport(ReportDto reportDto) {

        JasperDto mainReport = reportDto.getMainReport();

        String parentPath = JASPER_DIR + File.separator + mainReport.getName();

        String mainJasperPath = parentPath + File.separator + mainReport.getName() + "." + mainReport.getModified_at() + ".jasper";

        JasperReport mainJasperReport = null;
        if (new File(mainJasperPath).exists()) {
            mainJasperReport = loadJasperReport(mainJasperPath);
        } else {
            try {
                mainJasperReport = compileReport(mainReport.getUrl(), mainJasperPath);
            } catch (IOException | JRException e) {
                logger.error("Failed to generate the report", e);
                throw new ReportGenerationException("Failed to generate the report", e);
            }
        }

        // compile sub report to .jasper
        if (reportDto.getSubReports() != null) {
            for (JasperDto subReport : reportDto.getSubReports()) {
                String subJasperPath = parentPath + File.separator + subReport.getName() + "." + subReport.getModified_at() + ".jasper";

                if (new File(subJasperPath).exists()) {
                    // check is exist
                    JasperReport jr = loadJasperReport(subJasperPath);
                    // skip
                    if (jr != null) {
                        continue;
                    }
                } else {
                    // compile and save
                    try {
                        compileReport(subReport.getUrl(), subJasperPath);
                    } catch (IOException | JRException e) {
                        logger.error("Failed to generate the sub-report", e);
                        throw new ReportGenerationException("Failed to generate the sub-report", e);
                    }
                }
            }
        }

        // generate report
        JasperPrint jasperPrint;
        DataSource dataSource;
        if (reportDto.getDatasource().equalsIgnoreCase("opd")) {
            dataSource = jdbcTemplateOPD.getDataSource();
        } else {
            dataSource = jdbcTemplateIPD.getDataSource();
        }

        try (Connection conn = dataSource.getConnection()) {
            Map<String, Object> params = new HashMap<>();
            for (ParameterDto param :
                    reportDto.getParameters()) {
                if (param.getName().equalsIgnoreCase("SUBREPORT_DIR")) {
                    continue;
                }
                params.put(param.getName(), param.getConvertedValue());
            }
            params.put("SUBREPORT_DIR", parentPath + File.separator);
            logger.info("Parameters for " + mainReport.getName() + ":");
            for (String key :
                    params.keySet()) {
                logger.info(key + ":" + params.get(key) + ":" + params.get(key).getClass());
            }
            jasperPrint = JasperFillManager.fillReport(
                    mainJasperReport, params, conn);
        } catch (Exception e) {
            logger.error("Generate PDF failed", e);
            throw new ReportGenerationException("Failed to generate pdf", e);
        }
        return jasperPrint;
    }

    private JasperReport compileReport(String url, String jasperPath) throws IOException, JRException {
        URL reportUrl = new URL(url);
        JasperReport jasperReport;
        try (InputStream employeeReportStream = reportUrl.openStream()) {
            jasperReport = JasperCompileManager.compileReport(employeeReportStream);
            File file = new File(jasperPath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            JRSaver.saveObject(jasperReport, jasperPath);
        }
        return jasperReport;
    }
}
