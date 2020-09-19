package com.antra.evaluation.reporting_system.pojo.api.request;



import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.utility.group.ValidationFirst;
import com.antra.evaluation.reporting_system.utility.group.ValidationSecond;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MultiExcelRequest  {

    @Valid
    @JsonProperty("Excels")
    private List<ExcelRequest> excelRequestList = new ArrayList<>();


}

