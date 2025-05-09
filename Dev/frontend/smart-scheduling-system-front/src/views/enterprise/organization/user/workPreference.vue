<template>
    <el-form ref="form" :model="user" :rules="rules" label-width="120px">
        <el-form-item label="工作日偏好" prop="workDayPreferenceList">
            <el-checkbox-group v-model="user.workDayPreferenceList" @change="handleCheckedWordDayChange">
                <el-checkbox v-for="workDay in workDayList" :label="workDay.key" :key="workDay.key">
                    {{ workDay.label }}
                </el-checkbox>
            </el-checkbox-group>
        </el-form-item>
        <el-form-item label="工作时间偏好" prop="workTimePreference">
            <!-- <el-input v-model="user.workTimePreference" placeholder="请输入新密码" type="password" show-password /> -->
            <el-table :data="user.timeSlotList" style="width: 50%">
                <el-table-column label="开始时间" prop="startTime" align="center">
                    <template slot-scope="scope">
                        <el-time-select v-model="scope.row.startTime" :picker-options="{
                            start: workStartTime,
                            step: step,
                            end: wordEndTime,
                            minTime: scope.row.startMinTime,
                            maxTime: scope.row.startMaxTime
                        }" placeholder="选择时间" size="mini" @change="startChange(scope.$index)" class="timeSelect">
                        </el-time-select>
                    </template>
                </el-table-column>
                <el-table-column label="结束时间" prop="endTime" align="center">
                    <template slot-scope="scope">
                        <el-time-select v-model="scope.row.endTime" :picker-options="{
                            start: workStartTime,
                            step: step,
                            end: wordEndTime,
                            minTime: scope.row.endMinTime,
                            maxTime: scope.row.endMaxTime
                        }" placeholder="选择时间" size="mini" class="timeSelect">
                        </el-time-select>
                    </template>
                </el-table-column>
                <el-table-column align="center">
                    <template slot="header" slot-scope="scope">
                        <!-- <el-input v-model="search" size="mini" placeholder="输入关键字搜索" /> -->
                        <el-button type="text" @click="addTimeSlot" icon="el-icon-circle-plus-outline">添加时间段</el-button>
                    </template>
                    <template slot-scope="scope">
                        <el-button size="mini" type="danger"
                            @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-form-item>
        <el-form-item label="班次时长偏好" prop="shiftLengthPreference">
            每天工作时长：
                <el-time-select v-model="user.shiftLengthPreferenceOneDay" :picker-options="{
                            start: dayShiftMinTime,
                            step: step,
                            end: dayShiftMaxTime,
                            minTime: dayShiftMinTime,
                            maxTime: dayShiftMaxTime
                        }" placeholder="选择时间" size="mini" class="timeSelect">
                        </el-time-select>
           <span style="margin-left: 20px;">每周工作时长：</span> 
                <el-time-select v-model="user.shiftLengthPreferenceOneWeek" :picker-options="{
                            start: weekShiftMinTime,
                            step: step,
                            end: weekShiftMaxTime,
                            minTime: weekShiftMinTime,
                            maxTime: weekShiftMaxTime
                        }" placeholder="选择时间" size="mini" class="timeSelect">
                        </el-time-select>


        </el-form-item>
        <el-form-item>
            <el-button type="primary" size="mini" @click="submit">保存</el-button>
            <!-- <el-button type="danger" size="mini" @click="close">关闭</el-button> -->
        </el-form-item>
    </el-form>
</template>

<script>
import userApi from '@/api/system/user'

//工作日
const workDayOptions = [
    { key: 1, label: "星期一" },
    { key: 2, label: "星期二" },
    { key: 3, label: "星期三" },
    { key: 4, label: "星期四" },
    { key: 5, label: "星期五" },
    { key: 6, label: "星期六" },
    { key: 7, label: "星期天" }
]

export default {
    props: {
        user: {
            type: Object
        }
    },
    data() {
        const equalToPassword = (rule, value, callback) => {
            if (this.user.newPassword !== value) {
                callback(new Error("两次输入的密码不一致"));
            } else {
                callback();
            }
        };
        return {
            // 表单校验
            rules: {
                // oldPassword: [
                //     { required: true, message: "旧密码不能为空", trigger: "blur" }
                // ],
                // newPassword: [
                //     { required: true, message: "新密码不能为空", trigger: "blur" },
                //     { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }
                // ],
                // confirmPassword: [
                //     { required: true, message: "确认密码不能为空", trigger: "blur" },
                //     { required: true, validator: equalToPassword, trigger: "blur" }
                // ]
            },
            ///工作日偏好选项
            workDayList: workDayOptions,
            ///工作时间偏好
            //门店的开始上班时间
            workStartTime: '00:00',
            step: '00:05',
            //门店的下班时间
            wordEndTime: '24:00',
            ///班次时间偏好
            dayShiftMinTime: '02:00',
            dayShiftMaxTime: '10:00',
            weekShiftMinTime: '10:00',
            weekShiftMaxTime: '60:00',
        };
    },
    methods: {
        submit() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    ///工作日数据保存
                    let workDayPreference = ""
                    for (let i = 0; i < this.user.workDayPreferenceList.length - 1; i++) {
                        workDayPreference += this.user.workDayPreferenceList[i] + "|"
                    }
                    workDayPreference += this.user.workDayPreferenceList[this.user.workDayPreferenceList.length - 1]
                    this.user.workDayPreference = workDayPreference
                    ///工作时间偏好
                    let workTimePreference = ""
                    for (let i = 0; i < this.user.timeSlotList.length - 1; i++) {
                        workTimePreference += this.user.timeSlotList[i].startTime + "~" + this.user.timeSlotList[i].endTime + "|"
                    }
                    workTimePreference += this.user.timeSlotList[this.user.timeSlotList.length - 1].startTime + "~" + this.user.timeSlotList[this.user.timeSlotList.length - 1].endTime
                    this.user.workTimePreference = workTimePreference
                    ///班次时间偏好
                    this.user.shiftLengthPreference = this.user.shiftLengthPreferenceHour + "|" + this.user.shiftLengthPreferenceMinute
                    userApi.update(this.user).then(response => {
                        this.$message.success("修改成功");
                    });
                }
            });
        },
        close() {
            this.$tab.closePage();
        },
        ///工作日偏好改变
        handleCheckedWordDayChange(arr) {
            //arr是选中的key数组

        },
        ///工作时间偏好
        //删除时间段
        handleDelete(index, row) {
            console.log(index, row);
            this.user.timeSlotList.splice(index, 1)
        },
        //添加时间段
        addTimeSlot() {
            let newTimeSlot = undefined
            let startTime = undefined
            let endTime = undefined
            let minTime = undefined
            let maxTime = undefined
            if (this.user.timeSlotList.length > 0) {
                //--if--如果已经有时间段，新时间段的开始时间即最后一个时间段的结束时间
                startTime = this.user.timeSlotList[this.user.timeSlotList.length - 1].endTime
                endTime = this.getAddTime(startTime, 2)
            } else {
                //--if--如果还没有时间段，新时间段的开始时间即上班时间
                startTime = this.workStartTime
                endTime = this.getAddTime(startTime, 2)
            }
            minTime = startTime
            maxTime = this.wordEndTime
            newTimeSlot = {
                startTime: startTime,
                endTime: endTime,
                startMinTime: minTime,
                startMaxTime: maxTime,
                endMinTime: minTime,
                endMaxTime: maxTime
            }
            this.user.timeSlotList.push(newTimeSlot)
        },
        //在初始时间的基础上面添加一个时间段
        getAddTime(start, addHour) {
            let startStr = start + ""
            let startArray = startStr.split(":")
            // console.log("startArray:" + JSON.stringify(startArray))
            return (parseInt(startArray[0]) + addHour) + ":" + startArray[1]
        },
        //修改开始时间
        startChange(row) {
            console.log("row:" + JSON.stringify(row))
        },
        ///班次时间偏好
        hourChange() {
            if (this.shiftLengthPreferenceHour == this.maxHour) {
                this.shiftLengthPreferenceMinute = 0
                this.maxMiute = 0
            } else {
                this.maxMiute = 59
            }
        }
    }
};
</script>
<style scoped>
.timeSelect{
    width: 100px;
}
</style>
