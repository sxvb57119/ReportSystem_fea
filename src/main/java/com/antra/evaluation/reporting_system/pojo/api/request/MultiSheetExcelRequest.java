package com.antra.evaluation.reporting_system.pojo.api.request;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
public class MultiSheetExcelRequest extends ExcelRequest {


    @NotBlank
    private String splitBy;

    @AssertTrue
    private boolean isSplitByParaExist() {
        for (String header : this.getHeaders()) {
            if(header.equals(splitBy)) return true;
        }
        return false;
    }

}
