package com.dam.controller;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.constant.RedisConstant;
import com.dam.feign.SystemFeignService;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.EnterpriseVo;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.vo.EnterpriseRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dam.service.EnterpriseService;

import javax.servlet.http.HttpServletRequest;


/**
 * 企业表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-13 14:49:17
 */
@RestController
@RequestMapping("enterprise/enterprise")
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String title = "企业管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.enterprise.list')")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = enterpriseService.queryPage(params);

        return R.ok().addData("page", page);
    }

    /**
     * 获取系统的企业数量
     *
     * @return int count
     */
    @RequestMapping("/getAllEnterpriseNum")
    public R getAllEnterpriseNum() {
        int count = enterpriseService.count(new QueryWrapper<EnterpriseEntity>().eq("is_deleted", 0));
        return R.ok().addData("count", count);
    }

    /**
     * 列表
     */
    @RequestMapping("/listEnterpriseVo")
    @PreAuthorize("hasAuthority('bnt.enterprise.list')")
    public R listEnterpriseVo(@RequestParam Map<String, Object> params) {
        PageUtils page = enterpriseService.queryPage(params);
        List<EnterpriseEntity> enterpriseEntityList = (List<EnterpriseEntity>) page.getList();
        List<EnterpriseVo> enterpriseVoList=enterpriseService.buildEnterpriseVoList(enterpriseEntityList);
        page.setList(enterpriseVoList);
        return R.ok().addData("page", page);
    }

    /**
     * 从redis中获取企业的注册信息
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/listAllEnterpriseRegisterVoFromRedis")
    public R listAllEnterpriseRegisterVoFromRedis(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        long userId = Long.parseLong(JwtUtil.getUserId(token));
        List<EnterpriseRegisterVo> registerVoList = new ArrayList<>();
        R r = systemFeignService.getUserById(userId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            UserEntity user = r.getData("user", new TypeReference<UserEntity>() {
            });
            if (user.getType() == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
                //从redis中获取企业注册信息
//                Set<String> registerVoJSONSet = redisTemplate.boundSetOps(RedisConstant.ENTERPRISE_REGISTER).members();
                List registerVoJSONSet = redisTemplate.boundHashOps(RedisConstant.ENTERPRISE_REGISTER).values();
                for (Object json : registerVoJSONSet) {
                    registerVoList.add(JSON.parseObject(json.toString(), EnterpriseRegisterVo.class));
                }
            }
        }
        return R.ok().addData("registerVoList", registerVoList);
    }

    /**
     * 通过企业的注册申请
     *
     * @param enterpriseRegisterVo
     * @return
     */
    @PostMapping("/passEnterpriseRegister")
    @OperationLog(title = EnterpriseController.title, businessType = BusinessTypeEnum.PASS, detail = "通过企业注册")
    public R passEnterpriseRegister(@RequestBody EnterpriseRegisterVo enterpriseRegisterVo) {
        Boolean isSuccess = enterpriseService.passEnterpriseRegister(enterpriseRegisterVo);
        if (isSuccess == true) {
            //删除已经通过注册的企业
            redisTemplate.boundHashOps(RedisConstant.ENTERPRISE_REGISTER).delete(enterpriseRegisterVo.getUuid());
            return R.ok();
        } else {
            return R.error(ResultCodeEnum.FAIL.getCode(), "企业注册通过失败");
        }
    }

    /**
     * 拒绝企业的注册申请
     *
     * @param enterpriseRegisterVo
     * @return
     */
    @PostMapping("/rejectEnterpriseRegister")
    @OperationLog(title = EnterpriseController.title, businessType = BusinessTypeEnum.REJECT, detail = "拒绝企业注册")
    public R rejectEnterpriseRegister(@RequestBody EnterpriseRegisterVo enterpriseRegisterVo) {
        Boolean isSuccess = enterpriseService.rejectEnterpriseRegister(enterpriseRegisterVo);
        if (isSuccess == true) {
            //删除已经拒绝注册的企业
            redisTemplate.boundHashOps(RedisConstant.ENTERPRISE_REGISTER).delete(enterpriseRegisterVo.getUuid());
            return R.ok();
        } else {
            return R.error(ResultCodeEnum.FAIL.getCode(), "企业注册拒绝失败");
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/listAll")
    @PreAuthorize("hasAuthority('bnt.enterprise.list')")
    public R listAll() {
        List<EnterpriseEntity> list = enterpriseService.list();

        return R.ok().addData("list", list);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //用户需要查看自己的企业信息（分配权限的时候分好也行，这里为了方便直接不拦截了）
//    @PreAuthorize("hasAuthority('bnt.enterprise.list')")
    public R info(@PathVariable("id") Long id) {
        EnterpriseEntity enterprise = enterpriseService.getById(id);

        return R.ok().addData("enterprise", enterprise);
    }

    /**
     * 根据企业id集合查询企业，并封装成字典
     *
     * @return
     */
    @PostMapping("/getEnterpriseMapByIdList")
    public R getEnterpriseMapByIdList(@RequestBody List<Long> enterpriseIdList) {
        Map<Long, EnterpriseEntity> idAndEnterpriseEntityMap = new HashMap<>();
        if (enterpriseIdList.size() > 0) {
            List<EnterpriseEntity> enterpriseEntityList = enterpriseService.list(new QueryWrapper<EnterpriseEntity>().in("id", enterpriseIdList).eq("is_deleted", 0));
            for (EnterpriseEntity enterpriseEntity : enterpriseEntityList) {
                idAndEnterpriseEntityMap.put(enterpriseEntity.getId(), enterpriseEntity);
            }
        }
        return R.ok().addData("idAndEnterpriseEntityMap", idAndEnterpriseEntityMap);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @OperationLog(title = EnterpriseController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增企业")
    @PreAuthorize("hasAuthority('bnt.enterprise.add')")
    public R save(@RequestBody EnterpriseEntity enterprise) {
        enterpriseService.save(enterprise);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @OperationLog(title = EnterpriseController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改企业")
    @PreAuthorize("hasAuthority('bnt.enterprise.update')")
    public R update(@RequestBody EnterpriseEntity enterprise) {
        enterpriseService.updateById(enterprise);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @OperationLog(title = EnterpriseController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除企业")
    @PreAuthorize("hasAuthority('bnt.enterprise.delete')")
    public R delete(@RequestBody Long[] ids) {
        enterpriseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
