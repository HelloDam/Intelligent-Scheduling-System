package com.dam.enums;

public enum WebSocketEnum {

    CalculateEnd(0,"计算完成"),
    AllAlgorithmCalculateEnd(1,"全算法计算完成")
    ;

    /**
     * 信息类型
     */
    private Integer type;
    /**
     * 信息名称
     */
    private String name;

    WebSocketEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
