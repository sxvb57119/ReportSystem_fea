package com.antra.evaluation.reporting_system.exception;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;

public class DataException extends RuntimeException {

    private Integer code;

    public DataException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());

        this.code = errorEnum.getCode();
    }

    public DataException(Integer code, String message) {
        super(message);

        this.code = code;
    }
}
