import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/schedulingdate'

export default {

    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
        })
    },

    /**
     * 
     * @param {月份、选择职位} map 
     * @returns 
     */
    listDateByCondition(map) {
        return request({
            url: `${api_name}/listDateByCondition`,
            method: "post",
            data: map
        })
    },

    listAll(){
        return request({
            url: `${api_name}/listAll`,
            method: "get"
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

    /**
     * 更新
     * @param {实体类数据} data 
     * @param {1:修改用户信息 2:修改工作日} updateType 
     * @returns 
     */
    update(data,updateType) {
        return request({
            url: `${api_name}/update/${updateType}`,
            method: "post",
            data: data
        })
    },

    listAllDate() {
        return request({
            url: `${api_name}/listAllDate`,
            method: "get"
        })
    }
}