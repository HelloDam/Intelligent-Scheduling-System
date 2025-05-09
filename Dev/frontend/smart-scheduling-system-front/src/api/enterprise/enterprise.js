import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/enterprise'

export default {

    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
        })
    },

    listEnterpriseVo(data) {
        return request({
            url: `${api_name}/listEnterpriseVo`,
            method: "get",
            params: data
        })
    },

    listAll() {
        return request({
            url: `${api_name}/listAll`,
            method: "get"
        })
    },

    /**
     * 从redis中获取企业的注册信息
     * @returns 
     */
    listAllEnterpriseRegisterVoFromRedis() {
        return request({
            url: `${api_name}/listAllEnterpriseRegisterVoFromRedis`,
            method: "get"
        })
    },

    /**
     * 通过企业的注册申请
     * @param {*} enterpriseRegisterVo 
     * @returns 
     */
    passEnterpriseRegister(enterpriseRegisterVo) {
        return request({
            url: `${api_name}/passEnterpriseRegister`,
            method: "post",
            data: enterpriseRegisterVo
        })
    },

    /**
     * 拒绝企业的注册申请
     * @param {*} enterpriseRegisterVo 
     * @returns 
     */
    rejectEnterpriseRegister(enterpriseRegisterVo) {
        return request({
            url: `${api_name}/rejectEnterpriseRegister`,
            method: "post",
            data: enterpriseRegisterVo
        })
    },

    /*
    删除
    */
    delete(id) {
        return request({
            url: `${api_name}/delete/${id}`,
            method: "delete"
        })
    },

    /*
    批量删除
    */
    deleteBatch(ids) {
        return request({
            url: `${api_name}/deleteBatch`,
            method: "post",
            data: ids
        })
    },

    info(id) {
        return request({
            url: `${api_name}/info/${id}`,
            method: "get"
        })
    },



    save(data) {
        return request({
            url: `${api_name}/save`,
            method: "post",
            data: data
        })
    },
    update(data) {
        return request({
            url: `${api_name}/update`,
            method: "post",
            data: data
        })
    }
}