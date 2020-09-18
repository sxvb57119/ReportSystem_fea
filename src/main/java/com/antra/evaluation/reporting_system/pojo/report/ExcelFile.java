package com.antra.evaluation.reporting_system.pojo.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelFile {

    private String id;

    private String fileSize;

    private LocalDateTime generatedTime;

    private String downLoadLink;
}