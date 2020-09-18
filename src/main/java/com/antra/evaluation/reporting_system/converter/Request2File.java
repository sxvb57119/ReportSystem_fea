package com.antra.evaluation.reporting_system.converter;

import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public class Request2File {
    public static ExcelFile convertRequestToFile(ExcelRequest excelRequest, String id) {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setId(id);
        return excelFile;
    }
}
