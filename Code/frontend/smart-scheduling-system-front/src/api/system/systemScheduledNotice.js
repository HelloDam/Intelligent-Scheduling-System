import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/system/systemScheduledNotice'

export default {

    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
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

    infoByToken(id) {
        return request({
            url: `${api_name}/infoByToken`,
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
    },

}