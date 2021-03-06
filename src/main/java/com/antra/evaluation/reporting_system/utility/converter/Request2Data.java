package com.antra.evaluation.reporting_system.utility.converter;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;
import com.antra.evaluation.reporting_system.exception.DataException;
import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;


import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.enums.ExcelDataType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.GenericValidator;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Request2Data {

    private static final String[] DATE_PATTERNS = {"yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy-MM-dd",
            "dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy,",
            "dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm"};


    public static ExcelData convert2SingleSheetData(ExcelRequest excelRequest, String title, LocalDateTime generatedTime)  {

        ExcelData excelData = new ExcelData();
        excelData.setTitle(title);
        excelData.setGeneratedTime(generatedTime);
        List<String> headers = excelRequest.getHeaders();
        List<List<String>> data = excelRequest.getData();
        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        excelDataSheets.add(convert2Sheet(headers, data));
        excelData.setSheets(excelDataSheets);
        return excelData;
    }

    public static ExcelData convert2MultiSheetData(ExcelRequest excelRequest, String title, LocalDateTime generatedTime)  {

        ExcelData excelData = new ExcelData();
        excelData.setTitle(title);
        excelData.setGeneratedTime(generatedTime);
        List<String> headers = excelRequest.getHeaders();
        List<List<String>> data = excelRequest.getData();
        String splitBy = excelRequest.getSplitBy();
        int splitByIdx = 0;
        while (splitByIdx < headers.size() && !splitBy.equalsIgnoreCase(headers.get(splitByIdx))) splitByIdx++;
        int finalSplitByIdx = splitByIdx;
        Map<String, List<List<String>>> multiSheetMap = data.stream().collect(Collectors.groupingBy(x -> x.get(finalSplitByIdx)));
        List<ExcelDataSheet> excelDataSheetList = new ArrayList<>();
        for (Map.Entry<String, List<List<String>>> E : multiSheetMap.entrySet()) {
            ExcelDataSheet tmpExcelDataSheet = convert2Sheet(headers, E.getValue());
            tmpExcelDataSheet.setTitle(E.getKey());
            excelDataSheetList.add(tmpExcelDataSheet);
        }
        Collections.sort(excelDataSheetList, Comparator.comparing(ExcelDataSheet::getTitle));
        excelData.setSheets(excelDataSheetList);
        return excelData;
    }

    private static ExcelDataSheet convert2Sheet(List<String> headers, List<List<String>> data)  {
        ExcelDataSheet excelDataSheet = new ExcelDataSheet();
        List<ExcelDataHeader> curHeaders = new ArrayList<>();
        List<List<Object>> dataRows = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) dataRows.add(new ArrayList<Object>());
        for (int i = 0; i < headers.size(); i++) {
            ExcelDataHeader curHeader = new ExcelDataHeader();
            String sample = data.get(0).get(i);
            if (NumberUtils.isCreatable(sample)) {
                curHeader.setType(ExcelDataType.NUMBER);
            } else if (isValidDate(sample)) {
                curHeader.setType(ExcelDataType.DATE);
            } else {
                curHeader.setType(ExcelDataType.STRING);
            }
            curHeader.setName(headers.get(i));
            //curHeader.setWidth(0);
            curHeaders.add(curHeader);
            switch (curHeader.getType()) {

                case NUMBER:
                    for (int j = 0; j < data.size(); j++) {
                        int finalI1 = i;
                        List<String> numCol = data.stream().map(x -> x.get(finalI1)).collect(Collectors.toList());
                        try {
                            dataRows.get(j).add(NumberUtils.createNumber(numCol.get(j)));
                        } catch (NumberFormatException e) {
                            throw new DataException(ErrorEnum.PARAM_ERROR);
                        }

                    }
                    break;
                case STRING:
                    int finalI1 = i;
                    List<String> col = data.stream().map(x -> x.get(finalI1)).collect(Collectors.toList());
                    for (int j = 0; j < data.size(); j++) {
                        String curElement = col.get(j);
                        try {
                            DateUtils.parseDate(curElement, DATE_PATTERNS);
                        } catch (ParseException | IllegalArgumentException e) {
                            if(NumberUtils.isCreatable(curElement)) throw new DataException(ErrorEnum.PARAM_ERROR);
                            dataRows.get(j).add(curElement);
                            continue;

                        }
                        throw new DataException(ErrorEnum.PARAM_ERROR);
                    }
                    break;
                case DATE:
                    for (int j = 0; j < data.size(); j++) {
                        List<Object> curList = dataRows.get(j);
                        List<String> originList = data.get(j);
                        try {
                            curList.add(DateUtils.parseDate(originList.get(i), DATE_PATTERNS));
                        } catch (ParseException e) {
                            throw new DataException(ErrorEnum.PARAM_ERROR);
                        }

                    }
                    break;

            }

        }
        excelDataSheet.setHeaders(curHeaders);
        excelDataSheet.setDataRows(dataRows);
        excelDataSheet.setTitle("sheet1");
        return excelDataSheet;
    }

    private static boolean isValidDate(String header) {
        for (String dateFormat : DATE_PATTERNS) {
            if (GenericValidator.isDate(header, dateFormat, true)) return true;
        }
        return false;
    }


}
