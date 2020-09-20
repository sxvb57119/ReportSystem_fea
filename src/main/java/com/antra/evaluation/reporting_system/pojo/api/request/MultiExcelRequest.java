package com.antra.evaluation.reporting_system.pojo.api.request;



import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class MultiExcelRequest  {
    @NotNull(message = "requests cannot be null or empty")
    @NotEmpty(message = "requests cannot be null or empty")
    @Valid
    private List<ExcelRequest> excels = new ArrayList<>();


}

