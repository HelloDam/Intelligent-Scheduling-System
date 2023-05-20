<template>
    <div class="weeklyContainer">
        <div class="cardDiv">
            <el-row type="flex" class="toolRow" justify="space-around">
                <el-col>
                    <div class="leftButton">
                        <el-tooltip class="item" effect="dark" content="上一周" placement="top">
                            <el-button icon="el-icon-arrow-up" circle @click="handleLast" class="button"></el-button>
                        </el-tooltip>
                        <el-tooltip class="item" effect="dark" :content="gobackcontent" placement="top">
                            <el-button @click="back2CurWeek" class="button">{{ gobackcontent }}</el-button>
                        </el-tooltip>

                        <el-tooltip class="item" effect="dark" content="下一周" placement="top">
                            <el-button icon="el-icon-arrow-down" circle @click="handleNext" class="button"></el-button>
                        </el-tooltip>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="block">
                        <el-date-picker v-model="valueWeek" type="week" :format="startDate + ' 至 ' + endDate"
                            placeholder="选择周" :picker-options="{ 'firstDayOfWeek': 1 }" style="width: 240px"
                            @change="getWeek" class="button">
                        </el-date-picker>
                    </div>
                </el-col>
                <el-col></el-col>
            </el-row>
            <el-divider id="elDivider"></el-divider>
            <div class="weeklyDiv">
                <el-row type="flex" :gutter="10" class="weeklyRow">
                    <el-col class="insideCol" v-for="(shiftGroup, index) in shiftListOfEachDay" :key="index">
                        <div class="weekDiv">
                            {{ weekData[index].name }}
                            <span v-if="indexAndDateMap[index] != null && indexAndDateMap[index].isNeedWork == 0"
                                class="restSpan">休</span>
                        </div>

                        <div v-for="(shift, index1) in shiftGroup" :key="index1" class="cardbox"
                            @click="userDetails(shift, index)">
                            <el-card class="incard" shadow="never"
                                style="display: flex; align-items: center;flex-direction: column;">
                                <div style="incardDiv">
                                    <div style="display: flex; align-items: center;">
                                        <span class="iconfont icon-shijian"
                                            style="font-size: 16px; color: #409EFF; margin-right: 5px;"></span>
                                        <span style="font-size: 15px;  color: #303133;">{{ shift.startTime
                                        }}~{{ shift.endTime }}</span>
                                    </div>
                                    <div v-if="shift.userInfoVoList != null"
                                        style="display: flex; align-items: center; margin-top: 10px;">
                                        <span class="iconfont icon-yuangong"
                                            style="font-size: 16px; color: #67C23A; margin-right: 5px;"></span>
                                        <span style="font-size: 15px;  color: #303133;">{{
                                            shift.userInfoVoList.length }}</span>
                                        <span class="iconfont icon-shijianzhouqi"
                                            style="font-size: 16px; color: #E6A23C; margin-right: 5px;margin-left: 10px; "></span>
                                        <span style="font-size: 15px;  color: #303133;">{{
                                            shift.shiftMinute }}min</span>
                                    </div>
                                    <div style="margin-top: 10px; border-top: 1px solid #d9d9d9; padding-top: 10px;"
                                        v-if="shift.mealType != null">
                                        <span v-if="shift.mealType === 0">
                                            <span class="iconfont icon-wucan"
                                                style="font-size: 18px; color: #F56C6C; margin-right: 5px;"></span>
                                            <span style="font-size: 15px;  color: #F56C6C;">{{
                                                shift.mealStartTime
                                            }}~{{ shift.mealEndTime }}</span>
                                        </span>
                                        <span v-if="shift.mealType === 1">
                                            <span class="iconfont icon-wucan"
                                                style="font-size: 18px; color: #F56C6C; margin-right: 5px;"></span>
                                            <span style="font-size: 15px;  color: #F56C6C;">{{
                                                shift.mealStartTime
                                            }}~{{ shift.mealEndTime }}</span>
                                        </span>
                                    </div>
                                </div>

                            </el-card>
                        </div>
                    </el-col>
                </el-row>
            </div>

        </div>

        <!-- <el-avatar class="avatarClass" size="medium" :src="userInfo.avatar"
                            v-for="(userInfo, index2) in getAheadUserInfoVoList(shift.userInfoVoList)" :key="index2">
                        </el-avatar>
                        <span v-if="shift.userInfoVoList.length > 3" style="font-weight: bolder;">
                            ···
                        </span> -->

        <el-dialog :title="detailTitle" :visible.sync="dialogVisible" width="50%" :before-close="handleClose"
            append-to-body>
            <el-row v-for="(user, index3) in userInfoEachCard" :key="index3" style="padding-bottom:10px;">
                <el-card shadow="hover">
                    <el-row :gutter="10" style="padding-top:10px" type="flex" justify="space-between">
                        <el-col :span="4.5">
                            <el-avatar shape="square" :size="160" :src="user.avatar" class="avatar"></el-avatar>
                        </el-col>
                        <el-col :span="3.5">
                            <el-row>
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-user"></i>
                                    <span> 姓名: {{ user.name }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-male" v-if="user.gender === 0"></i>
                                    <i class="el-icon-female" v-if="user.gender === 1"></i>
                                    <span> 性别: {{ user.gender ? '女' : '男' }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-mobile-phone"></i>
                                    <span> 电话: {{ user.phone }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-message"></i>
                                    <span> 邮箱: {{ user.mail }}</span>
                                </el-card>
                            </el-row>
                        </el-col>
                        <el-col :span="3.5">
                            <el-row>
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-office-building"></i>
                                    <span> 所属企业: {{ user.enterpriseName }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-location-information"></i>
                                    <span> 所属门店: {{ user.storeName }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-suitcase"></i>
                                    <span> 员工职位: {{ user.positionName }}</span>
                                </el-card>
                            </el-row>
                            <el-row style="padding-top:3px">
                                <el-card body-style="padding:10px" class="messageCard">
                                    <i class="el-icon-s-custom"></i>
                                    <span v-if="user.type === 10"> 用户类型: 普通用户</span>
                                    <span v-if="user.type === 0"> 用户类型: 系统管理员</span>
                                    <span v-if="user.type === 1"> 用户类型: 企业管理员</span>
                                    <span v-if="user.type === 2"> 用户类型: 门店管理员</span>
                                </el-card>
                            </el-row>
                        </el-col>
                        <el-col :span="3.5" style="margin-right:10px">
                            <div style="text-align:center">
                                <span>今日工作安排</span>
                            </div>

                            <div style="height:160px;">
                                <el-scrollbar style="height:100%;width:190px">
                                    <el-timeline style="padding-top:10px">
                                        <div>
                                            <el-timeline-item v-for="(act, index1) in activities[index3]" :key="index1"
                                                :type="act.type" :timestamp="act.timestamp">
                                                {{ act.content }}
                                            </el-timeline-item>
                                        </div>
                                    </el-timeline>
                                </el-scrollbar>
                            </div>
                        </el-col>
                    </el-row>
                </el-card>
            </el-row>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>

import moment from 'moment'
import schedulingShiftApi from '@/api/shiftScheduling/schedulingShift'
import schedulingTaskApi from '@/api/shiftScheduling/schedulingTask'

//引入css
import weeklyStyle from '@/styles/weekly/weeklyStyle.css'

export default {
    name: 'WeeklyView',
    props: {
        taskId: {
            type: Number
        },
    },
    data() {
        return {
            // weekTime:''
            valueWeek: '',
            taskValueWeek: '',
            //周开始时间
            startDate: '',
            //周结束时间
            endDate: '',
            //周视图数据
            shiftListOfEachDay: [],
            indexAndDateMap: [],
            userIdAndShiftList: [],
            weekData: [
                { name: "周 一" },
                { name: "周 二" },
                { name: "周 三" },
                { name: "周 四" },
                { name: "周 五" },
                { name: "周 六" },
                { name: "周 日" },
            ],
            dialogVisible: false,
            detailTitle: '',
            userInfoEachCard: [],
            reverse: true,
            activities: [],
            gobackcontent: '回到本周',
            realStartIndex: '',
            realEndIndex: ''
            // activities: [{
            //     type: 'primary',
            //     content: '上午',
            //     timestamp: '07:30-09:00'
            //     }, {
            //     type: 'success',
            //     content: '下午',
            //     timestamp: '14:30-15:00'
            //     }, {
            //     type: 'warning',
            //     content: '晚上',
            //     timestamp: '18:30-20:00'
            //     }, {
            //     type: 'info',
            //     content: '下午',
            //     timestamp: '14:30-15:00'
            //     },]
        }
    },
    created() {
        this.getDateWeek()
        if (this.taskId) {
            this.gobackcontent = '回到该任务起始周'
        }
    },
    methods: {
        getDateWeek() {

            if (this.taskId) {
                schedulingTaskApi.info(this.taskId).then(
                    res => {
                        const now = new Date(res.schedulingTask.startDate);
                        const nowTime = now.getTime();
                        const day = now.getDay();
                        const oneDayTime = 24 * 60 * 60 * 1000;
                        const mondayTime = nowTime - (day - 2) * oneDayTime;//默认是周二
                        this.valueWeek = new Date(mondayTime);
                        this.taskValueWeek = new Date(mondayTime);
                        console.log('this.valueWeek' + this.valueWeek)
                        console.log('this.taskValueWeek' + this.taskValueWeek)
                        console.log(this.valueWeek.getTime() == this.taskValueWeek.getTime())
                        this.getWeek();
                    }
                )
            } else {
                const now = new Date();
                const nowTime = now.getTime();
                const day = now.getDay();
                const oneDayTime = 24 * 60 * 60 * 1000;
                const mondayTime = nowTime - (day - 2) * oneDayTime;//默认是周二
                this.valueWeek = new Date(mondayTime);
                this.getWeek();
            }
        },
        getWeek() {
            if (this.taskId) {
                console.log('taskid传入')
                console.log(this.taskId)
                if (this.valueWeek.getTime() == this.taskValueWeek.getTime()) {
                    schedulingTaskApi.info(this.taskId).then(
                        res => {
                            // const s_date = moment(res.schedulingTask.startDate).utcOffset(8).format('YYYY-MM-DD')
                            let s_date = moment(res.schedulingTask.startDate).utcOffset(8).format('YYYY-MM-DD')
                            let e_date = moment(res.schedulingTask.endDate).utcOffset(8).format('YYYY-MM-DD')
                            console.log(s_date)
                            let s_weekofday = moment(s_date, 'YYYY-MM-DD').format('E') //计算指定日期是这周第几天
                            // let e_weekofday = moment(s_date,'YYYY-MM-DD').format('E')
                            console.log('起始日期序号')
                            console.log(s_weekofday)
                            // console.log(e_weekofday)
                            let s_monday = moment(s_date).subtract(s_weekofday - 1, 'days').format('YYYY-MM-DD');//周一日期
                            let s_sunday = moment(s_date).add(7 - s_weekofday, 'days').format('YYYY-MM-DD');//周日日期
                            console.log(s_monday)
                            console.log(s_sunday)
                            // let start_weekofday = moment(s_monday,'YYYY-MM-DD').format('E')
                            let timeDiff = moment(e_date).diff(moment(s_date), "days")
                            console.log('timeDiff')
                            console.log(timeDiff)
                            if (timeDiff > 7) {
                                let end_weekofday = moment(s_sunday, 'YYYY-MM-DD').format('E')
                                console.log('跨周任务首周起止日期序号')
                                console.log(s_weekofday)
                                console.log(end_weekofday)
                                this.realStartIndex = s_weekofday
                                this.realEndIndex = end_weekofday
                            } else {
                                let end_weekofday = moment(e_date, 'YYYY-MM-DD').format('E')
                                console.log('任务不跨周的起止日期序号')
                                console.log(s_weekofday)
                                console.log(end_weekofday)
                                this.realStartIndex = s_weekofday
                                this.realEndIndex = end_weekofday
                            }
                            console.log('realStartIndex and realEndIndex')
                            console.log(this.realStartIndex)
                            console.log(this.realEndIndex)
                            this.startDate = s_monday; //本周起始时间
                            this.endDate = s_sunday; //本周结束时间
                            console.log(this.startDate)
                            console.log(this.endDate)
                            this.getCurWeekData();
                        }
                    )
                } else {
                    let now = new Date(this.valueWeek);
                    let nowTime = now.getTime();
                    let day = now.getDay();
                    let oneDayTime = 24 * 60 * 60 * 1000;
                    let mondayTime = nowTime - (day - 1) * oneDayTime;
                    let sundayTime = nowTime + (7 - day) * oneDayTime;
                    this.startDate = moment(mondayTime).format('YYYY-MM-DD'); //本周起始时间
                    this.endDate = moment(sundayTime).format('YYYY-MM-DD'); //本周结束时间
                    this.realStartIndex = '1'
                    this.realEndIndex = '7'
                    console.log(this.startDate)
                    console.log(this.endDate)
                    this.getCurWeekData();
                }
            } else {
                console.log('无taskid传入')
                let now = new Date(this.valueWeek);
                let nowTime = now.getTime();
                // console.log("nowTime " + nowTime)
                let day = now.getDay();
                let oneDayTime = 24 * 60 * 60 * 1000;
                let mondayTime = nowTime - (day - 1) * oneDayTime;
                let sundayTime = nowTime + (7 - day) * oneDayTime;
                this.startDate = moment(mondayTime).format('YYYY-MM-DD'); //本周起始时间
                this.endDate = moment(sundayTime).format('YYYY-MM-DD'); //本周结束时间
                this.realStartIndex = '1'
                this.realEndIndex = '7'
                console.log(this.startDate)
                console.log(this.endDate)
                this.getCurWeekData();
            }
        },
        /**
         * 上一周
         */
        handleLast() {
            const last = new Date(this.valueWeek);
            last.setDate(last.getDate() - 7);//日期相加减
            this.valueWeek = last;
            this.getWeek();
        },
        /**
         * 下一周
         */
        handleNext() {
            const next = new Date(this.valueWeek);
            next.setDate(next.getDate() + 7);
            this.valueWeek = next;
            this.getWeek();
        },
        /**
         * 回到本周
         */
        back2CurWeek() {
            this.getDateWeek()
        },
        /**
         * 获取当前周的排班数据
         */
        getCurWeekData() {
            console.log('获取当前选择周的数据')
            console.log("valueWeek：" + this.valueWeek)
            console.log("startDate" + this.startDate)
            console.log("endDate" + this.endDate)
            console.log("taskId" + this.taskId)
            console.log("realStartIndex" + this.realStartIndex)
            console.log("realEndIndex" + this.realEndIndex)
            schedulingShiftApi.getWeekViewData({
                "startDate": this.startDate, "endDate": this.endDate,
                "realStartIndex": this.realStartIndex, "realEndIndex": this.realEndIndex
            }).then(
                res => {
                    this.shiftListOfEachDay = res.shiftListOfEachDay;
                    this.indexAndDateMap = res.indexAndDateMap;
                }
            )
        },
        /**
         * 获取前面几个用户
         * @param {*} shift 
         */
        getAheadUserInfoVoList(userInfoVoList) {
            let aheadUserInfoVoList = [];
            for (let i = 0; i < Math.min(3, userInfoVoList.length); i++) {
                aheadUserInfoVoList.push(userInfoVoList[i]);
            }
            // console.log("aheadUserInfoVoList:"+JSON.stringify(aheadUserInfoVoList))
            for (let i = 0; i < aheadUserInfoVoList.length; i++) {
                console.log("aheadUserInfoVoList:" + JSON.stringify(aheadUserInfoVoList[i]))
            }
            return aheadUserInfoVoList;
        },

        handleClose(done) {
            done();
        },

        userDetails(shift, index) {
            console.log("查看班次")
            console.log(parseInt(this.startDate.substring(8, 10)) + index);
            var startnum = parseInt(this.startDate.substring(8, 10)) + index;
            var start = this.startDate.substring(0, 8) + startnum.toString();
            console.log(start)
            this.userInfoEachCard = shift.userInfoVoList;
            // console.log(this.userInfoEachCard[0]['id']);
            this.detailTitle = shift.startTime + '~' + shift.endTime + '班次所有员工信息';
            // 查该班次中员工的当日工作时段  activities:[{type, content, timestamp}]

            schedulingShiftApi.getWeekViewData({ "startDate": start, "endDate": start }).then(
                res => {
                    this.userIdAndShiftList = res.userIdAndShiftList;
                    console.log(this.userIdAndShiftList)
                    // const userId = this.userIdAndShiftList[this.userInfoEachCard[0]['id']]
                    // console.log(this.userIdAndShiftList[this.userInfoEachCard[0]['id']])
                    // console.log(this.userIdAndShiftList[userId])
                    // const userTasks = this.userIdAndShiftList[userId]
                    this.activities = []
                    var act = []
                    for (var key in this.userInfoEachCard) {
                        console.log(this.userIdAndShiftList[this.userInfoEachCard[key].id])
                        var id = this.userInfoEachCard[key].id
                        console.log(id)
                        var userTasks = this.userIdAndShiftList[this.userInfoEachCard[key].id]
                        // act.push({"numid":id.toString(), "timestamp":null})
                        for (var key2 in userTasks) {
                            console.log(userTasks[key2].startDate)
                            var timestamp = userTasks[key2].startDate.substring(11, 16) + '~' + userTasks[key2].endDate.substring(11, 16)
                            var starthour = parseInt(userTasks[key2].startDate.substring(11, 13))
                            var endhour = parseInt(userTasks[key2].endDate.substring(11, 13))
                            var content = ''
                            var type = ''
                            if (starthour < 12) {
                                content = '上午'
                                type = 'primary'
                            } else if (starthour <= 18) {
                                content = '下午'
                                type = 'primary'
                            } else {
                                content = '晚上'
                                type = 'danger'
                            }
                            var dic = { "type": type, "content": content, "timestamp": timestamp }
                            act.push(dic)
                        }
                        this.activities.push(act)
                        act = []
                    }

                    // console.log(this.activities)
                    // console.log("userIdAndShiftList:" + JSON.stringify(userIdAndShiftList))
                }
            )
            this.dialogVisible = true;
        }
    }
}
</script>

<style lang="scss" scoped>
.weeklyContainer {
    /* padding: 10px; */
    /* height: 100px; */
    /* min-height: calc(100% - 700px);
    max-height: calc(100% - 700px);
    background: rgb(255, 1, 1); */
    /* background: rgb(255, 1, 1); */
    height: 100%;
}

.cardDiv {
    /* background: rgb(1, 196, 255); */
    border-radius: 5px;
    margin-top: 10px;
    background: rgba(242, 247, 251, 0.6);
    backdrop-filter: blur(50px);
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
        inset 1px 1px 1px rgba(0, 0, 0, 0.1);
    height: 100%;
    padding: 10px;

    /* 回到本周那一行 */
    .toolRow {
        padding: 15px 0;
        border-radius: 5px;
        background: rgba(255, 255, 255, 0.6);
        border: 1px solid rgba(255, 255, 255, 0.18);
        box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2),
            inset 2px 2px 2px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        height: 70px;
    }

    #elDivider {
        margin: 15px 0px;
    }

    /* 定义滚动条的轨道和滑块样式 */
    .weeklyDiv:hover::-webkit-scrollbar-track {
        background-color: #f1f1f1;
    }

    .weeklyDiv:hover::-webkit-scrollbar-thumb {
        background-color: #D2D2D2;
        border-radius: 5px;
    }

    .weeklyDiv:hover::-webkit-scrollbar-button {
        background-color: #D2D2D2;
        height: 5px;
    }

    /* 隐藏滚动条 */
    .weeklyDiv::-webkit-scrollbar {
        // 隐藏滚动条宽度
        // width: 0 !important;
        // 隐藏滚动条高度
        // height: 0 !important;
        // 隐藏滚动条背景
        background-color: transparent;
        width: 10px;
        height: 5px;
    }

    /* 鼠标进来的时候显示滚动条 */
    .weeklyDiv:hover::-webkit-scrollbar {
        // background-color: #f1f1f1;
        width: 10px;
        height: 5px;
    }

    .weeklyDiv {
        height: calc(100% - 70px - 15px - 20px);
        // height: 300px;
        // background: #ff057e;
        //添加垂直滚动条
        overflow-y: auto;
        overflow-x: hidden;

        // 周视图哪一行
        .weeklyRow {
            // height: calc(100% - 70px - 15px - 100px);
            // height: 200px;
            // background: #56c44b;

            .insideCol {
                text-align: center;
                max-height: 100%;

                .weekDiv {
                    border-radius: 5px;
                    background: rgba(255, 255, 255, 0.6);
                    border: 1px solid rgba(255, 255, 255, 0.18);
                    box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2),
                        inset 2px 2px 2px rgba(0, 0, 0, 0.1);
                    width: 100%;
                    height: 50px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: bolder;
                }

                .cardbox {
                    border-radius: 5px;
                    background: rgba(255, 255, 255, 0.6);
                    border: 1px solid rgba(255, 255, 255, 0.18);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 10px 0px 10px 0px;
                    box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2),
                        inset 2px 2px 2px rgba(0, 0, 0, 0.1);
                    overflow: auto;
                }
            }
        }

    }


}


.leftButton {
    padding-left: 10px;
}

.weekrow {
    margin-top: 20px;
    font-family: "Microsoft YaHei";
}


.avatarClass {
    margin: 10px 3px 3px 3px;
}

.el-scrollbar__wrap {
    overflow-x: hidden;
}

.restSpan {
    color: #56c44b;
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 1px;
    padding: 2px 2px;
    box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    margin-left: 10px;
    font-size: 14px;
}

.button {
    background: rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.2),
        inset 2px 2px 2px rgba(0, 0, 0, 0.1);
}

.cardbox:hover {
    background-color: rgba(255, 255, 255, 0.8);
    /* margin: 10px 5px 10px 5px; */
    transform: scale(1.03);
    cursor: pointer;
}

.incard {
    background: rgba(255, 255, 255, 0);
    border: 1px solid rgba(255, 255, 255, 0);
    display: flex;
    align-items: center;
    justify-content: center;
}

.incardDiv {
    background: rgba(255, 255, 255, 0);
}

/* 头像 */
.avatar {
    border-radius: 3px;
    box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
    transition: transform 0.2s ease-in-out;
}

.avatar:hover {
    /* 鼠标放上去的时候，放大头像 */
    transform: scale(1.05);
}

.messageCard:hover {
    transform: scale(1.05);
}
</style>