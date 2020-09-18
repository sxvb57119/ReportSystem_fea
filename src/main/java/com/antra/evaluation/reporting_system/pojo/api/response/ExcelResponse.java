package com.antra.evaluation.reporting_system.pojo.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelResponse<T> {
    private String message;
    private T body;

}
