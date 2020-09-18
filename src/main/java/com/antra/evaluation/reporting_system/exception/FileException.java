package com.antra.evaluation.reporting_system.exception;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;


public class FileException extends RuntimeException {

    private Integer code;

    public FileException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());

        this.code = errorEnum.getCode();
    }

    public FileException(Integer code, String message) {
        super(message);

        this.code = code;
    }
}
