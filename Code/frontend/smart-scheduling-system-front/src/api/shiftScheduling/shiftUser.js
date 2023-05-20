import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/shiftuser'

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
     * 为班次替换人员或者追加人员
     * @returns 
     */
    replaceOrAddMembersForShift(params){
        return request({
            url: `${api_name}/replaceOrAddMembersForShift`,
            method: "post",
            data: params
        })
    }
}