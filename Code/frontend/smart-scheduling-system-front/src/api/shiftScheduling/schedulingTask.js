import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/schedulingTask'

export default {

    list(data) {
        return request({
            url: `${api_name}/list`,
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
    获取门店的最大排班日期
    */
    getMaxEndDateOfTask() {
        return request({
            url: `${api_name}/getMaxEndDateOfTask`,
            method: "get"
        })
    },

    /*
    获取门店的最大排班日期
    */
    listOtherTaskList(param) {
        return request({
            url: `${api_name}/listOtherTaskList`,
            method: "get",
            params: param
        })
    },

    /*
    获取门店的最大排班日期
    */
    deleteAllResultOfTask(param) {
        return request({
            url: `${api_name}/deleteAllResultOfTask`,
            method: "get",
            params: param
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
    update(data, updateType) {
        return request({
            url: `${api_name}/update/${updateType}`,
            method: "post",
            data: data
        })
    },

    /**
     * 修改任务的发布状态
     * @param {*} paramMap 
     * @returns 
     */
    updateTaskPublishStatus(paramMap) {
        return request({
            url: `${api_name}/updateTaskPublishStatus`,
            method: "post",
            data: paramMap
        })
    },

    listAllDate() {
        return request({
            url: `${api_name}/listAllDate`,
            method: "get"
        })
    },

     /**
     * 删除任务的所有结果
     * @param {*} taskId 
     * @returns 
     */
     deleteAllResultOfTask(taskId) {
        return request({
            url: `${api_name}/deleteAllResultOfTask`,
            method: "get",
            params: { taskId }
        })
    },
}