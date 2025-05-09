import request from '@/utils/request'

/*
操作日志相关的API请求函数
*/
const api_name = '/system/operationLog'

export default {

    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
        })
    },

    // /*
    // 删除
    // */
    // delete(id) {
    //     return request({
    //         url: `${api_name}/delete/${id}`,
    //         method: "delete"
    //     })
    // },

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

    
}
