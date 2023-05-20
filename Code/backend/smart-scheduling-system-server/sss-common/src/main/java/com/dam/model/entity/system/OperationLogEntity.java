package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 操作日志表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@Data
@TableName("operation_log")
public class OperationLogEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 模块标题
	 */
	private String title;
	/**
	 * 业务类型 (0其它 1新增 2修改 3删除)
	 */
	private Integer businessType;
	/**
	 * 方法名称
	 */
	private String method;
	/**
	 * 说明
	 */
	private String detail;
	/**
	 * 请求方式
	 */
	private String requestMethod;
	/**
	 * 操作类别(0其它 1后台用户 2手机端用户)
	 */
	private Integer operatorType;
	/**
	 * 操作人员
	 */
	private String operName;
	/**
	 * 企业id
	 */
	private Long enterpriseId;
	/**
	 * 门店名称
	 */
	private Long storeId;
	/**
	 * 请求URL
	 */
	private String operUrl;
	/**
	 * 主机地址
	 */
	private String operIp;
	/**
	 * 操作地点
	 */
	private String operLocation;
	/**
	 * 请求参数
	 */
	private String operParam;
	/**
	 * 返回参数
	 */
	private String jsonResult;
	/**
	 * 操作状态 (0正常 1异常)
	 */
	private Integer status;
	/**
	 * 错误消息
	 */
	private String errorMsg;

}
