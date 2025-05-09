
import $http from '@/common/utils/request.js';

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/message'

export default {

    /*
    查询列表
    */
    list(data) {
        return request({
            url: `${api_name}/list`,
            method: "get",
            params: data
        })
    },

    /**
     * 查询一个门店的所有节假日
     * @returns 
     */
    listAllHolidaysOfOneStore() {
        return request({
            url: `${api_name}/listAllHolidaysOfOneStore`,
            method: "get"
        })
    },

    /**
     * 添加法定节假日
     * @returns 
     */
    addStatutoryHolidays() {
        return request({
            url: `${api_name}/addStatutoryHolidays`,
            method: "get"
        })
    },

    /*
    删除
    */
    removeById(id) {
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

    /*
    更新
    */
    updateById(data) {
        return request({
            url: `${api_name}/update`,
            method: "post",
            data: data
        })
    },

    info(id) {
        return request({
            url: `${api_name}/info/${id}`,
            method: "get"
        })
    },

    /**
     * 查询用户接受到的所有信息
     * @returns 
     */
    listMessageOfUser() {
        return $http.request({
            url: `${api_name}/listMessageOfUser`,
            method: "get"
        })
    },

    updateMessagePublishStatus(paramMap) {
        return request({
            url: `${api_name}/updateMessagePublishStatus`,
            method: "post",
            data: paramMap
        })
    }

}