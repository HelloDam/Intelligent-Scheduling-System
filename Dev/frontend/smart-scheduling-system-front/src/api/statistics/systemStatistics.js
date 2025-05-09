import request from '@/utils/request'

const api_name = '/system/statistics'

export default {

    statisticsStoreUserNum() {
        return request({
            url: `${api_name}/statisticsStoreUserNum`,
            method: "get"
        })
    },


    getSystemUseStatisticsVo() {
        return request({
            url: `${api_name}/getSystemUseStatisticsVo`,
            method: "get"
        })
    },

    getRegisterUserNumOfYear(param) {
        return request({
            url: `${api_name}/getRegisterUserNumOfYear`,
            method: "get",
            params: param
        })
    },

}