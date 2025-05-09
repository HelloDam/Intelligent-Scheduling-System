import request from '@/utils/request'

//常量
const api_name = '/system/role'

export default {
    //列表
    getPageList(data) {

        return request({
            //接口路径
            url: `${api_name}/list`,
            method: 'get', //提交方式
            //参数
            params: data
        })
    },
    //删除
    removeId(id) {
        return request({
            //接口路径
            url: `${api_name}/delete/${id}`,
            method: 'delete' //提交方式
        })
    },
    //添加
    saveRole(role) {
        return request({
            //接口路径
            url: `${api_name}/save`,
            method: 'post', //提交方式
            //传递json格式
            data: role
        })
    },
    //根据id查询
    getRoleId(id) {
        return request({
            //接口路径
            url: `${api_name}/info/${id}`,
            method: 'get' //提交方式
        })
    },
    //修改的方法
    update(role) {
        return request({
            //接口路径
            url: `${api_name}/update`,
            method: 'post', //提交方式
            data: role
        })
    },
    //批量删除
    batchRemove(idList) {
        return request({
            //接口路径
            url: `${api_name}/deleteBatch`,
            method: 'delete', //提交方式
            data: idList
        })
    },
    //根据用户id查询用户已分配的角色
    getRolesByUserId(userId) {
        return request({
        url: `${api_name}/toAssign/${userId}`,
        method: 'get'
        })
    },
    //分配角色
    assignRoles(assginRoleVo) {
        return request({
        url: `${api_name}/doAssign`,
        method: 'post',
        data: assginRoleVo
        })
    }
}