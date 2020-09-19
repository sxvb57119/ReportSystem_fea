package com.antra.evaluation.reporting_system.service.impl;

import com.antra.evaluation.reporting_system.pojo.api.request.MultiExcelRequest;
import com.antra.evaluation.reporting_system.utility.converter.MultiExcelRequest2List;
import com.antra.evaluation.reporting_system.utility.converter.Request2Data;
import com.antra.evaluation.reporting_system.enums.ErrorEnum;
import com.antra.evaluation.reporting_system.exception.FileException;
import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.request.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        ExcelFile fileInfo = excelRepository.getFileById(id);
        if(fileInfo == null) return null;

        File file = new File(id + ".xlsx");
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new  FileException(ErrorEnum.FILE_NOT_EXIST);
        }

    }

    @Override
    public ExcelFile saveExcel(ExcelRequest excelRequest) throws IOException {
        String id = "Excel-" + atomicId.incrementAndGet();
        LocalDateTime generatedTime = LocalDateTime.now();
        ExcelData excelData = (excelRequest instanceof MultiSheetExcelRequest) ? Request2Data.convert2MultiSheetData((MultiSheetExcelRequest) excelRequest, id, generatedTime)
                : Request2Data.convert2SingleSheetData(excelRequest, id, generatedTime);
        File savedFile = excelGenerationService.generateExcelReport(excelData);
        ExcelFile excelFile = ExcelFile.builder().id(id).generatedTime(generatedTime).fileSize(savedFile.length() + "B").
                downLoadLink("localhost:8080/excel/" + id + "/content").build();
        excelRepository.saveFile(excelFile);
        return excelFile;
    }

    @Override
    public List<ExcelFile> saveMultiExcel(MultiExcelRequest multiExcelRequest) throws IOException {

        List<ExcelRequest> excelRequestList = multiExcelRequest.getExcelRequestList();
        for(ExcelRequest excelRequest : excelRequestList) {

        }
        return null;
    }

    @Override
    public ExcelFile getExcelDataById(String id) {
       return  excelRepository.getFileById(id);
    }

    @Override
    public void deleteExcel(String id) {
        excelRepository.deleteFile(id);
        return;
    }

    @Override
    public List<ExcelFile> getAllFiles() {
        return excelRepository.getListFiles();
    }
}
