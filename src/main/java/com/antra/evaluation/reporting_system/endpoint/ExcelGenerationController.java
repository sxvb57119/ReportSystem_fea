package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.response.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.request.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) throws IOException {
        ExcelFile excelFile = excelService.saveExcel(request);

        return new ResponseEntity<ExcelResponse>(new ExcelResponse("success",excelFile), HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException {
        ExcelFile excelFile = excelService.saveExcel(request);
        return new ResponseEntity<ExcelResponse>(new ExcelResponse("success",excelFile), HttpStatus.OK);
    }



    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<ExcelResponse> listExcels() {
        List<ExcelFile> excelFileList = excelService.getAllFiles();

        return new ResponseEntity<ExcelResponse>(new ExcelResponse("ojbk",excelFileList), HttpStatus.OK);
    }

    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream fis = excelService.getExcelBodyById(id);
        response.setHeader("Content-Type","application/vnd.ms-excel");
        response.setHeader("Content-Disposition","attachment; filename=\"" + id + ".xls\""); // TODO: File name cannot be hardcoded here
        FileCopyUtils.copy(fis, response.getOutputStream());
    }

    @GetMapping("/excel/{id}/data")
    @ApiOperation("get metadata of an excel file")
    public ResponseEntity<ExcelResponse> getExcelFileById(@PathVariable String id) {
        ExcelFile excelFile = excelService.getExcelDataById(id);
        return new ResponseEntity<ExcelResponse>(new ExcelResponse("success",excelFile), HttpStatus.OK);

    }


    @DeleteMapping("/excel/{id}")
    @ApiOperation(value = "delete a Excel file")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        ExcelFile excelFile = excelService.getExcelDataById(id);
        excelService.deleteExcel(id);
        var response = new ExcelResponse();
        return new ResponseEntity<ExcelResponse>(new ExcelResponse("ojbk",excelFile), HttpStatus.OK);
    }
}
// Log
// Exception handling
// Validation
