package com.dam.model.enums.log;

/**
 * 业务操作类型
 */
public enum BusinessTypeEnum {
    /**
     * 其它
     */
    OTHER(0,"其他"),

    /**
     * 新增
     */
    INSERT(1,"新增"),

    /**
     * 修改
     */
    UPDATE(2,"修改"),

    /**
     * 删除
     */
    DELETE(3,"删除"),

    /**
     * 授权
     */
    ASSGIN(4,"授权"),

    /**
     * 导出
     */
    EXPORT(5,"导出"),

    /**
     * 导入
     */
    IMPORT(6,"导入"),

    /**
     * 强退
     */
    FORCE(7,"强退"),

    /**
     * 更新状态
     */
    STATUS(8,"更新状态"),

    /**
     * 清空数据
     */
    CLEAN(9,"清空数据"),
    PASS(10,"通过"),
    REJECT(11,"拒绝"),
    ;

    private Integer code;
    private String name;

    BusinessTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}