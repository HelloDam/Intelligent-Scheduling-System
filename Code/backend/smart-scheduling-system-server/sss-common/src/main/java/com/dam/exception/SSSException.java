package com.dam.exception;

import com.dam.model.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class SSSException extends RuntimeException {

    private Integer code;
    private String message;

    public SSSException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SSSException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }
}
