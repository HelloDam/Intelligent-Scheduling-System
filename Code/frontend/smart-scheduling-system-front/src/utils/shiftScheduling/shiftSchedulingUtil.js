import schedulingTaskApi from "@/api/shiftScheduling/schedulingTask";


export default {



    /**
    * 将日期格式化为 2020-02-01
    * @param {日期格式 new Date()} date 
    */
    getFormatDateStr(date) {
        return date.getFullYear() + "-" + ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + (date.getDate())).slice(-2)
    },

    /**
   * 获取今天的日期 2023-02-01
   */
    getTodayDate() {
        let date = new Date();
        return this.dateFormat(date);
    },

    /**
   * 日期格式化 2023-02-01
   */
    dateFormat(date) {
        let year = date.getFullYear(); //  返回的是年份
        let month = date.getMonth() + 1; //  返回的月份上个月的月份，记得+1才是当月
        let dates = date.getDate();
        if (month < 10) month = "0" + month;
        if (date < 10) dates = "0" + dates;
        let time = year + "-" + month + "-" + dates;
        return time;
    },

    /**
   * 判断一天是否休息
   * @param {*} date
   */
    judgeWetherRest(date, dateEntityMap) {
        let newDate = new Date(date);
        let isRest =
            this.jubgeWhetherDateInMap(newDate.toLocaleDateString(), dateEntityMap) == true &&
            dateEntityMap[newDate.toLocaleDateString()].isNeedWork !=
            undefined &&
            dateEntityMap[newDate.toLocaleDateString()].isNeedWork == 0;
        return isRest;
    },
    /**
     * 判断一天是否有班次
     * @param {*} date
     */
    judgeWetherHaveShift(date, dateEntityMap) {
        let newDate = new Date(date);
        let haveShift =
            this.jubgeWhetherDateInMap(newDate.toLocaleDateString(), dateEntityMap) == true &&
            dateEntityMap[newDate.toLocaleDateString()].isHaveShift !=
            undefined &&
            dateEntityMap[newDate.toLocaleDateString()].isHaveShift == 1;
        return haveShift;
    },

    /**
  * 判断一天是否在字典中有数据
  * @param {2023/3/1} dateStr
  */
    jubgeWhetherDateInMap(dateStr, dateEntityMap) {
        if (dateEntityMap != undefined && dateEntityMap != null) {
            let keys = Object.keys(dateEntityMap).sort();
            for (let i = 0; i < keys.length; i++) {
                if (dateStr == keys[i]) {
                    return true;
                }
            }
        }
        return false;
    },

    /**
     * 获取日历表的显示日期范围
     * 由于日历表自定义日期的起始日期只能是星期一，结束日期必须是周日，因此需要计算周一和周日所对应的日期
     * @param {实际开始日期,Date类型} startDate 
     * @param {实际结束日期,Date类型} endDate 
     */
    getDateRange(startDate, endDate) {

        ///计算起始日期
        console.log("startDate:" + startDate.toLocaleDateString())
        console.log("startDate.getDay():" + startDate.getDay())
        //起始日期是星期几（1,2,3,4,5,6,7分别表示星期一到星期天）( startDate.getDay()：是0代表星期天)
        let startDateDay = startDate.getDay() == 0 ? 7 : startDate.getDay();
        startDate.setTime(startDate.getTime() - 3600 * 1000 * 24 * (startDateDay - 1));

        ///计算结束日期
        let endDateDay = endDate.getDay() == 0 ? 7 : endDate.getDay();
        endDate.setTime(endDate.getTime() + 3600 * 1000 * 24 * (7 - endDateDay));

        ///设置日期范围
        let dateRange = [];
        dateRange.push(this.getFormatDateStr(startDate));
        dateRange.push(this.getFormatDateStr(endDate));
        return dateRange;
    }

}