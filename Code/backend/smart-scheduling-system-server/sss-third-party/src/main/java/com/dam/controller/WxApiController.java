package com.dam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dam.config.wechatLogin.ConstantUtils;
import com.dam.config.wechatLogin.HttpClientUtils;
import com.dam.constant.RedisConstant;
import com.dam.exception.SSSException;
import com.dam.feign.SystemFeignService;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.utils.JwtUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CrossOrigin
//@RestController//创建对象，交给Spring管理，做数据返回，因为要重定向，不需要返回数据，这个注解不可以用
@Controller//只是请求地址，不需要返回数据
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 小程序微信登录
     *
     * @return
     */
    @GetMapping("appletWxLogin")
    public R appletWxLogin(@RequestParam("openid") String openid) throws SSSException {
        //查询数据库当前用用户是否曾经使用过微信登录
        //判断数据库中是否有相同的微信信息，根据openid判断
        R r = systemFeignService.getByOpenid(openid);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            UserEntity user = r.getData("user", new TypeReference<UserEntity>() {
            });
            if (user == null) {
                return R.error(ResultCodeEnum.FAIL.getCode(), "微信没有绑定系统账号");
            } else {
                ///登录
                //使用jwt根据menber对象生成token字符串
                String token = JwtUtil.createToken(user.getId(), user.getUsername(), user.getEnterpriseId(), user.getStoreId(), user.getType());
                ///调用远程服务，查询权限数据存储到redis中
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userId", user.getId());
                paramMap.put("username", user.getUsername());
                R r1 = systemFeignService.storeAuthoritiesToRedis(paramMap);
                if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    //返回首页面，通过路径传递token字符串
//                    return "redirect:http://localhost:6090/#/login?token=" + token;
                    return R.ok().addData("token", token);
                } else {
                    throw new SSSException(ResultCodeEnum.Feign_ERROR.getCode(), "调用远程服务存储权限到redis中失败");
                }
            }
        } else {
            throw new SSSException(ResultCodeEnum.Feign_ERROR.getCode(), "查询系统用户信息失败");
        }
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) throws SSSException {
//        System.out.println("token:" + session.getAttribute("token"));
//        System.out.println("session:"+session);
        String token = session.getAttribute("token") != null ? session.getAttribute("token").toString() : null;

        ////获取 accessToken 和 openid
        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantUtils.WX_OPEN_APP_ID,
                ConstantUtils.WX_OPEN_APP_SECRET,
                code);
        String result = null;
        try {
            //拼接好地址之后，用HttpClientUtils来发送请求（模拟浏览器请求），得到返回结果
            result = HttpClientUtils.get(accessTokenUrl);
//            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "获取access_token失败");

        }
        //解析json字符串
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String) map.get("access_token");
        String openid = (String) map.get("openid");
        System.out.println();

        if (!StringUtils.isEmpty(token)) {
            //--if--携带了token，说明是绑定微信
            //获取微信用户信息的路径
            String wechatInfoPath = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=en";
            try {
                String wxUserInfoJson = HttpClientUtils.get(wechatInfoPath);
                JSONObject wxUserInfoObject = JSON.parseObject(wxUserInfoJson);
                String nickname = wxUserInfoObject.get("nickname").toString();
                String headimgurl = wxUserInfoObject.get("headimgurl").toString().replace("\\", "");
                System.out.println("nickname:" + nickname);
                System.out.println("headimgurl:" + headimgurl);
                System.out.println("token:" + token);
                R r = systemFeignService.getUserEntityByToken(token);
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    System.out.println("r:" + r);
                    UserEntity user = r.getData("user", new TypeReference<UserEntity>() {
                    });
                    user.setWechatName(nickname);
                    user.setWechatAvatar(headimgurl);
                    user.setOpenid(openid);
                    R update = systemFeignService.bindWechat(user);
                    if (update.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                        return "redirect:http://localhost:6090/#/user/userCenter";
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "redirect:http://localhost:6090/#/user/userCenter";
        } else {
            //--if--没有携带token，说明是进行登录，获取用户信息
            //查询数据库当前用用户是否曾经使用过微信登录
            //判断数据库中是否有相同的微信信息，根据openid判断
            R r = systemFeignService.getByOpenid(openid);
            if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                UserEntity user = r.getData("user", new TypeReference<UserEntity>() {
                });
                if (user == null) {
                    String mes = "UnBound";
                    return "redirect:http://localhost:6090/#/login?message=" + mes;
                } else {
                    ///登录
                    //使用jwt根据menber对象生成token字符串
                    token = JwtUtil.createToken(user.getId(), user.getUsername(), user.getEnterpriseId(), user.getStoreId(), user.getType());
                    ///调用远程服务，查询权限数据存储到redis中
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("userId", user.getId());
                    paramMap.put("username", user.getUsername());
                    R r1 = systemFeignService.storeAuthoritiesToRedis(paramMap);
                    if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                        //返回首页面，通过路径传递token字符串
                        return "redirect:http://localhost:6090/#/login?token=" + token;
                    } else {
                        throw new SSSException(ResultCodeEnum.Feign_ERROR.getCode(), "调用远程服务存储权限到redis中失败");
                    }
                }
            } else {
                throw new SSSException(ResultCodeEnum.Feign_ERROR.getCode(), "查询系统用户信息失败");
            }
        }

    }

    @GetMapping("login")
    public String genQrConnect(HttpSession session, @RequestParam(value = "token", required = false) String token) throws SSSException {

        System.out.println("genQrConnect token:" + token);
        //微信开放平台授权baseUrl，后面拼接参数 %s：占位符，表示里面要传参数
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //获取业务服务器重定向地址
        String redirectUrl = ConstantUtils.WX_OPEN_REDIRECT_URL;
        try {
            //url编码
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SSSException(ResultCodeEnum.FAIL);
        }

        String state = UUID.randomUUID().toString().replaceAll("-", "");
        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantUtils.WX_OPEN_APP_ID,
                redirectUrl,
                state);

        //如果请求带有token，token不为空，说明是绑定用户
        session.setAttribute("token", token);

        //重定向到微信扫码地址
        return "redirect:" + qrcodeUrl;
    }

}
