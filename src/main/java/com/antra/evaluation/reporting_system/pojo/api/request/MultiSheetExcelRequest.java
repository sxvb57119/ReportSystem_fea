package com.antra.evaluation.reporting_system.pojo.api.request;

import com.antra.evaluation.reporting_system.utility.group.ValidationFirst;
import com.antra.evaluation.reporting_system.utility.group.ValidationSecond;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MultiSheetExcelRequest extends ExcelRequest {

    @NotNull(message = "The field for grouping cannot null", groups = ValidationFirst.class)
    @NotBlank(message = "The field for grouping cannot null", groups = ValidationFirst.class)
    private String splitBy;

    @AssertTrue(message = "The field for grouping should be one of fields in headers", groups = ValidationSecond.class)
    public boolean isSplitByParaExist() {
        if(this.getHeaders() == null) return false;
        for (String header : this.getHeaders()) {
            if(header.equals(splitBy)) return true;
        }
        return false;
    }

}
