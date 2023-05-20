import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/schedulingshift'

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
     * 根据日期id获取当天的班次信息
     */
    listSchedulingShiftVoByDateId(data) {
        // console.log("listSchedulingShiftVoByDateId_positionIdArr:" + JSON.stringify(positionIdArr))
        return request({
            url: `${api_name}/listSchedulingShiftVoByDateId`,
            method: "post",
            data: data
        })
    },

    /**
     * 获取周视图数据
     */
    getWeekViewData(params) {
        return request({
            url: `${api_name}/getWeekViewData`,
            method: "post",
            data: params
        })
    }
}