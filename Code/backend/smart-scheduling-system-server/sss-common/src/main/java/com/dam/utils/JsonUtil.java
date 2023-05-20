package com.dam.utils;

public class JsonUtil {

    /**
     * 前后端json统一
     *
     * @param jsonStr
     */
    public static String frontEndAndBackEntJsonEdit(String jsonStr) {
//        System.out.println("修改前：" + jsonStr);
        //因为后端所json格式化的数据和前端json格式化的数据不太一样，因此需要调整一下
        jsonStr = jsonStr.replace("\\", "");
        if (jsonStr.startsWith("\"")) {
            jsonStr = jsonStr.substring(1);
        }
        if (jsonStr.endsWith("\"")) {
            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        }
//        System.out.println("修改后：" + jsonStr);
        return jsonStr;
    }


}
