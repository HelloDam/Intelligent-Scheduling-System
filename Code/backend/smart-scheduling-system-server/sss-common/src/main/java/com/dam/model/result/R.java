/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.dam.model.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dam.model.enums.ResultCodeEnum;
import lombok.Data;

import java.util.HashMap;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
    }

    public Integer getCode() {
        return (int) (this.get("code"));
    }

    public static R ok() {
        R r = new R();
        r.put("code", ResultCodeEnum.SUCCESS.getCode());
        r.put("message", "success");
        return r;
    }

    public R addData(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("message", msg);
        return r;
    }

    public static R error(ResultCodeEnum resultCodeEnum) {
        R r = new R();
        r.put("code", resultCodeEnum.getCode());
        r.put("message", resultCodeEnum.getMessage());
        return r;
    }


    /**
     * 使用fastjson工具来转化数据类型
     *
     * @param typeReference
     * @param <T>
     * @return
     */
    public <T> T getData(String key, TypeReference<T> typeReference) {
        Object data = this.get(key);
        String s = JSON.toJSONString(data);
        T t = JSON.parseObject(s, typeReference);
        return t;
    }

}
