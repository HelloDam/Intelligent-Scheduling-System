import request from '@/utils/request'

const api_name = '/enterprise/statistics'

export default {

    getRegisterEnterpriseNumOfYear(param) {
        return request({
            url: `${api_name}/getRegisterEnterpriseNumOfYear`,
            method: "get",
            params: param
        })
    },

}