import request from '@/utils/request'

const api_name = '/system/monitor'

export default {

    getServerInfo() {
        return request({
            url: `${api_name}/getServerInfo`,
            method: "get"
        })
    },

}