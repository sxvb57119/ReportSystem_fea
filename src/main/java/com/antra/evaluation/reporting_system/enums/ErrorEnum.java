package com.antra.evaluation.reporting_system.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    FILE_ID_NOT_EXIST(1, "This file Id doesn`t exist"),

    FILE_NOT_EXIST(2, "This file doesnt`t exist"),

    PARAM_ERROR(11, "error with parameter type"),

    NO_SHEET_DEFINED(22, "Excel Data Error: no sheet is defined"),

    NO_SHEET_NAME( 23, "Excel Data Error: sheet name is missing"),

    ERROR_WITH_DATA_LENGTH(24,"Excel Data Error: sheet data has difference length than header number")



    ;


    private Integer code;

    private String message;

    ErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
