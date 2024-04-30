package com.github.somprasongd.jasperreports.pdf.service;

import com.github.somprasongd.jasperreports.pdf.dto.JasperDto;
import com.github.somprasongd.jasperreports.pdf.dto.ParameterDto;
import com.github.somprasongd.jasperreports.pdf.dto.ReportDto;
import com.github.somprasongd.jasperreports.pdf.exception.ReportGenerationException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
    private final String JRXML_DIR = System.getProperty("user.dir") + File.separator + "jrxmls";
    private final String IMG_DIR = System.getProperty("user.dir") + File.separator + "images";
    private final JdbcTemplate jdbcTemplateOPD; // Specify the desired JdbcTemplate
    private final JdbcTemplate jdbcTemplateIPD; // Specify the desired JdbcTemplate


    @Autowired
    public ReportService(JdbcTemplate jdbcTemplateOPD, JdbcTemplate jdbcTemplateIPD) {
        this.jdbcTemplateOPD = jdbcTemplateOPD;
        this.jdbcTemplateIPD = jdbcTemplateIPD;
    }


//    private String getReportNameFromUrl(String url) throws IOException {
//        URL reportUrl = new URL(url);
//        return reportUrl.getPath().substring(reportUrl.getPath().lastIndexOf("/") + 1, reportUrl.getPath().lastIndexOf("."));
//    }

    private JasperReport loadJasperReport(String sourceFile) {
        File file = new File(sourceFile);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try {
            return (JasperReport) JRLoader.loadObject(file);
        } catch (JRException ex) {
            return null;
        }

    }

    private String removeFileExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        if (idx != -1) { // Ensuring that a dot was found in the fileName
            return fileName.substring(0, idx);
        } else {
            return fileName; // If no dot was found, return the original fileName
        }
    }

    public JasperPrint generateReport(ReportDto reportDto) {

        JasperDto mainReport = reportDto.getMainReport();

        String parentPath = JASPER_DIR + File.separator + removeFileExtension(mainReport.getName());

        String mainJasperPath = parentPath + File.separator + removeFileExtension(mainReport.getName()) + "." + mainReport.getModified_at() + ".jasper";

        boolean mainReportIsExists = new File(mainJasperPath).exists();
        JasperReport mainJasperReport = null;
        if (mainReportIsExists) {
            mainJasperReport = loadJasperReport(mainJasperPath);
        }

        if (mainJasperReport == null) {
            try {
                mainJasperReport = compileReport(mainReport.getUrl(), mainJasperPath);
            } catch (Exception e) {
                logger.error("Failed to generate the report", e);
                throw new ReportGenerationException("Failed to generate the report", e);
            }
        }

        // compile sub report to .jasper
        if (reportDto.getSubReports() != null) {
            for (JasperDto subReport : reportDto.getSubReports()) {
                String fileName = removeFileExtension(subReport.getName());
                String subJasperFileVersionPrefix = fileName + ":";
                String subJasperFileVersion = parentPath + File.separator + subJasperFileVersionPrefix + ":" + subReport.getModified_at();
                String subJasperPath = parentPath + File.separator + fileName + ".jasper";

                if (new File(subJasperFileVersion).exists()) {
                    // check is exist
                    JasperReport jr = loadJasperReport(subJasperPath);
                    // skip
                    if (jr != null) {
                        continue;
                    }
                }

                try {
                    // remove early version
                    deleteFilesWithPrefix(parentPath, subJasperFileVersionPrefix);
                    // save new version
                    createVersionFile(subJasperFileVersion);
                    // compile and save
                    compileReport(subReport.getUrl(), subJasperPath);
                } catch (Exception e) {
                    logger.error("Failed to generate the sub-report", e);
                    throw new ReportGenerationException("Failed to generate the sub-report", e);
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
                if (param.getName().equalsIgnoreCase("IMAGE_DIR")) {
                    continue;
                }
                params.put(param.getName(), param.getConvertedValue());
            }
            params.put("SUBREPORT_DIR", parentPath + File.separator);
            params.put("IMAGE_DIR", IMG_DIR + File.separator);
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

    private JasperReport compileReport(String url, String jasperPath) throws Exception {
        JasperReport jasperReport;
        if (isValidURL(url)) {
            jasperReport = getJasperReportFromURL(url);
        } else {
            jasperReport = getJasperReportFromFile(url);
        }
        // save compiled file
        File file = new File(jasperPath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        JRSaver.saveObject(jasperReport, jasperPath);
        return jasperReport;
    }

    private JasperReport getJasperReportFromFile(String path) throws Exception {
        File reportFile = new File(JRXML_DIR + File.separator + path);
        if (!reportFile.isFile()) {
            throw new Exception(path + " is not a file.");
        }

        if (!reportFile.getName().endsWith(".jrxml")) {
            throw new Exception(path + " is not jrxml file.");
        }

        if (!reportFile.exists()) {
            throw new Exception(path + " is not exists.");
        }

        JasperDesign jasperDesign = JRXmlLoader.load(reportFile);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private JasperReport getJasperReportFromURL(String url) throws IOException, JRException{
        URL reportUrl = new URL(url);

        try (InputStream employeeReportStream = reportUrl.openStream()) {
            return JasperCompileManager.compileReport(employeeReportStream);
        }
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;

        }
    }

    private void deleteFilesWithPrefix(String directoryPath, String prefix) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(prefix)) {
                    if (file.delete()) {
                        logger.info("File deleted: " + file.getName());
                    } else {
                        logger.info("Failed to delete file: " + file.getName());
                    }
                }
            }
        } else {
            logger.warn("Directory does not exist or is not a directory.");
        }
    }

    private static void createVersionFile(String filePath) {
        File file = new File(filePath);

        try {
            if (file.createNewFile()) {
                logger.info("Empty file created successfully: " + file.getName());
            } else {
                logger.info("File already exists.");
            }
        } catch (IOException e) {
            logger.error("An error occurred while creating the file: " + e.getMessage());
        }
    }
}
