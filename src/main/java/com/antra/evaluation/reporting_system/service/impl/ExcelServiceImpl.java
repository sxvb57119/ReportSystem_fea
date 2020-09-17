package com.antra.evaluation.reporting_system.service.impl;

import com.antra.evaluation.reporting_system.converter.Request2Data;
import com.antra.evaluation.reporting_system.converter.Request2File;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService excelGenerationService;

    private final AtomicInteger atomicId = new AtomicInteger();

    @Override
    public InputStream getExcelBodyById(String id) {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
       // if (fileInfo.isPresent()) {
            File file = new File(id + ".xlsx");
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
      //  }
        return null;
    }

    @Override
    public ExcelFile save(ExcelRequest excelRequest) throws IOException {
        String id = "Excel-" + atomicId.incrementAndGet();

        ExcelData excelData = (excelRequest instanceof MultiSheetExcelRequest) ? Request2Data.convert2MultiSheetData((MultiSheetExcelRequest) excelRequest, id)
                : Request2Data.convert2SingleSheetData(excelRequest, id);
        File savedFile = excelGenerationService.generateExcelReport(excelData);
        ExcelFile excelFile = Request2File.convertRequestToFile(excelRequest, id);
        excelRepository.saveFile(excelFile);
        return excelFile;



    }


}
