package com.antra.evaluation.reporting_system.exception;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;


public class FileException extends ExcelException {
    public FileException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public FileException(Integer code, String message) {
        super(code, message);
    }
}
