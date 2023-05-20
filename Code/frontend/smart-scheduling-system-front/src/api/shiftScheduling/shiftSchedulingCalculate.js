import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/calculate'

export default {

    /**
     * 多线程计算多个任务
     * @param {任务id集合} taskIdList 
     * @returns 
     */
    solve(taskIdList) {
        return request({
            url: `${api_name}/solve`,
            method: "post",
            data: taskIdList
        })
    },

    /**
     * 同时使用全部算法来计算同一个任务
     * @param {参数} params 
     * @returns 
     */
    multiAlgorithmSolve(params) {
        return request({
            url: `${api_name}/multiAlgorithmSolve`,
            method: "post",
            data: params
        })
    },

    /**
     * 获取所有算法组合
     * @param {*} params 
     * @returns 
     */
    listMultiAlgorithmResult(params) {
        return request({
            url: `${api_name}/listMultiAlgorithmResult`,
            method: "post",
            data: params
        })
    },

    /**
     * 获取所有算法组合
     */
    getAllAlgorithmGroup(taskId) {
        return request({
            url: `${api_name}/getAllAlgorithmGroup`,
            method: "get",
            params: { taskId }
        })
    },

   

    /**
     * 用虚拟任务的结果覆盖任务结果
     */
    overlayResult(virtualTaskId) {
        return request({
            url: `${api_name}/overlayResult`,
            method: "get",
            params: { virtualTaskId }
        })
    }
}