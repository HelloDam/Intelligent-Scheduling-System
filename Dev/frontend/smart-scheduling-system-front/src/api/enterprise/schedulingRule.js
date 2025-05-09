import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/schedulingrule'

export default {

    /*
    查询列表
    */
    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
        })
    },

    /**
     * 查询所有规则
     * @returns 
     */
    listAll() {
        return request({
            url: `${api_name}/listAll`,
            method: "get"
        })
    },

    /*
    删除
    */
    removeById(id) {
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

    /*
    保存 添加
    */
    save(data) {
        return request({
            url: `${api_name}/save`,
            method: "post",
            data: data
        })
    },

    /*
    更新
    */
    updateById(data) {
        return request({
            url: `${api_name}/update`,
            method: "post",
            data: data
        })
    },

    info(id) {
        return request({
            url: `${api_name}/info/${id}`,
            method: "get"
        })
    },

    /**
     * 获取门店的排班规则
     * @returns 
     */
    getSchedulingRuleVoByStoreId() {
        return request({
            url: `${api_name}/getSchedulingRuleVoByStoreId`,
            method: "get"
        })
    },

    /**
    * 根据id获取排班规则
    * @returns 
    */
    getSchedulingRuleVoById(ruleId) {
        return request({
            url: `${api_name}/getSchedulingRuleVoById`,
            method: "get",
            params: { ruleId }
        })
    }

}