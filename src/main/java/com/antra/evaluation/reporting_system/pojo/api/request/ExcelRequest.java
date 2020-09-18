package com.antra.evaluation.reporting_system.pojo.api.request;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ExcelRequest {

    @NotEmpty
    private List<String> headers;

    @NotBlank
    private String description;

    @NotEmpty
    private List<List<String>> data;

    @AssertTrue
    private boolean isValidDataFormat() {
        int headerSize = headers.size();
        for (List<String> dataRow : data) {
            if (dataRow.size() != headerSize) return false;
        }
        return true;
    }

}
