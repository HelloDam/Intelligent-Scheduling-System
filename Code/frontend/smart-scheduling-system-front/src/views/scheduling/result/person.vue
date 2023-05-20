<template>
  <div class="postContainer">
    <el-row :gutter="10" class="contentRow">
      <el-col :span="4" class="contentCol">
        <div class="box-card">
          <div class="cardHeader">
            <span>员工列表</span>

            <span>
              <el-button id="button-primary" style="
                                                  font-weight: bold;
                                                  float: right;
                                                  padding: 3px 0;
                                                  margin-right: 5px;
                                                " type="text" icon="iconfont icon-huichangfuwu3-01"
                @click="appointMemberList()">
                指定人员
              </el-button>
            </span>
          </div>

          <el-divider id="elDivider"></el-divider>

          <div class="elTreeDiv">
            <el-table :data="selectUserList" style="width: 100%;height: 100%;">
              <el-table-column prop="username" label="用户名" align="center" />
              <el-table-column prop="name" label="姓名" align="center" />
            </el-table>
          </div>

        </div>
      </el-col>
      <el-col :span="20" class="contentCol">
        <div class="box-card">
          <div class="cardHeader">
            <span>排班日历</span>
            <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
          </div>

          <el-divider id="elDivider"></el-divider>

          <div class="calendarDiv">
            <SchedulingCalendar ref="schedulingCalendar" :dateEntityMap="dateEntityMap" :userIdList="userIdList"
              :taskIdForSchedulingCalendar="taskIdForSchedulingCalendar" @listDateByCondition="listDateByCondition"
              class="elCalendar" @initDayEnd="initDayEnd()">
            </SchedulingCalendar>
          </div>
        </div>
      </el-col>
    </el-row>

    <PersonSelect ref="userSelect" @userIdListChange="userIdListChange()" :taskPeriod="taskPeriod" :personType="0"
      v-if="isPersonSelect"></PersonSelect>

    <!-- <el-dialog :title="dtitle" :visible.sync="dialogVisible" width="90%" append-to-body>
      <div class="wrapper">
        <div class="container">
          <GanttChar
            class="left-container"
            ref="ganttChar"
            :tasks="tasks"
          ></GanttChar>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false"
          >确 定</el-button
        >
      </span>
    </el-dialog> -->
  </div>
</template>

<script>
import GanttChar from "../ganttx.vue";
import PersonSelect from "./personSelect";
import SchedulingCalendar from "./schedulingCalendar.vue";
import schedulingDateApi from "@/api/shiftScheduling/schedulingDate";
import schedulingShiftApi from "@/api/shiftScheduling/schedulingShift";
import shiftUserApi from "@/api/shiftScheduling/shiftUser";
import schedulingTaskApi from "@/api/shiftScheduling/schedulingTask";
import shiftSchedulingUtil from "@/utils/shiftScheduling/shiftSchedulingUtil";
import moment from "moment";

export default {
  name: "personView",
  components: { GanttChar, PersonSelect, SchedulingCalendar },
  props: {
    taskId: {
      type: Number,
    },
  },
  data() {
    return {
      // 遮罩层
      date: new Date(),
      dialogVisible: false,
      //甘特图对话框标题
      dtitle: "",
      //甘特图数据
      tasks: {
        data: [],
      },
      //组件是否构建完成
      isStructure: false,

      ////员工指定
      //所指定的员工id
      userIdList: [],

      ////排班日历
      day: undefined,
      //日历集合
      dateEntityMap: undefined,
      //日期是否初始化完成
      isInitDayEnd: false,
      initDayScheduleTask: undefined,

      ////指定员工
      //所选中的员工
      selectUserList: [],

      taskIdForSchedulingCalendar: undefined,

      tsakStartDate: "",
      taskPeriod: {},
      isPersonSelect: false,
    };
  },
  created() {
    // console.log("person,taskId:" + this.taskId);
    this.taskIdForSchedulingCalendar = this.taskId;
  },
  mounted() {
    this.$nextTick(() => {
      console.log("组件初始化完成")
      this.isStructure = true;
    })

  },
  methods: {
    //选择员工
    appointMemberList() {
      var s_date = new Date();
      var e_date = new Date();
      if (this.taskId != undefined) {
        // map.taskId = this.taskId;
        /**
         * 根据任务id获取起止时间 starttime endtime
         * 1、年份是否相同
         * 2、月份时间是否相同
         *    相同 -> 月包任务 -> 传starttime+endtime
         *    不同 -> 跨月任务 -> 传starttime+月尾 or 传月首+endtime or 月份首尾（跨多月任务）
         */
        schedulingTaskApi.info(this.taskId).then((res) => {
          let pmap = {};
          s_date = res.schedulingTask.startDate;
          e_date = res.schedulingTask.endDate;
          console.log("s_date" + s_date);
          console.log("e_date" + e_date);
          let taskStartYear = parseInt(
            moment(s_date).utcOffset(8).format("YYYY")
          );
          let taskEndYear = parseInt(
            moment(e_date).utcOffset(8).format("YYYY")
          );
          let taskStartMonth = parseInt(
            moment(s_date).utcOffset(8).format("MM")
          );
          let taskEndMonth = parseInt(moment(e_date).utcOffset(8).format("MM"));
          // console.log("当前测试位置");
          console.log("taskStartYear" + taskStartYear + typeof taskStartYear);
          console.log("taskEndYear" + taskEndYear);
          console.log("taskStartMonth" + taskStartMonth);
          console.log("taskEndMonth" + taskEndMonth);
          console.log(moment(s_date).endOf("month").format("YYYY-MM-DD"));
          if (taskStartYear == taskEndYear) {
            // 任务年份相同
            console.log("年份相同");
            if (taskStartMonth == taskEndMonth) {
              console.log("月份相同");
              // 任务月份相同  月包任务 -> 传starttime+endtime
              pmap.startDate = moment(s_date).utcOffset(8).format("YYYY-MM-DD");
              pmap.endDate = moment(e_date).utcOffset(8).format("YYYY-MM-DD");
            } else {
              // 跨月任务 -> 传starttime+月尾
              pmap.startDate = moment(s_date).utcOffset(8).format("YYYY-MM-DD");
              pmap.endDate = moment(e_date).endOf("month").format("YYYY-MM-DD");
            }
          } else {
            // 跨月任务 -> 传starttime+月尾
            pmap.startDate = moment(s_date).utcOffset(8).format("YYYY-MM-DD");
            pmap.endDate = moment(s_date).endOf("month").format("YYYY-MM-DD");
          }
          pmap.taskId = this.taskId;
          console.log("pmap:" + JSON.stringify(pmap));
          this.taskPeriod = pmap;
          console.log("this.taskPeriod:" + JSON.stringify(this.taskPeriod));
        });
      } else {
        this.taskPeriod.taskId = this.taskId;
        this.taskPeriod.startDate = moment()
          .startOf("months")
          .format("YYYY-MM-DD");
        this.taskPeriod.endDate = moment().endOf("months").format("YYYY-MM-DD");
        console.log("this.taskPeriod:" + JSON.stringify(this.taskPeriod));
      }
      this.isPersonSelect = true
      setTimeout(() => {
        this.$refs.userSelect.personSelectVisible = true;
      }, 100);
      // this.$refs.userSelect.personSelectVisible = true;
    },

    ////排班日历显示
    /**
     * 初始化日期结束
     */
    initDayEnd() {
      this.getData();
    },
    getData() {
      if (this.isStructure === true) {
        // console.log("日期初始化完成")
        this.listDateByCondition();
      } else {
        this.sleep();
      }
    },
    sleep() {
      if (this.initDayScheduleTask) {
        window.clearTimeout(this.this.initDayScheduleTask);
      }
      setTimeout(() => {
        this.getData();
      }, 500);
    },

    //查询
    listDateByCondition() {
      let map = {
        userIdList: this.userIdList,
        day: this.$refs.schedulingCalendar.day,
      };
      if (this.taskId) {
        map.taskId = this.taskId;
      }
      // console.log("map:" + JSON.stringify(map));
      schedulingDateApi.listDateByCondition(map).then((res) => {
        // console.log(JSON.stringify(res))
        this.dateEntityMap = res.dateEntityMap;
        // console.log("res" + JSON.stringify(this.dateEntityMap));
        let keys = Object.keys(this.dateEntityMap);
        if (keys.length == 0) {
          this.$message.warning("当前查询月份没有排班信息");
        }
        // console.log("this.dateEntityMap:" + JSON.stringify(this.dateEntityMap))
      });
    },
    /**
     * day发生变化
     */
    // dayChange() {
    //   this.listDateByCondition();
    // },

    ////选择员工
    userIdListChange() {
      this.userIdList = this.$refs.userSelect.getSelectedUserIdList();
      this.selectUserList = this.$refs.userSelect.getSelectedUserList();
      this.listDateByCondition();
      this.$message.success("指定成功");
    },
  },
};
</script>

<style lang="scss" scoped>
.postContainer {
  height: 100%;
  margin-top: 10px;

  .contentRow {
    height: 100%;

    .contentCol {
      height: 100%;

      .box-card {
        height: 100%;
        border-radius: 5px;
        background: rgba(242, 247, 251, 0.6);
        backdrop-filter: blur(50px);
        box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
          inset 1px 1px 1px rgba(0, 0, 0, 0.1);

        .cardHeader {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 20px;
          height: 50px;
        }

        #elDivider {
          margin: 0px 0px 0px 0px;
        }

        .elTreeDiv {
          border-radius: 5px;
          height: calc(100% - 50px);
          padding: 10px;
          // background: rgba(255, 255, 255, 0.9);
          // box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
          //   inset 1px 1px 1px rgba(0, 0, 0, 0.1);

          .elTree {
            border-radius: 5px;
            padding-top: 10px;
            height: 100%;
            background: rgba(255, 255, 255, 1);
            box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
              inset 1px 1px 1px rgba(0, 0, 0, 0.1);
          }
        }

        .calendarDiv {
          border-radius: 5px;
          height: calc(100% - 50px);
          padding: 10px;

          .elCalendar {
            border-radius: 5px;
            height: 100%;
            background: rgba(255, 255, 255, 1);
            box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
              inset 1px 1px 1px rgba(0, 0, 0, 0.1);
          }
        }

      }
    }
  }
}

.grid-content {
  padding: 5px 0;
}

.el-group-list.el-radio-group {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 8px 24px;
  background: #eef1f6;
}

.el-group-list.el-radio-group .el-radio-button:first-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button:last-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button:first-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button__inner {
  border-radius: 0px !important;
  border: none !important;
}

.el-group-list.el-radio-group .el-radio-button {
  border-bottom: 1px solid #f7f7f7 !important;
}

.el-group-list.el-radio-group {
  border: 1px solid #dcdfe6;
}

.el-group-list.el-radio-group>label>span {
  width: 100%;
  text-align: left;
  padding-left: 20px;
}

.wrapper {
  height: 450px;
}

.container {
  height: 100%;
  width: 100%;
}

.left-container {
  overflow: hidden;
  position: relative;
  height: 100%;
}
</style>
