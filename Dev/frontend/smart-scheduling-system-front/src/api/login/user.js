import request from '@/utils/request'

//常量
const api_name = '/system/login'

export function login(data) {
  return request({
    url: `${api_name}/login`,
    method: 'post',
    data
  })
}

export function regist(data) {
  return request({
    url: `${api_name}/regist`,
    method: 'post',
    data
  })
}

export function enterpriseRegister(data) {
  return request({
    url: `${api_name}/enterpriseRegister`,
    method: 'post',
    data
  })
}

export function generateVerificationCode() {
  return request({
    url: `${api_name}/generateVerificationCode`,
    method: 'get',
  })
}

export function getInfo(token) {
  return request({
    url: `${api_name}/info`,
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: `${api_name}/logout`,
    method: 'post'
  })
}

//查询是否有重复的用户名
export function usernameCheck(username) {
  return request({
    url: `${api_name}/usernameCheck/${username}`,
    method: 'get'
  })
}

/**
 * 发送邮箱验证码
 * @param {*} mail 
 * @param {0：用户注册 1：企业注册} type 
 * @returns 
 */
export function sendMailCode(mail,type) {
  return request({
    url: `${api_name}/sendMailCode`,
    method: 'post',
    data:{mail,type}
  })
}

