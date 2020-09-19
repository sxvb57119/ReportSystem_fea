package com.antra.evaluation.reporting_system.pojo.api.request;

import com.antra.evaluation.reporting_system.utility.group.ValidationFirst;
import com.antra.evaluation.reporting_system.utility.group.ValidationSecond;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExcelRequest {

    @NotNull(message = "Headers cannot be null")
    @NotEmpty(message = "Headers cannot be empty")
    private List<String> headers;


    private String description;

    private String splitBy;

    @NotNull(message = "Data cannot be null", groups = ValidationFirst.class)
    @NotEmpty(message = "Data cannot be empty", groups = ValidationFirst.class)
    private List<List<String>> data;

    @AssertTrue(message = "Columns of data and headers should be at the same length", groups = ValidationSecond.class)
    public boolean isValidDataFormat() {
        List<String> headers = this.getHeaders();
        if (headers == null) return false;
        for (List<String> dataRow : data) {
            if (dataRow.size() != headers.size()) return false;
        }
        return true;
    }

}
