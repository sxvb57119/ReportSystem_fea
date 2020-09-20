package com.antra.evaluation.reporting_system.exception;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;

public  abstract class ExcelException extends RuntimeException {

    private Integer code;

    private String errorMessage;

    public Integer getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ExcelException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorMessage = errorEnum.getMessage();
        this.code = errorEnum.getCode();
    }

    public ExcelException(Integer code, String message) {
        super(message);
        this.errorMessage  = message;
        this.code = code;
    }
}
