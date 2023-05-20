
<!-- 查询当前月每个门店的员工的平均工作时长 -->
<template>
  <div>
  <!-- <div class="chartTitleDiv">
      <monthSelect ref="monthSelect" @monthChange="monthChange()" style="display: inline-block;"></monthSelect>
          <span>{{ year }}年{{ month }}月</span>
        </div> -->
    <div ref="linechart" class="chartStyle"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import monthSelect from '@/components/MonthSelect/monthSelect.vue'
import shiftSchedulingStatisticsApi from '@/api/statistics/shiftSchedulingStatistics'

export default {
  components: {
    monthSelect
  },
  props: {
    year: {
      type: Number
    },
    month: {
      type: Number
    },
    size: {
      type: Number
    },
    timeChange: {
      type: Number
    }
  },
  watch: {
    timeChange: {
      handler(newValue, oldValue) {
        this.monthChange()
      },
      // immediate: true,
    },
    size: {
      handler(newValue, oldValue) {
        if (this.chart != null) {
          this.chart.resize();
        }
      },
    },
  },
  data() {
    return {
      chart: null,
      monthNameList: undefined,
      averageShiftNumList: undefined,
      averageShiftAllocationRateList: undefined,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      // this.getMonthShiftNumAndAllocationRate().then(
      //   () => {
      //     this.initChart();
      //     this.setOptions();
      //   }
      // );
    })
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.linechart)
      // this.setOptions()
    },
    setOptions() {
      this.chart.setOption({
        grid: {
          left: '3%',
          right: '10%',
          bottom: '10%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        toolbox: {
          feature: {
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar'] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        legend: {
          // data: ['日均工作时长']
        },
        xAxis: [
          {
            type: 'category',
            name: '门店',
            data: this.monthNameList,
            axisPointer: {
              type: 'shadow'
            },
            dataZoomIndex: 0  // x 轴对应的 dataZoom 组件的索引为 0
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '日均班次数量',
            // min: 0,
            // max: 250,
            // interval: 50,
            axisLabel: {
              formatter: '{value}个'
            }
          },
        ],
        series: [
          {
            name: '门店日均班次数量',
            type: 'bar',
            tooltip: {
              valueFormatter: function (value) {
                return value;
              }
            },
            data: this.averageShiftNumList,
            itemStyle: {
              color: '#4194cb'
            }
          }

        ],
        dataZoom: {
          show: true, // 为true 滚动条出现
          // realtime: true,  
          type: 'inside', // 有type这个属性，滚动条在最下面，也可以不行，写y：36，这表示距离顶端36px，一般就是在图上面。
          height: 5, // 表示滚动条的高度，也就是粗细
          xAxisIndex: 0,
          start: 0, // 表示默认展示20%～100%这一段。
          end: 100,
          //放在图标的下方
          bottom: 25,
          zoomOnMouseWheel: true,
          filterMode: 'filter'  // 缩放模式为数据过滤
        }
      })
    },
    /**
     * 获取指定月份的各门店的员工平均工作时长（月的工作时长/月的工作天数/参与工作的人数/60）
     */
    getMonthShiftNumAndAllocationRate() {
      return new Promise((resolve, reject) => {

        let param = { year: this.year }
        // console.log("param:" + JSON.stringify(param))
        shiftSchedulingStatisticsApi.getMonthShiftNumAndAllocationRate(param).then(
          res => {
            this.monthNameList = res.monthNameList;
            this.averageShiftNumList = res.averageShiftNumList;
            this.$emit('receiveData');
            resolve();
          }
        )
      })
    },
    /**
     * 月份发生改变
     */
    monthChange() {
      // console.log("重新统计数据")
      this.getMonthShiftNumAndAllocationRate().then(
        () => {
          this.setOptions();
        }
      );
    }
  }
}
</script>

<style lang="scss" scoped>
.chartTitleDiv {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px;
}
</style>
<style></style>