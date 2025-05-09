import request from '@/utils/request'

const api_name = '/system/monitor'

export default {

    getRedisCacheInfo() {
        return request({
            url: `${api_name}/getRedisCacheInfo`,
            method: "get"
        })
    },

}