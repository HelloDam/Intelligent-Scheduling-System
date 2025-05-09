import request from '@/utils/request'

/*
菜单管理相关的API请求函数
*/
const api_name = '/enterprise/position'

export default {

    /*
    查询列表
    */
    listMemberListAndAppointMemberIdList(positionId) {
        return request({
            url: `${api_name}/listMemberListAndAppointMemberIdList/${positionId}`,
            method: "get"
        })
    },

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
     * 查询所有规则
     * @returns 
     */
    listAll() {
        return request({
            url: `${api_name}/listAll`,
            method: "get"
        })
    },

    /**
   * 查询出所有叶子节点数据
   * @returns 
   */
    listAllSonPosition(storeId) {
        return request({
            url: `${api_name}/listAllSonPosition`,
            method: "get",
            params: { storeId }
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
     * 
     * @returns 获取职位树
     */
    getPositionSelectTree() {
        return request({
            url: `${api_name}/getPositionSelectTree`,
            method: "get"
        })
    },

    //查询所有职位的树形结构
    findNodes(data) {
        return request({
            url: `${api_name}/findNodes`,
            method: 'post',
            data: data
        })
    },

}