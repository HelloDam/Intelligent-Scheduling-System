package com.dam.aop;

import com.alibaba.fastjson.JSON;
import com.dam.annotation.OperationLog;
import com.dam.model.entity.system.OperationLogEntity;
import com.dam.service.OperationLogService;
import com.dam.utils.IpUtil;
import com.dam.utils.JwtUtil;
import com.dam.utils.ip.IpAddressUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;


/**
 * 操作日志记录处理
 */
@Aspect//表示要使用AOP这个操作
@Component//交给spring管理
public class OperationLogAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    @Resource
    private OperationLogService operationLogService;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    //在操作方法执行之后执行，pointcut：切入点
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog controllerLog, Object jsonResult) {
        //需要处理的日志
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 处理日志，填充日志信息，并将日志存储到数据库
     * @param joinPoint
     * @param controllerLog
     * @param e
     * @param jsonResult
     */
    protected void handleLog(final JoinPoint joinPoint, OperationLog controllerLog, final Exception e, Object jsonResult) {
        try {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            //获取request之后，请求的内容都可以知道
            HttpServletRequest request = sra.getRequest();

            ////操作日志对象信息存储
            OperationLogEntity operLog = new OperationLogEntity();
            //操作状态
            operLog.setStatus(0);
            //请求的地址
            String ip = IpUtil.getIpAddress(request);
            operLog.setOperIp(ip);
            operLog.setOperUrl(request.getRequestURI());
            //获取ip地址
            operLog.setOperLocation(IpAddressUtils.getRealAddressByIP(ip));
            String token = request.getHeader("token");
            String userName = JwtUtil.getUsername(token);
            operLog.setOperName(userName);
            //设置企业信息
            operLog.setEnterpriseId(Long.parseLong(JwtUtil.getEnterpriseId(token)));
            operLog.setStoreId(Long.parseLong(JwtUtil.getStoreId(token)));
            if (e != null) {
                //异常
                operLog.setStatus(1);
                operLog.setErrorMsg(e.getMessage());
            }
            //获取类名和方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            //设置请求方式
            operLog.setRequestMethod(request.getMethod());
            //处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);

            //将操作数据保存数据库
            operationLogService.save(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, OperationLog log, OperationLogEntity operLog, Object jsonResult) throws Exception {
        //设置action动作
        operLog.setBusinessType(log.businessType().getCode());
        //设置标题
        operLog.setTitle(log.title());
        //设置说明
        operLog.setDetail(log.detail());
        //设置操作人类别
        operLog.setOperatorType(log.operatorType().getCode());
        //是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            //获取参数的信息，传入到数据库中。
            this.setRequestValue(joinPoint, operLog);
        }
        //是否需要保存response，参数和值
        if (log.isSaveResponseData() && !StringUtils.isEmpty(jsonResult)) {
            operLog.setJsonResult(JSON.toJSONString(jsonResult));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, OperationLogEntity operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(params);
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (!StringUtils.isEmpty(o) && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSON.toJSON(o);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}