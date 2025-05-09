/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.dam.model.exception;

import com.dam.model.enums.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义异常
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class MpcException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    private String msg;
    private int code = 500;
    
    public MpcException(String msg) {
		super(msg);
		this.msg = msg;
	}

	/**
	 * 接收枚举类型对象
	 * @param resultCodeEnum
	 */
	public MpcException(ResultCodeEnum resultCodeEnum) {
		super(resultCodeEnum.getMessage());
		this.code = resultCodeEnum.getCode();
		this.msg = resultCodeEnum.getMessage();
	}
	
	public MpcException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public MpcException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public MpcException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

}
