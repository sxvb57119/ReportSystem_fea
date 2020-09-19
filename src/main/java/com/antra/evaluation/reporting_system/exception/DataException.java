package com.antra.evaluation.reporting_system.exception;

import com.antra.evaluation.reporting_system.enums.ErrorEnum;

public class DataException extends ExcelException {

    public DataException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public DataException(Integer code, String message) {
        super(code, message);
    }
}
