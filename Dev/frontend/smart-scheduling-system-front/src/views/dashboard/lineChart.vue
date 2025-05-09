<template>
  <div ref="linechart" style="width:100%;height:185px"></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
    props: {
      
    },
    data() {
      return {
        chart: null
      }
    },
    mounted() {
      this.$nextTick(() => {
        this.initChart()
      })
    },
    methods: {
        initChart(){
          this.chart = echarts.init(this.$refs.linechart)
          this.setOptions()
        },
        setOptions(){
          this.chart.setOption({
            grid: {
              left: '3%',
              right: '4%',
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
              data: ['任务数']
            },
            xAxis: [
              {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Sun', 'Sun', 'Sun', 'Sun'],
                axisPointer: {
                  type: 'shadow'
                }
              }
            ],
            yAxis: [
              {
                type: 'value',
                name: '月度任务数变化',
                // min: 0,
                // max: 250,
                // interval: 50,
                axisLabel: {
                  formatter: '{value}'
                }
              },
              // {
              //   type: 'value',
              //   name: 'Temperature',
              //   min: 0,
              //   max: 25,
              //   interval: 5,
              //   axisLabel: {
              //     formatter: '{value} °C'
              //   }
              // }
            ],
            series: [
              {
                name: '任务数',
                type: 'bar',
                tooltip: {
                  valueFormatter: function (value) {
                    return value + ' ml';
                  }
                },
                data: [
                  2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
                ]
              },
              // {
              //   name: 'Precipitation',
              //   type: 'bar',
              //   tooltip: {
              //     valueFormatter: function (value) {
              //       return value + ' ml';
              //     }
              //   },
              //   data: [
              //     2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
              //   ]
              // },
              // {
              //   name: 'Temperature',
              //   type: 'line',
              //   yAxisIndex: 1,
              //   tooltip: {
              //     valueFormatter: function (value) {
              //       return value + ' °C';
              //     }
              //   },
              //   data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
              // }
            ],
            dataZoom: {
              show: true, // 为true 滚动条出现
              // realtime: true,  
              type:'slider', // 有type这个属性，滚动条在最下面，也可以不行，写y：36，这表示距离顶端36px，一般就是在图上面。
              height: 3, // 表示滚动条的高度，也就是粗细
              start: 20, // 表示默认展示20%～100%这一段。
              end: 100,
              bottom: 15
            }
          })
        }
    }
}
</script>

<style>

</style>