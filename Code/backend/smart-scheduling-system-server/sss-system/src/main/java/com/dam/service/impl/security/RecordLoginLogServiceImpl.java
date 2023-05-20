package com.dam.service.impl.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dam.dao.LoginLogDao;
import com.dam.model.entity.system.LoginLogEntity;
import com.dam.model.vo.system.LoginLogQueryVo;
import com.dam.service.RecordLoginLogService;
import com.dam.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

//@Service("systemRecordLoginLogServiceImpl")
@Service
public class RecordLoginLogServiceImpl implements RecordLoginLogService {

    @Autowired
    private LoginLogDao loginLogDao;

    @Override
    public void recordLoginLog(String username, Integer status, String ipaddr, String message, Long enterpriseId, Long storeId) {


        LoginLogEntity sysLoginLog = new LoginLogEntity();
        sysLoginLog.setUsername(username);
        sysLoginLog.setIpaddr(ipaddr);
        sysLoginLog.setMsg(message);
        // 日志状态
        sysLoginLog.setStatus(status);

        ///获取用户的浏览器和操作系统
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        sysLoginLog.setOs(os);
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        sysLoginLog.setBrowser(browser);

        ///存储用户的企业 门店信息
        if (enterpriseId != null) {
            sysLoginLog.setEnterpriseId(enterpriseId);
        }
        if (storeId != null) {
            sysLoginLog.setStoreId(storeId);
        }

        loginLogDao.insert(sysLoginLog);
    }

    @Override
    public IPage<LoginLogEntity> selectPage(Page<LoginLogEntity> pageParam, LoginLogQueryVo sysLoginLogQueryVo) {
        //获取条件值
        String username = sysLoginLogQueryVo.getUsername();
        String createTimeBegin = sysLoginLogQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysLoginLogQueryVo.getCreateTimeEnd();
        //封装条件
        QueryWrapper<LoginLogEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            wrapper.like("username", username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.le("create_time", createTimeEnd);
        }
        //调用mapper方法
        IPage<LoginLogEntity> pageModel = loginLogDao.selectPage(pageParam, wrapper);
        return pageModel;
    }

    @Override
    public LoginLogEntity getById(Long id) {
        return loginLogDao.selectById(id);
    }

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<LoginLogEntity> page = this.page(
//                new Query<LoginLogEntity>().getPage(params),
//                new QueryWrapper<LoginLogEntity>().orderByDesc("create_time")
//        );
//
//        return new PageUtils(page);
//    }
}
