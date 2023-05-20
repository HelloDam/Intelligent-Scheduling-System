import request from '@/utils/request'

const api_name = '/shift-scheduling-calculate-service/statistics'

export default {

    /**
     * 获取指定月份的各门店的员工日均工作时长（月的工作时长/月的工作天数/参与工作的人数/60）
     * @param {*} param 
     * @returns 
     */
    statisticsStoreUserNum(param) {
        return request({
            url: `${api_name}/getStoreAverageStaffWorkTime`,
            method: "get",
            params: param
        })
    },

    /**
     * 查询指定月份的各门店的日均班次数量及日均分配率
     * @param {*} param 
     * @returns 
     */
    getStoreShiftNumAndAllocationRate(param) {
        return request({
            url: `${api_name}/getStoreShiftNumAndAllocationRate`,
            method: "get",
            params: param
        })
    },

    /**
     * 获取指定月份各门店的日均客流量
     * @param {*} param 
     * @returns 
     */
    getAveragePassengerFlow(param) {
        return request({
            url: `${api_name}/getAveragePassengerFlow`,
            method: "get",
            params: param
        })
    },

    /**
    * 获取指定月份的午餐数量和晚餐数量
    * @param {*} param 
    * @returns 
    */
    getTotalLunchNumAndDinnerNum(param) {
        return request({
            url: `${api_name}/getTotalLunchNumAndDinnerNum`,
            method: "get",
            params: param
        })
    },

    /**
    * 获取企业统计数据
    * @param {*} param 
    * @returns 
    */
    getStatisticsVoByEnterpriseId() {
        return request({
            url: `${api_name}/getStatisticsVoByEnterpriseId`,
            method: "get"
        })
    },

    /**
    * 获取门店统计数据
    * @param {*} param 
    * @returns 
    */
    getStatisticsVoByStoreId() {
        return request({
            url: `${api_name}/getStatisticsVoByStoreId`,
            method: "get"
        })
    },

    /**
    * 获取当前门店指定年 各月份的员工日均工作时长 员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量
    * @param {*} param 
    * @returns 
    */
    getMonthAverageStaffWorkTime(param) {
        return request({
            url: `${api_name}/getMonthAverageStaffWorkTime`,
            method: "get",
            params: param
        })
    },

    /**
    * 获取指定年份 各月的午餐数量和晚餐数量
    * @param {*} param 
    * @returns 
    */
    getMonthTotalLunchNumAndDinnerNum(param) {
        return request({
            url: `${api_name}/getMonthTotalLunchNumAndDinnerNum`,
            method: "get",
            params: param
        })
    },

    /**
    * 查询指定年份，每月的各门店的日均班次数量及日均分配率
    * @param {*} param 
    * @returns 
    */
    getMonthShiftNumAndAllocationRate(param) {
        return request({
            url: `${api_name}/getMonthShiftNumAndAllocationRate`,
            method: "get",
            params: param
        })
    },

    /**
   * 获取指定月份 日均工作时间最长/最短的前 n 名员工
   * @param {*} param 
   * @returns 
   */
    getUserWorkTime(param) {
        return request({
            url: `${api_name}/getUserWorkTime`,
            method: "get",
            params: param
        })
    },

}