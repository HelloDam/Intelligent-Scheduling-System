package com.dam.exception;

import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //指定出现什么异常执行这个方法
    @ExceptionHandler(value = Exception.class)
    @ResponseBody //为了返回数据
    public R error(Exception e) {
        e.printStackTrace();
        return R.error(ResultCodeEnum.FAIL);
    }

    //特定异常
    @ExceptionHandler(value = ArithmeticException.class)
    @ResponseBody //为了返回数据
    public R error(ArithmeticException e) {
        e.printStackTrace();
        return R.error(ResultCodeEnum.FAIL.getCode(),"执行了ArithmeticException异常处理..");
    }

    //自定义异常
    @ExceptionHandler(value = SSSException.class)
    @ResponseBody //为了返回数据
    public R error(SSSException e) {
        log.error(e.getMessage());
        e.printStackTrace();

        return R.error(e.getCode(),e.getMessage());
    }

    // 参数校验异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public R handleValidException(MethodArgumentNotValidException exception){

        Map<String,String> map=new HashMap<>();
        // 获取数据校验的错误结果
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            map.put(field,message);
        });

        log.error("数据校验出现问题{},异常类型{}",exception.getMessage(),exception.getClass());

        // 获取错误信息
        StringBuilder sb = new StringBuilder();
        int id = 1;
        for (String key : map.keySet()) {
            sb.append(id++).append(") ").append(key).append("->").append(map.get(key)).append("<br/>");
        }
        System.out.println(sb.toString());
        return R.error(ResultCodeEnum.ARGUMENT_VALID_ERROR.getCode(),ResultCodeEnum.ARGUMENT_VALID_ERROR.getMessage()+":<br/>"+sb.toString());
    }

}
