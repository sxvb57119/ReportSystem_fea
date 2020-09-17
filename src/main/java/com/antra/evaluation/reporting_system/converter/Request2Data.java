package com.antra.evaluation.reporting_system.converter;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;


import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Request2Data {

    private static final String[] PARSE_PATTERNS = {"yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};



    public static ExcelData convert2SingleSheetData(ExcelRequest excelRequest, String title) {

        ExcelData excelData = new ExcelData();
        excelData.setTitle(title);
        excelData.setGeneratedTime(LocalDateTime.now());
        List<String> headers = excelRequest.getHeaders();
        List<List<String>> data = excelRequest.getData();
        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        excelDataSheets.add(convert2Sheet(headers, data));
        excelData.setSheets(excelDataSheets);
        return excelData;
    }

    public static ExcelData convert2MultiSheetData(MultiSheetExcelRequest multiSheetExcelRequest, String title) {
        ExcelData excelData = new ExcelData();
        excelData.setTitle(title);
        excelData.setGeneratedTime(LocalDateTime.now());
        List<String> headers = multiSheetExcelRequest.getHeaders();
        List<List<String>> data = multiSheetExcelRequest.getData();
        String splitBy = multiSheetExcelRequest.getSplitBy();
        int splitByIdx = 0;
        while(splitByIdx < headers.size() && !splitBy.equalsIgnoreCase(headers.get(splitByIdx))) splitByIdx++;
        int finalSplitByIdx = splitByIdx;
        Map<String, List<List<String>>> multiSheetMap = data.stream().collect(Collectors.groupingBy(x -> x.get(finalSplitByIdx)));
        List<ExcelDataSheet> excelDataSheetList = new ArrayList<>();
        for(Map.Entry<String, List<List<String>>> E : multiSheetMap.entrySet()) {
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
        List<ExcelDataHeader> curHeaders =  new ArrayList<>();
        List<List<Object>> dataRows = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) dataRows.add(new ArrayList<Object>());
        for(int i = 0; i < headers.size(); i++) {
            ExcelDataHeader curHeader = new ExcelDataHeader();
            String sample = data.get(0).get(i);
            if( NumberUtils.isCreatable(sample)) {
                curHeader.setType(ExcelDataType.NUMBER);
            } else if(isValidDate(sample)) {
                curHeader.setType(ExcelDataType.DATE);
            } else {
                curHeader.setType(ExcelDataType.STRING);
            }
            curHeader.setName(headers.get(i));
            //curHeader.setWidth(0);
            curHeaders.add(curHeader);
            switch (curHeader.getType()) {

                case NUMBER:
                    for(int j = 0; j < data.size(); j++) {
                        int finalI1 = i;
                        dataRows.get(j).add(NumberUtils.createNumber(data.stream().map(x -> x.get(finalI1)).collect(Collectors.toList()).get(j)));
                    }
                    break;
                case STRING:
                    for(int j = 0; j < data.size(); j++) {
                        int finalI1 = i;
                        dataRows.get(j).add(data.stream().map(x -> x.get(finalI1)).collect(Collectors.toList()).get(j));
                    }
                    break;
                case DATE:
                    for(int j = 0; j < data.size(); j++) {
                         List<Object> curList = dataRows.get(j);
                         List<String> originList = data.get(j);
                         try {
                             curList.add(DateUtils.parseDate(originList.get(i), PARSE_PATTERNS));
                         } catch (ParseException e) {
                             throw new RuntimeException();
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
        for(String dateFormat : PARSE_PATTERNS) {
            if(GenericValidator.isDate(header, dateFormat, true)) return true;
        }
        return false;
    }

}
