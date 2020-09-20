package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;
import com.antra.evaluation.reporting_system.exception.DataException;
import com.antra.evaluation.reporting_system.exception.FileException;
import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.request.MultiExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.response.ErrorResponse;
import com.antra.evaluation.reporting_system.pojo.api.response.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import com.antra.evaluation.reporting_system.utility.ZipUtils;
import com.antra.evaluation.reporting_system.utility.group.GenericGroup;
import com.antra.evaluation.reporting_system.utility.group.MultiSheetGroup;
import com.antra.evaluation.reporting_system.utility.group.SingleSheetGroup;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.description.type.TypeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

    private ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated({SingleSheetGroup.class}) ExcelRequest request) {
        ExcelFile excelFile;
        try {
            excelFile = excelService.saveExcel(request);
        } catch (IOException e) {
            throw new FileException(ErrorEnum.UPLOAD_ERROR);
        }
        return new ResponseEntity<>(new ExcelResponse<>("Generated Successfully", excelFile), HttpStatus.CREATED);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated({MultiSheetGroup.class}) ExcelRequest request) throws IOException {
        ExcelFile excelFile;
        try {
            excelFile = excelService.saveExcel(request);
        } catch (IOException e) {
            throw new FileException(ErrorEnum.UPLOAD_ERROR);
        }
        return new ResponseEntity<>(new ExcelResponse<>("Generated Successfully", excelFile), HttpStatus.CREATED);
    }

    @PostMapping("/excel/multi")
    @ApiOperation("generate multiple excel files")
    public ResponseEntity<ExcelResponse> createMultiExcel(@RequestBody @Validated({GenericGroup.class}) MultiExcelRequest models) {
        boolean saveAll = true;
        List<ExcelRequest> excelRequestList = models.getExcels();
        List<Object> responseList = new ArrayList<>();
        for (ExcelRequest excelRequest : excelRequestList) {
            try {
                ExcelFile excelFile = excelService.saveExcel(excelRequest);
                responseList.add(new ExcelResponse("Generated Successfully", excelFile));
            } catch (IOException ex) {
                ErrorResponse error = new ErrorResponse();
                String message = ErrorEnum.UPLOAD_ERROR.getMessage();
                error.setMessage(message);
                log.error(message, ex);
                responseList.add(error);
                saveAll = false;

            } catch (DataException | FileException ex) {
                ErrorResponse error = new ErrorResponse();
                Integer errorCode = ex.getCode();
                error.setErrorCode(errorCode);
                String message = ex.getErrorMessage();
                error.setMessage(message);
                log.error(message, ex);
                responseList.add(error);
                saveAll = false;
            }
        }

        return new ResponseEntity<>(new ExcelResponse<>("Generated finished", responseList), saveAll ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);

    }


    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<ExcelResponse> listExcels() {
        List<ExcelFile> excelFileList = excelService.getAllFiles();
        if (excelFileList == null || excelFileList.size() == 0) throw new FileException(ErrorEnum.FILE_NOT_EXIST);
        return new ResponseEntity<>(new ExcelResponse("All Excel Files", excelFileList), HttpStatus.OK);
    }

    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) {
        InputStream fis = excelService.getExcelBodyById(id);
        if (fis == null) throw new FileException(ErrorEnum.FILE_NOT_EXIST);
        response.setHeader("Content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".xls\""); // TODO: File name cannot be hardcoded here
        try {
            FileCopyUtils.copy(fis, response.getOutputStream());
        } catch (IOException e) {
            throw new FileException(ErrorEnum.DOWNLOAD_ERROR);
        }

    }

    @GetMapping("/excel/multi/content")
    public void downloadMultiExcels(HttpServletResponse response, @RequestParam String[] fileId) throws IOException {
        response.setHeader("Content-Type", "application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"Download.zip\"");

        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        List<File> fileList = new ArrayList<>();
        for (String id : fileId) {
            if (excelService.getExcelDataById(id) == null) throw new FileException(1, id + " file dosen`t exist");
            fileList.add(new File(id + ".xlsx"));
        }
        try {
            for (Iterator<File> it = fileList.iterator(); it.hasNext(); ) {
                File file = it.next();
                ZipUtils.doCompress(file.getName(), out);
                response.flushBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }


    @GetMapping("/excel/{id}/data")
    @ApiOperation("get metadata of an excel file")
    public ResponseEntity<ExcelResponse> getExcelFileById(@PathVariable String id) {
        ExcelFile excelFile = excelService.getExcelDataById(id);
        if (excelFile == null) throw new FileException(ErrorEnum.FILE_NOT_EXIST);
        return new ResponseEntity<>(new ExcelResponse<>("MateData of " + id, excelFile), HttpStatus.OK);

    }


    @DeleteMapping("/excel/{id}")
    @ApiOperation(value = "delete a Excel file")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        ExcelFile excelFile = excelService.getExcelDataById(id);
        if (excelFile == null) throw new FileException(ErrorEnum.FILE_NOT_EXIST);
        excelService.deleteExcel(id);
        return new ResponseEntity<>(new ExcelResponse<>("Deleted Successfully", excelFile), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("Application Error");
        log.error("Application Error", ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        error.setMessage(message);
        log.error(message, ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity<ErrorResponse> excelExceptionHandler(DataException ex) {
        ErrorResponse error = new ErrorResponse();
        Integer errorCode = ex.getCode();
        error.setErrorCode(errorCode);
        String message = ex.getErrorMessage();
        error.setMessage(ex.getErrorMessage());
        log.error(message, ex);
        if (errorCode > 10 && errorCode <= 20) return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> excelExceptionHandler(FileException ex) {
        ErrorResponse error = new ErrorResponse();
        Integer errorCode = ex.getCode();
        error.setErrorCode(errorCode);
        String message = ex.getErrorMessage();
        error.setMessage(ex.getErrorMessage());
        log.error(message, ex);
        if (errorCode == 1) return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        if (errorCode >= 30 && errorCode < 40) return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
// Log
// Exception handling
// Validation
