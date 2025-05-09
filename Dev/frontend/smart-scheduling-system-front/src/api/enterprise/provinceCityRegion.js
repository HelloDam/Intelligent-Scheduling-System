import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/provincecityregion'

export default {

    list(pageIndex, pageSize, key) {
        let param = {
            page: pageIndex,
            limit: pageSize,
            key: key
        }
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: param
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

    update(data) {
        return request({
            url: `${api_name}/update`,
            method: "post",
            data: data
        })
    },

    //获取省市区的树形结构数据
    getAreaTree() {
        return request({
            url: `${api_name}/getAreaTree`,
            method: "get"
        })
    },
}