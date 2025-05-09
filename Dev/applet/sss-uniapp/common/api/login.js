//常量
const api_name = '/system/login'
import $http from '@/common/utils/request.js';

export default {
	// 登录
	login(data) {
		console.log("进来，"+JSON.stringify(data))
	  return $http.request({
	    url: `${api_name}/login`,
	    method: 'post',
	    data
	  })
	},
	// 生成验证码
	generateVerificationCode() {
	  return $http.request({
	    url: `${api_name}/generateVerificationCode`,
	    method: 'get',
	  })
	},
	// 注册
	regist(data) {
	  return $http.request({
	    url: `${api_name}/regist`,
	    method: 'post',
	    data
	  })
	},
	// 企业注册
	enterpriseRegister(data) {
	  return $http.request({
	    url: `${api_name}/enterpriseRegister`,
	    method: 'post',
	    data
	  })
	},
	// 获取用户信息
	getInfo(token) {
	  return $http.request({
	    url: `${api_name}/info`,
	    method: 'get',
	    params: { token }
	  })
	},
	// 登出
	logout() {
	  return $http.request({
	    url: `${api_name}/logout`,
	    method: 'post'
	  })
	},
	// 查询是否有重复的用户名
	usernameCheck(username) {
	  return $http.request({
	    url: `${api_name}/usernameCheck/${username}`,
	    method: 'get'
	  })
	},
	/**
	 * 发送邮箱验证码
	 * @param {Object} mail
	 * @param {0：用户注册 1：企业注册} type
	 */
	sendMailCode(mail,type) {
	  return request({
	    url: `${api_name}/sendMailCode`,
	    method: 'post',
	    data:{mail,type}
	  })
	}
	
}


