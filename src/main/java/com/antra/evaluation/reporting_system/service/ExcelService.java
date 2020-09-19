package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.request.MultiExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelService {

    InputStream getExcelBodyById(String id);

    ExcelFile getExcelDataById(String id);

    ExcelFile saveExcel(ExcelRequest excelRequest) throws IOException;

    void deleteExcel(String id);

    List<ExcelFile> getAllFiles();

    List<ExcelFile> saveMultiExcel(MultiExcelRequest multiExcelRequest) throws IOException;
}
