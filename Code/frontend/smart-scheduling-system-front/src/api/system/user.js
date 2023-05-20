import request from '@/utils/request'

//常量
const api_name = '/system/user'

export default {
    //列表
    getPageList(page, limit, searchObj) {
        return request({
            //接口路径
            url: `${api_name}/${page}/${limit}`,
            method: 'post', //提交方式
            //参数
            data: searchObj
        })
    },
    //添加
    save(user) {
        return request({
            //接口路径
            url: `${api_name}/save`,
            method: 'post', //提交方式
            //参数
            data: user
        })
    },
    //根据id查询
    getUserId(id) {
        return request({
            //接口路径
            url: `${api_name}/info/${id}`,
            method: 'get' //提交方式
        })
    },
    userInfoVo(id) {
        return request({
            //接口路径
            url: `${api_name}/userInfoVo`,
            method: 'get', //提交方式
            params: { id }
        })
    },
    //修改
    update(user) {
        return request({
            //接口路径
            url: `${api_name}/update`,
            method: 'post', //提交方式
            //参数
            data: user
        })
    },
    //删除
    removeById(id) {
        return request({
            //接口路径
            url: `${api_name}/delete/${id}`,
            method: 'delete' //提交方式
        })
    },
    //批量删除
    deleteBatch(idArr) {
        console.log("idArr:" + JSON.stringify(idArr))
        return request({
            //接口路径
            url: `${api_name}/deleteBatch`,
            method: 'post',//提交方式
            data: idArr
        })
    },
    //更改用户状态
    updateStatus(id, status) {
        return request({
            //接口路径
            url: `${api_name}/updateStatus/${id}/${status}`,
            method: 'get' //提交方式
        })
    },
    //获取用户信息
    getUserInfoVoByToken() {
        return request({
            //接口路径
            url: `${api_name}/getUserInfoVoByToken`,
            method: 'get' //提交方式
        })
    },
    //修改密码
    changePassword(oldPassword, newPassword) {
        let params = {
            oldPassword: oldPassword,
            newPassword: newPassword
        }
        return request({
            //接口路径
            url: `${api_name}/changePassword`,
            method: 'get', //提交方式
            params: params
        })
    }
}