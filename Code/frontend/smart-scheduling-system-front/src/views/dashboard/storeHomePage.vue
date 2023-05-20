<template>
  <div class="container">
    <el-row :gutter="10" class="bigRow">
      <!-- 统计数据 -->
      <el-col :span="18" class="bigCol">
        <div class="messageDiv">

          <div class="chartTitleDiv" v-if="userType === 1">
            <span class="dateSpan">{{ year }} 年 {{ month }} 月</span>
            <span class="pageTitle">企 业 信 息 监 控</span>
            <monthSelect ref="monthSelect" @monthChange="monthChange()" style="display: inline-block;"></monthSelect>
          </div>

          <div class="chartTitleDiv1" v-if="userType === 2">
            <span class="pageTitle">门 店 信 息 监 控</span>
            <yearMonthSelect ref="yearMonthSelect" class="yearMonthSelect" :type="0" @yearMonthChange="yearMonthChange()">
            </yearMonthSelect>
          </div>

          <!-- 展示企业管理员的统计数据 -->
          <div v-if="userType === 1">
            <el-row :gutter="10">
              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class="statisticsItem" style="background: #4194cb;">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-renwujincheng"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计任务数: {{ statisticsVo.totalTaskInYear }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalTaskInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="monthDataSpan">当月累计任务数: {{ statisticsVo.totalTaskInMonth }}</span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalTaskInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>

              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class=" statisticsItem" style="background:#46a2da;">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-liuliang"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                          <!-- <svg-icon></svg-icon> -->
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计客流量: {{
                              Number(statisticsVo.totalPassengerFlowInYear).toFixed(2)
                            }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalPassengerFlowInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当月累计客流量: {{
                              Number(statisticsVo.totalPassengerFlowInMonth).toFixed(2)
                            }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalPassengerFlowInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>

              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class="statisticsItem" style="background:#58afdd">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-a-Calendarshift"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                          <!-- <svg-icon></svg-icon> -->
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计班次数: {{ statisticsVo.totalShiftNumInYear }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalShiftNumInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当月累计班次数: {{ statisticsVo.totalShiftNumInMonth }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalShiftNumInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading1" element-loading-spinner="el-icon-loading">
                  <!-- 门店用户数 -->
                  <div class="use-title">
                    门店用户数
                    <el-tooltip content="统计各门店的用户数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <storeUserNumBarChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading1 = false"></storeUserNumBarChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading2" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    员工日均工作时长
                    <el-tooltip content="员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <!-- 员工日均工作时长 -->
                  <storeAverageStaffWorkTimeLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading2 = false">
                  </storeAverageStaffWorkTimeLineChart>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading3" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    门店日均班次数量
                    <el-tooltip content="门店日均班次数量 = 当月总班次数量/工作日数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <!-- 每个门店的日均班次数量 -->
                  <storeShiftNumLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading3 = false">
                  </storeShiftNumLineChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading4" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    门店日均班次分配率
                    <el-tooltip content="门店日均班次分配率 = (当月已分配班次数量/当月总班次数量)*100" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <!-- 每个门店的班次分配率 -->
                  <storeShiftAssignRateLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading4 = false">
                  </storeShiftAssignRateLineChart>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading5" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    门店日均客流量
                    <el-tooltip content="门店日均客流量 = (当月总客流量/工作日数量)" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <!-- 每个门店的日均客流量 -->
                  <storeAveragePassengerFlowChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading5 = false">
                  </storeAveragePassengerFlowChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading6" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    午餐、晚餐数量
                    <el-tooltip content="当月的总午餐、晚餐数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <totalLunchNumAndDinnerNumLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading6 = false">
                  </totalLunchNumAndDinnerNumLineChart>
                  <!-- <pieChart></pieChart> -->
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 展示门店管理员的统计数据 -->
          <div v-if="userType === 2">
            <el-row :gutter="10">
              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class="statisticsItem" style="background: #4194cb;">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-renwujincheng"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计任务数: {{ statisticsVo.totalTaskInYear }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalTaskInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="monthDataSpan">当月累计任务数: {{ statisticsVo.totalTaskInMonth }}</span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalTaskInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>

              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class=" statisticsItem" style="background:#46a2da;">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-liuliang"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                          <!-- <svg-icon></svg-icon> -->
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计客流量: {{
                              Number(statisticsVo.totalPassengerFlowInYear).toFixed(2)
                            }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalPassengerFlowInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当月累计客流量: {{
                              Number(statisticsVo.totalPassengerFlowInMonth).toFixed(2)
                            }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalPassengerFlowInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>

              <el-col :span="8">
                <div>
                  <el-card body-style="padding:0px" class="statisticsItem" style="background:#58afdd">
                    <el-row>
                      <el-col :span="6">
                        <div class="grid-content" style="text-align: center">
                          <i class="iconfont icon-a-Calendarshift"
                            style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;"></i>
                          <!-- <svg-icon></svg-icon> -->
                        </div>
                      </el-col>
                      <el-col :span="18">
                        <div class="grid-content card-text">
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当年累计班次数: {{ statisticsVo.totalShiftNumInYear }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalShiftNumInYear' :duration='duration' /> -->
                          </div>
                          <div style="min-height: 30px">
                            <span class="yearDataSpan">当月累计班次数: {{ statisticsVo.totalShiftNumInMonth }} </span>
                            <!-- <CountTo :startVal='startVal' :endVal='statisticsVo.totalShiftNumInMonth' :duration='duration' /> -->
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading7" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    员工日均工作时长
                    <el-tooltip content="统计当前门店指定年份各月的员工日均工作时长" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <monthAverageStaffWorkTimeLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading7 = false">
                  </monthAverageStaffWorkTimeLineChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading8" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    每月午餐、晚餐数量
                    <el-tooltip content="员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <monthLunchNumAndDinnerNumLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading8 = false">
                  </monthLunchNumAndDinnerNumLineChart>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading9" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    每月日均班次数量
                    <el-tooltip content="门店日均班次数量 = 当月总班次数量/工作日数量" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <monthShiftNumLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading9 = false">
                  </monthShiftNumLineChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading10" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    每月日均班次分配率
                    <el-tooltip content="日均班次分配率 = (当月已分配班次数量/当月总班次数量)*100" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <monthShiftAssignRateLineChart :year="year" :month="month" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading10 = false">
                  </monthShiftAssignRateLineChart>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="chart-card" v-loading="loading11" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    当月日均工作时间最长的前<el-input v-model="num1" placeholder="" size="mini" style="display: inline-block;max-width: 50px;" ></el-input>位员工
                    <el-tooltip content="当月日均工作时间最长的前十员工" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <maxWorkTimeUserBarChart :year="year" :month="month" :num="num1" :type="0" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading11 = false">
                  </maxWorkTimeUserBarChart>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-card" v-loading="loading12" element-loading-spinner="el-icon-loading">
                  <div class="use-title">
                    当月日均工作时间最短的前<el-input v-model="num2" placeholder="" size="mini" style="display: inline-block;max-width: 50px;" ></el-input>位员工
                    <el-tooltip content="当月日均工作时间最长的前十员工" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </div>
                  <maxWorkTimeUserBarChart :year="year" :month="month" :num="num2" :type="1" :timeChange="timeChange" :size="curTime"
                    @receiveData="loading12 = false">
                  </maxWorkTimeUserBarChart>
                </div>
              </el-col>
            </el-row>
          </div>

        </div>
      </el-col>

      <!-- 通知查看 -->
      <el-col :span="6" class="bigCol">
        <div class="messageDiv">
          <div class="block">
            <div class="inform-header">
              <span style="font-size: 18px;font-weight: bold;">通 知</span>
            </div>
            <el-divider></el-divider>
            <div class="messageInsideDiv" style="height: calc(100% - 70px - 50px); ">
              <!--     :icon="message.icon"
                    :type="message.type"
                    :color="message.color"
                    :size="message.size"
                    :timestamp="message.timestamp" -->
              <!-- <el-scrollbar style="height: 100%">
                <el-timeline>
   
                  <el-timeline-item placement="top" v-for="(message, index) in messageList" :key="index"
                    icon="el-icon-more" type="primary" size="large" :timestamp="message.publishTime">
                    <div style="margin-right: 25px">
                      <el-card>
                        <div v-html="message.content"></div>
                      </el-card>
                    </div>
                  </el-timeline-item>
                </el-timeline>
              </el-scrollbar> -->

              <messageItem v-for="(message, index) in messageList" :key="index" :messageItemVo="message"></messageItem>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
////组件
import lineChart from './lineChart.vue';
import lineChartCustomerFlow from './lineChartCustomerFlow.vue';
import pieChart from './pieChart.vue';
import CountTo from 'vue-count-to';
import messageItem from './MessageItem.vue';
///企业统计信息
import storeUserNumBarChart from './enterpriseManager/storeUserNumBarChart.vue';
import storeAverageStaffWorkTimeLineChart from './enterpriseManager/storeAverageStaffWorkTimeLineChart.vue';
import storeShiftNumLineChart from './enterpriseManager/storeShiftNumLineChart.vue';
import storeShiftAssignRateLineChart from './enterpriseManager/storeShiftAssignRateLineChart.vue';
import storeAveragePassengerFlowChart from '@/views/dashboard/enterpriseManager/storeAveragePassengerFlowChart';
import totalLunchNumAndDinnerNumLineChart from '@/views/dashboard/enterpriseManager/totalLunchNumAndDinnerNumLineChart';
///门店统计信息
import monthAverageStaffWorkTimeLineChart from './storeManager/monthAverageStaffWorkTimeLineChart.vue';
import monthLunchNumAndDinnerNumLineChart from '@/views/dashboard/storeManager/monthLunchNumAndDinnerNumLineChart'
import monthShiftNumLineChart from '@/views/dashboard/storeManager/monthShiftNumLineChart'
import monthShiftAssignRateLineChart from '@/views/dashboard/storeManager/monthShiftAssignRateLineChart'
import maxWorkTimeUserBarChart from '@/views/dashboard/storeManager/maxWorkTimeUserBarChart'
///时间选择工具
import monthSelect from '@/components/MonthSelect/monthSelect.vue';
import yearMonthSelect from '@/components/YearMonthSelect/yearMonthSelect.vue';

////js
import messageApi from '@/api/enterprise/message';
import shiftSchedulingStatisticsApi from '@/api/statistics/shiftSchedulingStatistics';
import { mapGetters } from 'vuex'

export default {
  components: {
    lineChart, pieChart, lineChartCustomerFlow, CountTo, messageItem,
    storeUserNumBarChart, storeAverageStaffWorkTimeLineChart, storeShiftNumLineChart, storeShiftAssignRateLineChart,
    storeAveragePassengerFlowChart, totalLunchNumAndDinnerNumLineChart, monthSelect, yearMonthSelect,
    monthAverageStaffWorkTimeLineChart, monthLunchNumAndDinnerNumLineChart, monthShiftNumLineChart, monthShiftAssignRateLineChart,
    maxWorkTimeUserBarChart
  },
  computed: {
    ...mapGetters([
      'userType'
    ])
  },

  data() {
    return {
      messageList: [],
      statisticsVo: {},
      CustomerFlowdata: [],
      startVal: 0,
      endVal: 100,
      duration: 3000,

      year: undefined,
      month: undefined,

      curTime: 0,
      timeChange: 0,

      num1:"10",
      num2:"10",

      ///echart加载
      loading1: true,
      loading2: true,
      loading3: true,
      loading4: true,
      loading5: true,
      loading6: true,
      loading7: true,
      loading8: true,
      loading9: true,
      loading10: true,
      loading11: true,
      loading12: true
    };
  },

  created() {

    window.addEventListener('resize', this.handleResize);

    //接收消息
    this.listMessageOfUser().then(() => {
      let messageBox = document.querySelector('.messageInsideDiv');
      //将滚动条的滑块滚动到底部
      messageBox.scrollTop = messageBox.scrollHeight;
    })
    if (this.userType == 1) {
      //获取统计信息
      this.getStatisticsVoByEnterpriseId();
    } else if (this.userType == 2) {
      this.getStatisticsVoByStoreId();
    }

  },

  mounted() {
    if (this.userType === 1) {
      this.year = this.$refs.monthSelect.getYear();
      this.month = this.$refs.monthSelect.getMonth();
      this.timeChange = this.timeChange + 1;
    } else if (this.userType === 2) {
      this.year = this.$refs.yearMonthSelect.getYear();
      this.month = this.$refs.yearMonthSelect.getMonth();
      this.timeChange = this.timeChange + 1;
    }
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {

    /**
     * 这里执行窗口大小变化后的逻辑
     */
    handleResize() {
      console.log('窗口大小变化了');
      // 强制组件重新渲染
      this.curTime = new Date().getTime();
    },

    /**
     * 获取统计信息
     */
    getStatisticsVoByEnterpriseId() {
      shiftSchedulingStatisticsApi.getStatisticsVoByEnterpriseId().then(
        res => {
          this.statisticsVo = res.statisticsVo;
          // console.log("this.statisticsVo:"+this.statisticsVo)
        }
      )
    },
    getStatisticsVoByStoreId() {
      shiftSchedulingStatisticsApi.getStatisticsVoByStoreId().then(
        res => {
          this.statisticsVo = res.statisticsVo;
          // console.log("this.statisticsVo:"+this.statisticsVo)
        }
      )
    },

    /**
     * 接收信息
     */
    listMessageOfUser() {
      return new Promise((resolve, reject) => {
        messageApi.listMessageOfUser().then(
          res => {
            this.messageList = res.messageList;
            resolve();
          }
        )
      })

    },

    /**
    * 月份发生改变
    */
    monthChange() {
      this.year = this.$refs.monthSelect.getYear();
      this.month = this.$refs.monthSelect.getMonth();
      this.timeChange = this.timeChange + 1;
      // this.loading1 = true;
      this.loading2 = true;
      this.loading3 = true;
      this.loading4 = true;
      this.loading5 = true;
      this.loading6 = true;
      this.loading7 = true;
      this.loading8 = true;
      this.loading9 = true;
      this.loading10 = true;
      this.loading11 = true;
      this.loading12 = true;
      // console.log("月份改变")
    },
    /**
     * 月份或者年份发生变化
     */
    yearMonthChange() {
      if (this.$refs.yearMonthSelect.getYear() && this.$refs.yearMonthSelect.getMonth()) {
        this.year = this.$refs.yearMonthSelect.getYear();
        this.month = this.$refs.yearMonthSelect.getMonth();
        this.timeChange = this.timeChange + 1;
        // console.log("年月改变")
      }
    }
  }
};
</script>

<style lang="scss" scoped>
// * {
//   font-family: "微软雅黑";
// }
.container {
  // margin-top: 18px;
  height: 100%;
  width: 100%;


  .bigRow {
    height: 100%;
    // background: #3F9EFF;

    .bigCol {
      // background: #ff8c3f;
      height: 100%;
    }
  }
}

.dateSpan {
  font-family: "SimHei", Arial, sans-serif;
  font-weight: bold;
  /* 可选 */
  color: #235284;
}


.chartTitleDiv {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 7px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 5px;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.2);
  margin-bottom: 10px;
  height: 40px;

  .pageTitle {
    font-family: "SimHei", Arial, sans-serif;
    font-size: 20px;
    font-weight: bold;
    /* 可选 */
    color: #235284;
  }

}

.chartTitleDiv1 {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 7px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 5px;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.2);
  margin-bottom: 10px;
  height: 40px;
  position: relative;
  /* 添加 position 属性 */

  .pageTitle {
    margin-right: auto;
    margin-left: auto;
    font-family: "SimHei", Arial, sans-serif;
    font-size: 20px;
    font-weight: bold;
    /* 可选 */
    color: #235284;
    position: absolute;
    /* 添加 position 属性 */
    left: 50%;
    /* 使标题居中 */
    transform: translateX(-50%);
    /* 通过 translateX 调整标题位置 */
  }

  .yearMonthSelect {
    margin-left: auto;
    /* 将年月选择器放置在标题的右侧 */
  }
}

.use-title {
  font-size: 15px;
  font-style: normal;
  color: #333;
  padding: 10px;
}

.card-data {
  box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.192);
  border-radius: 5px;
}

.card-text {
  line-height: 18px;
  // color: rgba(0, 0, 0, 0.45);
  font-size: 16px;
  margin-top: 10px;
}

.chart-card {
  height: calc((100vh - 194px - 80px) / 3);
  // height: 10px;
  background: rgba(255, 255, 255, 0.6);
  // box-shadow: 0px 0px 10px 0px rgba(20, 20, 20, 0.116);
  border-radius: 10px;
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);
}

.yearDataSpan {
  color: #ffffff;
  font-weight: bold;
}

.monthDataSpan {
  color: #ffffff;
  font-weight: bold;
}

.el-row {
  margin-bottom: 10px;

  &:last-child {
    margin-bottom: 0;
  }
}

.el-col {
  border-radius: 4px;
}

.bg-purple {
  background: #d3dce6;
}

.bg-white {
  background: #ffffff;
}

.statisticsItem {
  border-radius: 6px;
  height: 80px;
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);
}

.grid-content {
  border-radius: 6px;
  min-height: 36px;
}

.block {
  margin-left: 0%;
  height: 100%;
  margin-bottom: 20px;
}

.inform-header {
  text-align: center;
  padding-top: 10px;
  height: 30px;
}

.messageDiv {
  height: 100%;
  /* 设置阴影 */
  border-radius: 15px;
  // padding: 10px;
  padding-top: 10px;
  padding-left: 10px;
  padding-right: 10px;
  border-radius: 10px;
  background: rgba(245, 245, 245, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2),
    inset 2px 2px 2px rgba(0, 0, 0, 0.1);

  //消息框
  .messageInsideDiv {
    //添加滚动条
    overflow: auto;
  }

  /* 隐藏滚动条 */
  .messageInsideDiv::-webkit-scrollbar {
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
  .messageInsideDiv:hover::-webkit-scrollbar {
    // background-color: #f1f1f1;
    width: 10px;
    height: 5px;
  }

  /* 定义滚动条的轨道和滑块样式 */
  .messageInsideDiv:hover::-webkit-scrollbar-track {
    background-color: #f1f1f1;
  }

  .messageInsideDiv:hover::-webkit-scrollbar-thumb {
    background-color: #D2D2D2;
    border-radius: 5px;
  }

  .messageInsideDiv:hover::-webkit-scrollbar-button {
    background-color: #D2D2D2;
    height: 5px;
  }

}
</style>
