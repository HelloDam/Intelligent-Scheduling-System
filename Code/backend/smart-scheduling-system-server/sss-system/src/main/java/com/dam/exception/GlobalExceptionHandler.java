package com.dam.exception;

import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.AccessDeniedException;

public class GlobalExceptionHandler {

    /**
     * 自定义权限不足的异常
     * @param e
     * @return
     * @throws AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public R error(AccessDeniedException e) throws AccessDeniedException {
        return R.error(ResultCodeEnum.PERMISSION);
    }

}
