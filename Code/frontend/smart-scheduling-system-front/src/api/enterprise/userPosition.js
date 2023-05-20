import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/userposition'

export default {

    //保存职位所指定的成员列表
    saveAppointMemberData(appointMemberData) {
        return request({
            url: `${api_name}/saveAppointMemberData`,
            method: 'post',
            data: appointMemberData
        })
    },

    /*
    保存 添加
    */
    save(data) {
        return request({
            url: `${api_name}/save`,
            method: "post",
            data: data
        })
    },


}