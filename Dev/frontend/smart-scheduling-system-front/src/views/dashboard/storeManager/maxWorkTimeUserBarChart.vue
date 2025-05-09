<!-- 查询企业中每个门店的用户数量 -->
<template>
  <div ref="barChart" class="chartStyle"></div>
</template>

<script>
import * as echarts from 'echarts'
import systemStatisticsApi from '@/api/statistics/systemStatistics'
import shiftSchedulingStatisticsApi from '@/api/statistics/shiftSchedulingStatistics'

export default {
  props: {
    year: {
      type: Number
    },
    month: {
      type: Number
    },
    type: {
      type: Number
    },
    size: {
      type: Number
    },
    timeChange: {
      type: Number
    },
    num: {
      type: String
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
    num: {
      handler(newValue, oldValue) {
        if (newValue != "") {
          this.getUserWorkTime().then(
            () => {
              this.setOptions();
            }
          );
        }

      },
    },
  },
  data() {
    return {
      chart: null,

      //门店名称集合
      staffNameList: [],
      //门店对应数量集合
      averageWorkTimeList: [],

      barEchartsColorList: [
        // '#DAA520',
        // '#DAA520',
        // '#DBAC1F',
        // '#DFBA18',
        // '#E3C711',
        // '#DACD1F',
        // '#C3C844',
        // '#BEC74C',
        // '#AFC364',
        // '#A1C07B',
        // '#8FBC99',
        // '#a0c9ae',
        '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#b8e2f4',
        // '#8dcfec',
        // '#6abce2',
        // '#58afdd',
        // '#46a2da',
        // '#4194cb',
        // '#3982b8',
        // '#3271a5',
        // '#235284',
      ],
    }
  },
  created() {

  },
  mounted() {
    // console.log("前十员工")
    this.initChart();
    // this.$nextTick(() => {
    //   this.getUserWorkTime().then(
    //     () => {
    //       this.initChart();
    //       this.setOptions();
    //     }
    //   );
    // })
  },
  methods: {

    ////条形图配置
    initChart() {
      this.chart = echarts.init(this.$refs.barChart)
    },
    setOptions() {
      const that = this
      this.chart.setOption({

        tooltip: {
          trigger: 'axis',
          //鼠标放到条形的时候，提示信息
          axisPointer: {
            type: 'shadow'
          },
          //出现一条竖直线来显示当前的x轴
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
        legend: {},
        //设置离div四边的间隔
        grid: {
          left: '3%',
          right: '10%',
          top: '15%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          //坐标轴名称
          name: '数量',
          type: 'value',
          // boundaryGap: [0, 0.01],
          axisLabel: {
            //指定单位
            formatter: '{value} h',
            align: 'center'
          },

        },
        yAxis: {
          //坐标轴名称
          name: '门店名称',
          type: 'category',
          data: this.staffNameList,

          axisPointer: {
            type: 'shadow'
          }
        },
        series: [
          {
            name: '门店用户数',
            type: 'bar',
            showBackground: true,
            data: this.averageWorkTimeList,
            itemStyle: {
              // color: function (params) {
              //   return that.barEchartsColorList[params.dataIndex]
              // }
              color: "#4194cb"
            },
          }

        ],
        dataZoom: [
          {
            type: 'inside', // 类型, inside表示鼠标滚动进行缩放
            id: '',
            disabled: false, // 是否停止组件的功能
            // xAxisIndex: '', // 组件控制的 x轴, 默认全部
            // yAxisIndex: '', // 组件控制的 y轴
            // radiusAxisIndex: '', // 组件控制的 radius轴
            // angleAxisIndex: '', // 组件控制的 angle轴
            // filterMode: '', // 可选值filter/weakFilter/empty/none，详解如下
            start: 0, // 数据窗口范围的起始百分比。范围是：0~100, 表示0%~100%同下
            end: 100, // 数据窗口范围的结束百分比。范围是：0~100
            // startValue: '', // 范围的起始数值，若设置了inside.start则失效
            // endValue: '', // 范围的结束数值，若设置了inside.end则失效
            // minSpan: 100, // 限制窗口大小的最小值（百分比值），取值范围是0~100，设置inside.minValueSpan则失效
            // maxSpan: 100, // 限制窗口大小的最小值（百分比值），取值范围是0~100，设置inside.maxValueSpan则失效
            // minValueSpan: '', // 用于限制窗口大小的最小值, 若时间轴上可设置为：3600*24*1000*5[时间轴接收的都是时间戳]表示5天,  在类目轴上可以设置为5表示5个类目
            // maxValueSpan: '', // 用于限制窗口大小的最大值，用法同上
            orient: 'vertical', // 布局方式是横还是竖，可选值horizontal[水平]/vertical[竖直]
            // zoomLock: false, // 是否锁定选择区域的大小，若设置为true则锁定选择区域的大小，也就是说，只能平移，不能缩放
            throttle: 100, // 设置触发视图刷新的频率。单位为毫秒（ms）
            // rangeMode: '', // 详解如下
            // zoomOnMouseWheel: true, // 触发方式, 可选值true[直接鼠标滚轮触发，默认]/false[滚轮不能触发]/shift[按住shift和滚轮触发]/ctrl[参考shift]/alt[参考shift]
            // moveOnMouseMove: true, // 触发数据窗口平移方式，可选值参考zoomOnMouseWheel
            // moveOnMouseWheel: true, // 触发数据窗口平移方式，可选值参考zoomOnMouseWheel
            // preventDefaultMouseMove: true // 是否阻止mousemove事件的默认行为。
            // width: 5, // 表示滚动条的高度，也就是粗细
          }]
      })
    },

    ////获取数据
    getUserWorkTime() {
      return new Promise((resolve, reject) => {
        let param = { year: this.year, month: this.month, type: this.type, num: this.num }
        // console.log("前十员工："+JSON.stringify(param))
        shiftSchedulingStatisticsApi.getUserWorkTime(param).then(res => {
          this.staffNameList = res.staffNameList
          this.averageWorkTimeList = res.averageWorkTimeList
          this.$emit('receiveData');
          resolve();
        })
      })
    },

    /**
    * 月份发生改变
    */
    monthChange() {
      // console.log("重新统计数据")
      this.getUserWorkTime().then(
        () => {
          this.setOptions();
        }
      );
    }
  }
}
</script>

<style ></style>