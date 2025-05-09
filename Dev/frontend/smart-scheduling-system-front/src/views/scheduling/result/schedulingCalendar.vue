<template>
  <div v-loading="listShiftLoading" element-loading-text="正在查询当天班次信息" style="height: 100%">
    <el-calendar v-model="date" :range="dateRange" style="height: 100%">
      <template slot="dateCell" slot-scope="{ date, data }">
        <div style="width: 100%; height: 100%" :class="data.isSelected ? 'is-selected' : ''"
          @click="dateClick(date, data)" v-if="jubgeWhetherDateInMap(date.toLocaleDateString())">
          <span :class="data.isSelected ? 'is-selected' : ''">
            <span v-if="judgeWetherRest(date) === true" style="color: #67c23a">
              {{ data.day.split("-").slice(1).join("-") }}
            </span>
            <span v-else>
              {{ data.day.split("-").slice(1).join("-") }}
            </span>
          </span>
          <span v-if="judgeWetherRest(date) === true">
            <h1 style="margin: 0px">
              <el-tag type="success">休息</el-tag>
            </h1>
          </span>
          <span v-if="judgeWetherHaveShift(date) === true">
            <h1 style="margin: 0px">
              <el-tag type="warning">上班</el-tag>
            </h1>
          </span>
        </div>
      </template>
    </el-calendar>
    <el-dialog :title="dtitle" :visible.sync="dialogVisible" width="90%" class="parent" append-to-body>
      <!-- <div style="padding-bottom:10px">
        <el-button @click="exportPDF">导出PDF</el-button>
      </div> -->
      <div slot="title" class="dialog-title">
        <span class="dialog-title-text">{{ dtitle }}</span>
        <!-- <el-button type="primary" style="">编辑</el-button> -->
        <el-tag effect="dark" type="" style="margin-left: 30px"> 班次 </el-tag>
        <el-tag effect="dark" type="success" style="margin-left: 5px">
          午餐
        </el-tag>
        <el-tag effect="dark" type="danger" style="margin-left: 5px">
          晚餐
        </el-tag>
      </div>
      <!-- <el-divider></el-divider> -->
      <!-- <div style="text-align:center;font-size:14px">
                <el-row>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>班次总数量：</span>
                    </div>
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>班次总时长：</span>
                    </div>
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>参与员工数量：</span>
                    </div>
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>员工平均工作时长：</span>
                    </div>
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>吃午饭人数：</span>
                    </div>
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-purple">
                    <span>吃晚餐人数：</span>
                    </div>
                </el-col>
                </el-row>
            </div> -->
      <div class="wrapper">
        <div class="container">
          <GanttChar class="left-container" ref="ganttChar" :tasks="tasks"></GanttChar>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import schedulingDateApi from "@/api/shiftScheduling/schedulingDate";
import schedulingShiftApi from "@/api/shiftScheduling/schedulingShift";
import shiftUserApi from "@/api/shiftScheduling/shiftUser";
import { gantt } from "dhtmlx-gantt";
import GanttChar from "../ganttx.vue";
import schedulingTaskApi from "@/api/shiftScheduling/schedulingTask";

///工具js
import shiftSchedulingUtil from "@/utils/shiftScheduling/shiftSchedulingUtil";

export default {
  name: "schedulingCalendar",
  components: { GanttChar },
  props: {
    taskIdForSchedulingCalendar: {
      type: Number,
    },
    dateEntityMap: {
      type: Object,
    },
    userIdList: {
      type: Array,
    },
  },
  data() {
    return {
      //// 遮罩层
      date: new Date(),
      dialogVisible: false,
      dialogVisible2: false,
      pdfDialogVisible: false,

      ////加载
      //查询班次加载
      listShiftLoading: false,

      ////日历选择
      dtitle: "",
      day: undefined,

      ////排班显示
      tasks: {
        data: [
          {
            id: 1,
            text: "张三",
            eid: "121212",
            startDate: "2023-03-15 08:00:00",
            start_date: "2023-03-15 08:00:00",
            startTime: "08:00",
            endDate: "2023-03-15 9:00:00",
            end_date: "2023-03-15 9:00:00",
            endTime: "9:00",
            progress: 1.0,
            type: "task",
          },
          {
            id: 2,
            text: "张三",
            eid: "121213",
            startDate: "2023-03-15 08:00:00",
            start_date: "2023-03-15 08:00:00",
            startTime: "08:00",
            endDate: "2023-03-15 9:00:00",
            end_date: "2023-03-15 9:00:00",
            endTime: "9:00",
            progress: 0,
            render: "split",
            parent: null,
            type: "task",
          },
          {
            id: 3,
            text: "张三",
            eid: "121212",
            startDate: "2023-03-15 08:00:00",
            start_date: "2023-03-15 08:00:00",
            startTime: "08:00",
            endDate: "2023-03-15 9:00:00",
            end_date: "2023-03-15 9:00:00",
            endTime: "9:00",
            progress: 0,
            parent: 2,
            render: null,
            priority: 1,
            type: "task",
          },
          {
            id: 4,
            text: "张三",
            eid: "121212",
            startDate: "2023-03-15 09:00:00",
            start_date: "2023-03-15 09:00:00",
            startTime: "09:00",
            endDate: "2023-03-15 10:00:00",
            end_date: "2023-03-15 10:00:00",
            endTime: "10:00",
            progress: 0,
            parent: 2,
            priority: 3,
            type: "task",
          },
          {
            id: 5,
            text: "张三",
            eid: "121212",
            startDate: "2023-03-15 09:00:00",
            start_date: "2023-03-15 09:00:00",
            startTime: "09:00",
            endDate: "2023-03-15 10:00:00",
            end_date: "2023-03-15 10:00:00",
            endTime: "10:00",
            progress: 0,
            priority: "3",
            type: "task",
          },
          {
            id: 459671,
            eid: "5354",
            createTime: "2023-03-15 15:35:46",
            updateTime: "2023-03-15 15:35:46",
            startDate: "2023-03-15 08:30:00",
            start_date: "2023-03-15 08:30:00",
            startTime: "08:30",
            endDate: "2023-03-15 11:15:00",
            end_date: "2023-03-15 11:15:00",
            endTime: "11:15",
            schedulingDateId: 23856,
            mealStartDate: "2023-03-15 11:15:00",
            mealEndDate: "2023-03-15 11:45:00",
            mealType: 0,
            totalMinute: 210,
            staffNameList: ["闾丘行"],
            staffNameListStr: "闾丘行",
            text: "闾丘行",
            positionNameList: ["收银"],
            positionNameListStr: "收银",
            dep: "收银",
            progress: 1,
            render: "split",
            parent: null,
            priority: 1,
            type: "task",
          },
          {
            id: 459672,
            eid: "5354",
            createTime: "2023-03-15 15:35:46",
            updateTime: "2023-03-15 15:35:46",
            startDate: "2023-03-15 08:30:00",
            start_date: "2023-03-15 08:30:00",
            startTime: "08:30",
            endDate: "2023-03-15 10:15:00",
            end_date: "2023-03-15 10:15:00",
            endTime: "10:15",
            schedulingDateId: 23856,
            mealStartDate: "2023-03-15 11:15:00",
            mealEndDate: "2023-03-15 11:45:00",
            mealType: 0,
            totalMinute: 210,
            staffNameList: ["闾丘行"],
            staffNameListStr: "闾丘行",
            text: "闾丘行",
            positionNameList: ["收银"],
            positionNameListStr: "收银",
            dep: "收银",
            progress: 1,
            render: null,
            parent: 459671,
            priority: 1,
            type: "task",
          },
          {
            id: 459673,
            eid: "5354",
            createTime: "2023-03-15 15:35:46",
            updateTime: "2023-03-15 15:35:46",
            startDate: "2023-03-15 10:15:00",
            start_date: "2023-03-15 10:15:00",
            startTime: "10:15",
            endDate: "2023-03-15 11:15:00",
            end_date: "2023-03-15 11:15:00",
            endTime: "11:15",
            schedulingDateId: 23856,
            mealStartDate: "2023-03-15 11:15:00",
            mealEndDate: "2023-03-15 11:45:00",
            mealType: 0,
            totalMinute: 210,
            staffNameList: ["闾丘行"],
            staffNameListStr: "闾丘行",
            text: "闾丘行",
            positionNameList: ["收银"],
            positionNameListStr: "收银",
            dep: "收银",
            progress: 1,
            render: null,
            parent: 459671,
            priority: "1",
            type: "task",
          },
        ],
        // links: [
        // {id: 1, source: 1, target: 2, type: '0'}
        // ]
      },

      ////根据任务查询日历
      curTask: {},
      //日历显示范围（任务的起始日期到结束日期）
      dateRange: ["2000-01-01", "3000-01-01"],
    };
  },

  watch: {
    date: {
      handler(newValue, oldValue) {
        // console.log("点击切换月份：" + JSON.stringify(this.date));
        this.day = shiftSchedulingUtil.dateFormat(this.date);
        // this.$emit("dayChange", this.day);
      },
    },
  },

  created() {
    this.initDay().then(() => {
      this.$emit("initDayEnd");
    });
    this.$nextTick(() => {
      const prevBtn = document.querySelector(
        ".el-calendar__button-group .el-button-group>button:nth-child(1)"
      );

      // 点击前一个月
      prevBtn.addEventListener("click", (e) => {
        // this.$parent.listDateByCondition()
        this.$emit("listDateByCondition");
        console.log(e);
        // this.listDateByCondition();
        // const daTime = this.parseTime(this.value)
        // this.monthDay = daTime.substr(0, 7)
        // this.getToday()
        // console.log(this.monthDay)
      });

      // 点击下一个月
      const nextBtn = document.querySelector(
        ".el-calendar__button-group .el-button-group>button:nth-child(3)"
      );
      nextBtn.addEventListener("click", (e) => {
        this.$emit("listDateByCondition");
        console.log(e);
        // this.listDateByCondition();
        // this.$parent.listDateByCondition()
        // const daTime = this.parseTime(this.value)
        // this.monthDay = daTime.substr(0, 7)
        // console.log(this.monthDay)
        // this.getToday()
      });

      // 点击今天
      const prevBtn2 = document.querySelector(
        ".el-calendar__button-group .el-button-group>button:nth-child(2)"
      );
      prevBtn2.addEventListener("click", () => {
        this.$emit("listDateByCondition");
        // this.listDateByCondition();
        // this.$parent.listDateByCondition()
        // console.log(e)
        // this.monthDay = ''
        // console.log(this.monthDay)
        // this.getToday()
      });
    });
  },

  mounted() { },

  methods: {
    ////日历选择
    /**
     * 点击日历日期格
     * @param {*} data
     */
    dateClick(date, data) {
      this.dtitle = data.day;
      this.day = data.day;
      // console.log("date:" + date);
      // console.log(JSON.stringify(data));

      if (this.judgeWetherRest(date)) {
        this.$message.warning("今天休息，没有班次可查看哟");
      } else if (this.judgeWetherHaveShift(date) == false) {
        this.$message.warning("当前所选中的职位没有班次可查看哟");
      } else {
        let data = {
          dateId: this.dateEntityMap[new Date(date).toLocaleDateString()].id,
          userIdList: this.userIdList,
        };
        console.log("查询data：" + JSON.stringify(data));
        this.listShiftLoading = true;
        // 查询当天的所有班次信息
        schedulingShiftApi.listSchedulingShiftVoByDateId(data).then((res) => {
          // 根据选中日期返回当日日程甘特图数据载入tasks
          this.dialogVisible = true;
          // console.log("查询当天的班次列表：" + JSON.stringify(res));
          this.tasks.data = res.schedulingShiftVoList;
          // console.log("this.tasks.data:" + JSON.stringify(this.tasks.data));
          setTimeout(() => {
            this.listShiftLoading = false;
            this.$refs.ganttChar.reload();
          }, 100);
        });
      }
    },
    /**
     * 判断一天是否休息
     * @param {*} date
     */
    judgeWetherRest(date) {
      return shiftSchedulingUtil.judgeWetherRest(date, this.dateEntityMap);
    },
    /**
     * 判断一天是否有班次
     * @param {*} date
     */
    judgeWetherHaveShift(date) {
      return shiftSchedulingUtil.judgeWetherHaveShift(date, this.dateEntityMap);
    },

    /**
     * 判断一天是否在字典中有数据
     * @param {2023/3/1} dateStr
     */
    jubgeWhetherDateInMap(dateStr) {
      return shiftSchedulingUtil.jubgeWhetherDateInMap(
        dateStr,
        this.dateEntityMap
      );
    },

    ////根据任务来查询日历
    /**
     * 根据id获取任务的开始时间
     */
    getTaskStartDateByTaskId(id) {
      return new Promise((resolve, reject) => {
        schedulingTaskApi.info(id).then((res) => {
          this.curTask = res.schedulingTask;

          ////设置日历所显示的月份
          // console.log("curTask:" + JSON.stringify(this.curTask.startDate));
          this.date = new Date(this.curTask.startDate);
          let formatDate = shiftSchedulingUtil.dateFormat(this.date);
          // console.log("formatDate:" + formatDate);
          this.day = formatDate;

          // console.log("this.day:" + this.day);

          ////设置日历的显示范围
          this.dateRange = shiftSchedulingUtil.getDateRange(
            new Date(this.curTask.startDate),
            new Date(this.curTask.endDate)
          );
          console.log("日期限制:" + JSON.stringify(this.dateRange));
          // this.$emit("initDayEnd");
          resolve();
        });
      });
    },

    /**
     * 初始化Day
     */
    initDay() {
      console.log("调用initDay")
      return new Promise((resolve, reject) => {
        console.log(
          "schedulingCalendar,taskIdForSchedulingCalendar:" +
          this.taskIdForSchedulingCalendar
        );
        if (this.taskIdForSchedulingCalendar == undefined) {
          //--if--如果没有任务id
          this.day = shiftSchedulingUtil.getTodayDate();

          resolve();
        } else {
          this.getTaskStartDateByTaskId(this.taskIdForSchedulingCalendar).then(
            () => {
              resolve();
            }
          );
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped></style>
