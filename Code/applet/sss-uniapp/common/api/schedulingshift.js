import $http from '@/common/utils/request.js';

/*
菜单管理相关的API请求函数
*/
const api_name = '/shift-scheduling-calculate-service/schedulingshift'

export default {

	/*
	查询列表
	*/
	getTodayShiftListOfUser() {
		return $http.request({
			url: `${api_name}/getTodayShiftListOfUser`,
			method: "get"
		})
	},
	/**
	 * 获取用户指定周的班次数据
	 * @param {Object} startDateStr 开始日期
	 * @param {Object} endDateStr 结束日期
	 */
	getWeekShiftListOfUser(startDateStr, endDateStr) {
		return $http.request({
			url: `${api_name}/getWeekShiftListOfUser`,
			method: "get",
			data: {
				startDateStr,
				endDateStr
			}
		})
	}


}