<template>
  <div class="postContainer">
    <el-row :gutter="10" class="contentRow">
      <el-col :span="spanSize1" class="contentCol">
        <div class="box-card">
          <div class="cardHeader">
            <span>职位选择</span>
            <span>
              <el-button id="button-black" style="font-weight: bold;float: right;padding: 3px 0; margin-right: 5px;"
                type="text" icon="iconfont icon-expand" v-if="isExpand == false" size="mini"
                @click="foldOrExpand()"></el-button>
              <el-button id="button-black" style="font-weight: bold;float: right;padding: 3px 0;margin-right: 5px;"
                type="text" icon="iconfont icon-zhedie" v-if="isExpand == true" size="mini"
                @click="foldOrExpand()"></el-button>
              <el-button id="button-black" style="font-weight: bold; float: right;padding: 3px 0;margin-right: 5px;"
                type="text" icon="iconfont icon-ziyuan" v-if="isSelectAll == false" size="mini"
                @click="selectAllOrNot()"></el-button>
              <el-button id="button-black" style="font-weight: bold;float: right;padding: 3px 0;margin-right: 5px;"
                type="text" icon="iconfont icon-ziyuan1" v-if="isSelectAll == true" size="mini"
                @click="selectAllOrNot()"></el-button>
            </span>
          </div>

          <el-divider id="elDivider"></el-divider>

          <div class="elTreeDiv">
            <el-tree ref="positionSelectTree" :props="positionProps" :data="positionSelectTree" show-checkbox
              node-key="id" :default-checked-keys="positionIdArr" :default-expand-all="true" @check="listDateByCondition"
              class="elTree">
            </el-tree>
          </div>
        </div>
      </el-col>
      <el-col :span="spanSize2" class="contentCol">
        <div class="box-card">
          <div class="cardHeader">
            <span>排班日历</span>
            <el-switch v-model="isAllocate" active-color="#13ce66" inactive-color="#ff4949" active-text="已分配"
              inactive-text="未分配" style="float: right;" @change="listDateByCondition()">
            </el-switch>
          </div>

          <el-divider id="elDivider"></el-divider>

          <div class="calendarDiv">
            <el-calendar :range="dateRange" v-model="date" v-loading="calendarLoading"
              :element-loading-text="calendarLoadingStr" class="elCalendar">
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
                      <el-tag type="warning">班次</el-tag>
                    </h1>
                  </span>
                </div>
              </template>
            </el-calendar>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="dtitle" :visible.sync="dialogVisible" width="90%" class="parent" append-to-body>
      <!-- <div style="padding-bottom:10px">
        <el-button @click="exportPDF">导出PDF</el-button>
      </div> -->
      <div slot="title" class="dialog-title">
        <span class="dialog-title-text">{{ dtitle }}</span>
        <!-- <el-button type="primary" style="">编辑</el-button> -->
        <el-tag effect="dark" type="" style="margin-left:30px">
          班次
        </el-tag>
        <el-tag effect="dark" type="success" style="margin-left:5px">
          午餐
        </el-tag>
        <el-tag effect="dark" type="danger" style="margin-left:5px">
          晚餐
        </el-tag>
      </div>
      <!-- <el-divider></el-divider> -->
      <div style="text-align:center;font-size:14px;color:white;">
        <el-row>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-banciweihu" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>班次总数量：{{ this.ganttStatisticsVo.totalShiftNum }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-shichang" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>班次总时长：{{ this.ganttStatisticsVo.totalShiftMinute }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-yuangong1" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>参与员工数量：{{ this.ganttStatisticsVo.staffNum }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-shichang" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>员工平均工作时长：{{ this.ganttStatisticsVo.averageWorkMinute }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-zhongcan" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>吃午饭人数：{{ this.ganttStatisticsVo.lunchPersonNum }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="grid-content bg-blue">
              <span class="iconfont icon-canpinhui-xican" style="font-size: 14px; color: white; margin-right: 5px;"></span>
              <span>吃晚餐人数：{{ this.ganttStatisticsVo.dinnerPersonNum }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

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

    <PersonSelect ref="userSelect" v-model="linetask" @userIdListChange="userIdListChange()" :key="personSelectKey"
      :personType="1">
    </PersonSelect>

    <!-- <el-dialog
      title="导出PDF"
      :visible.sync="pdfDialogVisible"
      width="40%"
      :before-close="pdfHandleClose"
    >
      <div>
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="文件名称">
            <el-input v-model="form.name"></el-input>
          </el-form-item>
        </el-form>

      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pdfDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="exppdf"
          >确 定</el-button
        >
      </span>
    </el-dialog> -->

  </div>
</template>

<script>
import GanttChar from "../ganttx.vue";
import positionApi from "@/api/enterprise/position";
import schedulingDateApi from "@/api/shiftScheduling/schedulingDate";
import schedulingShiftApi from "@/api/shiftScheduling/schedulingShift";
import shiftUserApi from '@/api/shiftScheduling/shiftUser'
import { gantt } from "dhtmlx-gantt";
import PersonSelect from "./personSelect";
import schedulingTaskApi from "@/api/shiftScheduling/schedulingTask";

///工具js
import shiftSchedulingUtil from "@/utils/shiftScheduling/shiftSchedulingUtil";

export default {
  name: "SkillView",
  components: { GanttChar, PersonSelect },
  props: {
    taskId: {
      type: Number
    }
  },
  data() {
    return {
      //// 遮罩层
      date: new Date(),
      dialogVisible: false,
      dialogVisible2: false,
      pdfDialogVisible: false,
      dtitle: "",
      selectedType: null,
      skillType: [],
      tasks: {
        data: [
          {
            id: 1, text: '张三', eid: '121212', startDate: "2023-03-15 08:00:00", start_date: "2023-03-15 08:00:00", startTime: "08:00",
            endDate: "2023-03-15 9:00:00", end_date: "2023-03-15 9:00:00", endTime: "9:00", progress: 1.0, type: 'task'
          },
          {
            id: 2, text: '张三', eid: '121213', startDate: "2023-03-15 08:00:00", start_date: "2023-03-15 08:00:00", startTime: "08:00",
            endDate: "2023-03-15 9:00:00", end_date: "2023-03-15 9:00:00", endTime: "9:00", progress: 0, render: "split", parent: null, type: 'task'
          },
          {
            id: 3, text: '张三', eid: '121212', startDate: "2023-03-15 08:00:00", start_date: "2023-03-15 08:00:00", startTime: "08:00",
            endDate: "2023-03-15 9:00:00", end_date: "2023-03-15 9:00:00", endTime: "9:00", progress: 0, parent: 2, render: null, priority: 1, type: 'task'
          },
          {
            id: 4, text: '张三', eid: '121212', startDate: "2023-03-15 09:00:00", start_date: "2023-03-15 09:00:00", startTime: "09:00",
            endDate: "2023-03-15 10:00:00", end_date: "2023-03-15 10:00:00", endTime: "10:00", progress: 0, parent: 2, priority: 3, type: 'task'
          },
          {
            id: 5, text: '张三', eid: '121212', startDate: "2023-03-15 09:00:00", start_date: "2023-03-15 09:00:00", startTime: "09:00",
            endDate: "2023-03-15 10:00:00", end_date: "2023-03-15 10:00:00", endTime: "10:00", progress: 0, priority: '3', type: 'task'
          },
          {
            "id": 459671, "eid": "5354", "createTime": "2023-03-15 15:35:46", "updateTime": "2023-03-15 15:35:46", "startDate": "2023-03-15 08:30:00",
            "start_date": "2023-03-15 08:30:00", "startTime": "08:30", "endDate": "2023-03-15 11:15:00", "end_date": "2023-03-15 11:15:00", "endTime": "11:15",
            "schedulingDateId": 23856, "mealStartDate": "2023-03-15 11:15:00", "mealEndDate": "2023-03-15 11:45:00", "mealType": 0, "totalMinute": 210,
            "staffNameList": ["闾丘行"], "staffNameListStr": "闾丘行", "text": "闾丘行", "positionNameList": ["收银"], "positionNameListStr": "收银", "dep": "收银",
            "progress": 1, "render": "split", "parent": null, "priority": 1, 'type': 'task'
          },
          {
            "id": 459672, "eid": "5354", "createTime": "2023-03-15 15:35:46", "updateTime": "2023-03-15 15:35:46", "startDate": "2023-03-15 08:30:00",
            "start_date": "2023-03-15 08:30:00", "startTime": "08:30", "endDate": "2023-03-15 10:15:00", "end_date": "2023-03-15 10:15:00", "endTime": "10:15",
            "schedulingDateId": 23856, "mealStartDate": "2023-03-15 11:15:00", "mealEndDate": "2023-03-15 11:45:00", "mealType": 0, "totalMinute": 210,
            "staffNameList": ["闾丘行"], "staffNameListStr": "闾丘行", "text": "闾丘行", "positionNameList": ["收银"], "positionNameListStr": "收银", "dep": "收银",
            "progress": 1, "render": null, "parent": 459671, "priority": 1, 'type': 'task'
          },
          {
            "id": 459673, "eid": "5354", "createTime": "2023-03-15 15:35:46", "updateTime": "2023-03-15 15:35:46", "startDate": "2023-03-15 10:15:00",
            "start_date": "2023-03-15 10:15:00", "startTime": "10:15", "endDate": "2023-03-15 11:15:00", "end_date": "2023-03-15 11:15:00", "endTime": "11:15",
            "schedulingDateId": 23856, "mealStartDate": "2023-03-15 11:15:00", "mealEndDate": "2023-03-15 11:45:00", "mealType": 0, "totalMinute": 210,
            "staffNameList": ["闾丘行"], "staffNameListStr": "闾丘行", "text": "闾丘行", "positionNameList": ["收银"], "positionNameListStr": "收银", "dep": "收银",
            "progress": 1, "render": null, "parent": 459671, "priority": '1', 'type': 'task'
          },
        ],
        // links: [
        // {id: 1, source: 1, target: 2, type: '0'}
        // ]
      },

      ////加载
      //查询班次加载
      calendarLoading: false,
      calendarLoadingStr: "",

      ////职位选择
      //职位选择树
      positionSelectTree: [],
      isExpand: true,
      isSelectAll: false,
      //选中的职位
      positionIdArr: [],
      //展示的内容对应
      positionProps: {
        label: "label",
        children: "children",
      },

      ////排班日历
      day: undefined,
      //日历集合
      dateEntityMap: undefined,
      //当前所选定的日期
      curSelectDate: undefined,
      // 编辑传入linetask
      linetask: [],
      // exportPDF params
      form: {
        name: '',
      },

      ////指定员工
      isShowPersonSelect: false,
      personSelectKey: 0,


      ////根据任务查询日历
      curTask: {},
      //日历显示范围（任务的起始日期到结束日期）
      dateRange: ["2000-01-01", "3000-01-01"],

      ////是否选择分配班次
      spanSize1: 4,
      spanSize2: 20,
      isAllocate: true,
      ganttStatisticsVo: {},
      monthDay: '',
      value: new Date(),
      tidToperselect:0,
    };
  },
  watch: {
    date: {
      //切换月份
      handler(newValue, oldValue) {
        // console.log("点击切换月份：" + JSON.stringify(this.date));
        console.log('日期变化？')
        this.day = shiftSchedulingUtil.dateFormat(this.date);
        // console.log("天" + JSON.stringify(this.day));
        // this.listDateByCondition();
      },
    },
  },
  created() {
    this.task = this.tasks
    this.initPositionTree();
    this.$nextTick(() => {
      const prevBtn = document.querySelector(
        '.el-calendar__button-group .el-button-group>button:nth-child(1)'
      )

      // 点击前一个月
      prevBtn.addEventListener('click', e => {
        // console.log(e)
        this.listDateByCondition();
        // const daTime = this.parseTime(this.value)
        // this.monthDay = daTime.substr(0, 7)
        // this.getToday()
        // console.log(this.monthDay)
      })

      // 点击下一个月
      const nextBtn = document.querySelector(
        '.el-calendar__button-group .el-button-group>button:nth-child(3)'
      )
      nextBtn.addEventListener('click', (e) => {
        // console.log(e)
        this.listDateByCondition();
        // const daTime = this.parseTime(this.value)
        // this.monthDay = daTime.substr(0, 7)
        // console.log(this.monthDay)
        // this.getToday()
      })

      // 点击今天
      const prevBtn2 = document.querySelector('.el-calendar__button-group .el-button-group>button:nth-child(2)')
      prevBtn2.addEventListener('click', () => {
        this.listDateByCondition();
        // console.log(e)
        // this.monthDay = ''
        // console.log(this.monthDay)
        // this.getToday()
      })
    })
  },
  mounted() {
    this.initDay().then(() => {
      console.log("this.day:" + this.day)
      var thisa = this;
      console.log('mounted？')
      this.listDateByCondition();
      this.personSelectKey = new Date().getTime();
      gantt.attachEvent("onTaskDblClick", function (id, e) {
        thisa.linetask = [];
        console.log('id:'+id)
        for (var key in thisa.tasks.data) {
          var i = thisa.tasks.data[key].id.toString();
          // console.log('idididid')
          var tid = id;
          if (id.includes("|")) {
            var idx = id.indexOf("|")
            tid = id.substring(0, idx);
            console.log('tid:'+tid);
          }

          if (i.includes(tid)) {
            // console.log("匹配" + id)
            // console.log(thisa.tasks.data[key]);
            thisa.linetask.push(thisa.tasks.data[key])
          }
        }
        thisa.tidToperselect = id
        console.log('double click task')
        console.log(this.tidToperselect)
        console.log(id)
        setTimeout(() => {
          thisa.$refs.userSelect.getUserInfo();
          thisa.$refs.userSelect.personSelectVisible = true;
        }, 100);
      });
    })

  },
  methods: {
    ////职位选择
    //初始化职位树
    initPositionTree() {
      positionApi.getPositionSelectTree().then((res) => {
        this.positionSelectTree = res.positionTree;
        // for (let i = 0; i < this.positionSelectTree.length; i++) {
        //     this.positionIdArr.push(this.positionSelectTree[i].id);
        // }
        // console.log("this.positionIdArr:"+JSON.stringify(this.positionIdArr))
      });
    },
    //是否全选
    selectAllOrNot() {
      this.isSelectAll = !this.isSelectAll;
      if (this.isSelectAll === true) {
        for (let i = 0; i < this.positionSelectTree.length; i++) {
          this.positionIdArr.push(this.positionSelectTree[i].id);
        }
        this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
        this.listDateByCondition();
      } else {
        this.positionIdArr.length = 0;
        this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
        this.listDateByCondition();
      }
      // console.log("this.positionIdArr:" + JSON.stringify(this.positionIdArr))
    },
    // 一键折叠/展开
    foldOrExpand() {
      this.isExpand = !this.isExpand;
      this.expandFunc(this.positionSelectTree);
    },
    // 遍历树形数据，通过设置每一项的expanded属性，实现展开与折叠
    expandFunc(data) {
      data.forEach((item) => {
        this.$refs.positionSelectTree.store.nodesMap[item.id].expanded =
          this.isExpand;
        if (item.children != null && item.children.length > 0) {
          this.expandFunc(item.children);
        }
      });
    },
    //查询
    listDateByCondition() {
      this.calendarLoading = true;
      this.calendarLoadingStr = "正在查询日历信息";
      this.positionIdArr = this.$refs.positionSelectTree.getCheckedKeys();
      let map = { positionIdList: this.positionIdArr, day: this.day };
      if (this.taskId != undefined) {
        map.taskId = this.taskId;
      }
      map.isSearchUnAssignedShifts = !this.isAllocate;
      if (this.isAllocate === true) {
        console.log("已分配")
        this.spanSize1 = 4;
        this.spanSize2 = 20;
      } else {
        console.log("未分配")
        this.spanSize1 = 0;
        this.spanSize2 = 24;
      }
      // console.log("map:" + JSON.stringify(map))
      schedulingDateApi.listDateByCondition(map).then((res) => {
        // console.log(JSON.stringify(res))
        this.dateEntityMap = res.dateEntityMap;
        this.calendarLoading = false;
        // console.log("this.dateEntityMap:" + JSON.stringify(this.dateEntityMap))
        let keys = Object.keys(this.dateEntityMap);
        if (keys.length == 0) {
          this.$message.warning("当前查询月份没有排班信息")
        }
      });
    },

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
      this.curSelectDate = date;

      if (this.judgeWetherRest(date)) {
        this.$message.warning("今天休息，没有班次可查看哟");
      } else if (this.judgeWetherHaveShift(date) == false) {
        this.$message.warning("当前所选中的职位没有班次可查看哟");
      } else {
        console.log("哈哈哈哈哈")
        // 查询当天的所有班次信息
        this.calendarLoading = true;
        this.calendarLoadingStr = "正在查询当天的班次信息";
        this.listSchedulingShiftVoByDate(date);
      }
    },
    /**
     * 查询所选定日期的班次列表
     */
    listSchedulingShiftVoByDate(date) {
      this.positionIdArr = this.$refs.positionSelectTree.getCheckedKeys();
      let data = { "dateId": this.dateEntityMap[new Date(date).toLocaleDateString()].id, "positionIdArr": this.positionIdArr }
      data.isSearchUnAssignedShifts = !this.isAllocate;
      // console.log(data)
      schedulingShiftApi
        .listSchedulingShiftVoByDateId(
          data
        )
        .then((res) => {
          console.log("查询成功")
          // 根据选中日期返回当日日程甘特图数据载入tasks
          this.dialogVisible = true;
          // console.log("查询当天的班次列表：" + JSON.stringify(res));
          this.tasks.data = res.schedulingShiftVoList;
          this.ganttStatisticsVo = res.ganttStatisticsVo;
          this.ganttStatisticsVo.averageWorkMinute = this.ganttStatisticsVo.averageWorkMinute.toFixed(2)
          // console.log(this.ganttStatisticsVo)
          // console.log("ganttstatisticsVo:" + JSON.stringify(res.ganttStatisticsVo));
          setTimeout(() => {
            console.log('reloadgant')
            this.calendarLoading = false;
            this.$refs.ganttChar.reload();
            // thisa.isShowPersonSelect = true;
          }, 100);
        });
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
      return shiftSchedulingUtil.jubgeWhetherDateInMap(dateStr, this.dateEntityMap);
    },


    // exportPDF(){
    //   this.pdfDialogVisible = true
    // },

    // exppdf(){
    //     gantt.exportToPDF({
    //       name: this.form.name,
    //       header:"<h1>My company</h1>",
    //       footer:"<h4>Bottom line</h4>",
    //       locale:"en",
    //       // start:"01-04-2013",
    //       // end:"11-04-2013",
    //       skin:'terrace',
    //       data:this.tasks,
    //       server:"https://myapp.com/myexport/gantt",
    //       raw:true,
    //       callback: function(res){
    //           alert(res.url);
    //       }
    //     });
    //     this.pdfDialogVisible = false
    // },


    ////班次指定员工
    userIdListChange() {
      console.log(this.tidToperselect)
      let userIdList = this.$refs.userSelect.getSelectedUserIdList();
      //0：替换班次原本的员工 1：给班次追加员工
      let appointType = this.$refs.userSelect.getAppointType();
      //指定员工完成
      this.isShowPersonSelect = false;
      let params = {
        shiftStartDate: this.linetask[0].startDate,
        shiftEndDate: this.linetask[0].endDate,
        appointType: appointType,
        userIdList: userIdList, 
        shiftId: this.tidToperselect,
      }
      shiftUserApi.replaceOrAddMembersForShift(params).then(
        res => {
          this.$message.success("操作成功");
          // 清空已选择的员工集合
          this.$refs.userSelect.clearMemberList();
          // 重新查询当天的所有班次信息
          this.listSchedulingShiftVoByDate(this.curSelectDate);
        }
      )
    },


    ////根据任务来查询日历
    /**
     * 根据id获取任务的开始时间
     */
    getTaskStartDateByTaskId(id) {
      return new Promise((resolve, reject) => {
        schedulingTaskApi.info(id).then(
          res => {
            this.curTask = res.schedulingTask;

            ////设置日历所显示的月份
            // console.log("curTask:" + JSON.stringify(this.curTask.startDate));
            this.date = new Date(this.curTask.startDate);
            let formatDate = shiftSchedulingUtil.dateFormat(this.date);
            // console.log("formatDate:" + formatDate);
            this.day = formatDate;

            ////设置日历的显示范围
            this.dateRange = shiftSchedulingUtil.getDateRange(new Date(this.curTask.startDate), new Date(this.curTask.endDate));
            console.log("日期限制:" + JSON.stringify(this.dateRange))
            resolve();
          }
        )
      })

    },
    /**
     * 初始化Day
     */
    initDay() {
      return new Promise((resolve, reject) => {
        if (this.taskId == undefined) {
          this.day = shiftSchedulingUtil.getTodayDate();
          resolve();
        } else {
          this.getTaskStartDateByTaskId(this.taskId).then(() => {
            resolve();
          })
        }
      })
    },

    /**
     * 获取指定方式
     */
    getAppointType() {
      return this.isAllocate == true ? 0 : 1;
    },

    isAssigned() {
      // console.log('isAssigned' + this.getAppointType)
      // let x = this.getAppointType()
      // console.log(x)
      if (this.getAppointType()) {
        console.log('已分配');
        this.listDateByCondition();
      } else {
        console.log('未分配');
      }
    }

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
            background: rgba(255, 255, 255,1);
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

.bg-purple {
  background: #d3dce6;
}

.bg-blue{
  background: #3a8ee6;
}

.el-dialog__body {
  padding-top: 0px;
}
</style>
