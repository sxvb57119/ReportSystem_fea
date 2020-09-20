package com.antra.evaluation.reporting_system.pojo.api.request;

import com.antra.evaluation.reporting_system.utility.group.ValidateNoSplitBy;
import com.antra.evaluation.reporting_system.utility.group.ValidateNotNull;
import com.antra.evaluation.reporting_system.utility.group.ValidateFormat;
import com.antra.evaluation.reporting_system.utility.group.ValidateSplitBy;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExcelRequest {

    @NotNull(message = "Headers cannot be null", groups = ValidateNotNull.class)
    @NotEmpty(message = "Headers cannot be null", groups = ValidateNotNull.class)
    private List<String> headers;

    private String description;

    private String splitBy;

    @NotNull(message = "Data cannot be null", groups = ValidateNotNull.class)
    @NotEmpty(message = "Data cannot be empty", groups = ValidateNotNull.class)
    private List<List<String>> data;

    @AssertTrue(message = "Columns of data and headers should be at the same length", groups = ValidateFormat.class)
    public boolean isValidDataFormat() {
        List<String> headers = this.getHeaders();
        if (headers == null) return false;
        for (List<String> dataRow : data) {
            if (dataRow.size() != headers.size()) return false;
        }
        return true;
    }

    @AssertTrue(message = "Single Sheet file cannot be split, try excel/auto", groups = ValidateNoSplitBy.class)
    public boolean isSplitByExist() {
        if (splitBy != null) return false;
        return true;
    }

    @AssertTrue(message = "The field for grouping should be one of fields in headers", groups = ValidateSplitBy.class)
    public boolean isSplitByParaExist() {
        if (splitBy == null || splitBy.length() == 0) return false;
        for (String header : this.getHeaders()) {
            if (header.equals(splitBy)) return true;
        }
        return false;
    }


}
