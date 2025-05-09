import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/thirdParty/oss'

export default {

    getPolicy(data) {
        return request({
            url: `${api_name}/policy`,
            method: "get",
            params: data
        })
    },

}