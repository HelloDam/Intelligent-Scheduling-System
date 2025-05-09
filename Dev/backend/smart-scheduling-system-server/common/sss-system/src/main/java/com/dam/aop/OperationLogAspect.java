package com.dam.aop;

import com.alibaba.fastjson.JSON;
import com.dam.annotation.OperationLog;
import com.dam.model.entity.system.OperationLogEntity;
import com.dam.service.OperationLogService;
import com.dam.utils.JwtUtil;
import com.dam.utils.ip.IpAddressUtils;
import com.dam.utils.ip.IpUtil;
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
// 使用 AOP（面向切面编程）技术实现操作日志记录
@Aspect
// 将该类作为 Spring 管理的 Bean
@Component
public class OperationLogAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    /**
     * 注入 OperationLogService 依赖，用于持久化操作日志到数据库
     */
    @Resource
    private OperationLogService operationLogService;

    /**
     * 处理完请求后执行
     * 在带有 @OperationLog 注解的方法执行成功并返回后触发
     * pointcut：切入点
     *
     * @param joinPoint     切点
     * @param controllerLog 注解实例
     * @param jsonResult    返回结果对象（如果有的话）
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog controllerLog, Object jsonResult) {
        // 调用通用日志处理方法，记录正常操作日志
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * 在带有 @OperationLog 注解的方法抛出异常时触发
     *
     * @param joinPoint     切点
     * @param controllerLog 注解实例
     * @param e             异常对象
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog controllerLog, Exception e) {
        // 调用通用日志处理方法，记录异常操作日志
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 处理日志，填充日志信息，并将日志存储到数据库
     *
     * @param joinPoint     切点信息
     * @param controllerLog 操作日志注解实例
     * @param e             异常对象（如果有的话）
     * @param jsonResult    返回结果对象（如果有的话）
     */
    protected void handleLog(final JoinPoint joinPoint, OperationLog controllerLog, final Exception e, Object jsonResult) {
        try {
            //// 根据请求上下文获取 HttpServletRequest 相关信息
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            // 获取request之后，请求的内容都可以知道
            HttpServletRequest request = sra.getRequest();

            //// 操作日志对象信息存储
            OperationLogEntity operationLog = new OperationLogEntity();
            // 操作状态
            operationLog.setStatus(0);
            // 请求的地址
            String ip = IpUtil.getIpAddress(request);
            operationLog.setOperIp(ip);
            // 设置请求的 URL
            operationLog.setOperUrl(request.getRequestURI());
            // 设置请求的地理位置信息（根据 IP 解析）
            operationLog.setOperLocation(IpAddressUtils.getRealAddressByIP(ip));
            // 从请求头中提取 JWT 令牌，获取用户名和企业、店铺 ID
            String token = request.getHeader("token");
            String userName = JwtUtil.getUsername(token);
            operationLog.setOperName(userName);
            // 设置企业信息
            operationLog.setEnterpriseId(Long.parseLong(JwtUtil.getEnterpriseId(token)));
            // 设置门店信息
            operationLog.setStoreId(Long.parseLong(JwtUtil.getStoreId(token)));

            // 如果存在异常，更新操作状态并记录异常信息
            if (e != null) {
                // 异常
                operationLog.setStatus(1);
                operationLog.setErrorMsg(e.getMessage());
            }

            // 获取类名和方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operationLog.setMethod(className + "." + methodName + "()");

            // 设置请求方法（GET、POST、PUT 等）
            operationLog.setRequestMethod(request.getMethod());

            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operationLog, jsonResult);

            // 将操作数据保存数据库
            operationLogService.save(operationLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log          日志
     * @param operationLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, OperationLog log, OperationLogEntity operationLog, Object jsonResult) throws Exception {
        // 设置业务类型（从注解中获取）
        operationLog.setBusinessType(log.businessType().getCode());
        // 设置操作标题（从注解中获取）
        operationLog.setTitle(log.title());
        // 设置操作详情（从注解中获取）
        operationLog.setDetail(log.detail());
        // 设置操作人类型（从注解中获取）
        operationLog.setOperatorType(log.operatorType().getCode());
        // 如果需要保存请求数据，提取并设置请求参数
        if (log.isSaveRequestData()) {
            this.setRequestValue(joinPoint, operationLog);
        }
        // 如果需要保存响应数据且有返回结果，将其序列化并设置到日志中
        if (log.isSaveResponseData() && !StringUtils.isEmpty(jsonResult)) {
            operationLog.setJsonResult(JSON.toJSONString(jsonResult));
        }
    }

    /**
     * 获取请求的参数，放到操作日志中
     *
     * @param joinPoint    切点信息
     * @param operationLog 操作日志实体对象
     */
    private void setRequestValue(JoinPoint joinPoint, OperationLogEntity operationLog) {
        String requestMethod = operationLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operationLog.setOperParam(params);
        }
    }

    /**
     * 将方法参数数组转化为字符串表示形式
     *
     * @param paramArr 方法参数数组
     * @return 参数字符串
     */
    private String argsArrayToString(Object[] paramArr) {
        String params = "";
        if (paramArr != null && paramArr.length > 0) {
            for (Object param : paramArr) {
                // 每个param包含了 健值
                if (!StringUtils.isEmpty(param) && !isFilterObject(param)) {
                    try {
                        Object jsonObj = JSON.toJSON(param);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象
     *
     * @param o 对象信息
     * @return 如果是需要过滤的对象，则返回true；否则返回false
     */
//    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            // 如果是数组，检查其组件类型是否为 MultipartFile
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            // 如果是集合，检查其中是否有 MultipartFile 类型的元素
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            // 如果是 Map，检查其中是否有 MultipartFile 类型的值
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        // 检查对象是否为 MultipartFile、HttpServletRequest、HttpServletResponse 或 BindingResult 类型
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}