<template>
  <div ref="linechart" style="width:100%;height:265px"></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
    props: {
      data: {
        type: Array,
      },
    },
    data() {
      return {
        chart: null,
        // dayData:[["2023-03-21", 116], ["2023-03-22", 123], ["2023-03-23", 111]]
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

          // 图表页面自适应
          // window.addEventListener("resize",()=>{
          //   // console.log('浏览器大小改变')
          //   linechart.resize()
          // })
        },
        setOptions(){
          // const dateList = this.data.map(function (item) {
          //   return item[0];
          // });
          // const valueList = data.map(function (item) {
          //   return item[1];
          // });
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
              data: ['客流量']
            },
            xAxis: [
              {
                type: 'category',
                data: [
                    '2023/3/22 8:00', '2023/3/22 8:30', '2023/3/22 9:00', '2023/3/22 9:30', '2023/3/22 10:00', '2023/3/22 10:30', 
                    '2023/3/22 11:00', '2023/3/22 11:30', '2023/3/22 12:00', '2023/3/22 12:30', '2023/3/22 13:00', '2023/3/22 13:30',
                    '2023/3/22 14:00', '2023/3/22 14:30', '2023/3/22 15:00', '2023/3/22 15:30', '2023/3/22 16:00', '2023/3/22 16:30',
                    '2023/3/22 17:00', '2023/3/22 17:30',
                ].map(function (str) {
                    return str.replace(' ', '\n');
                }),
                // data: this.data[0],
                axisPointer: {
                  type: 'shadow'
                }
              }
            ],
            yAxis: [
              {
                type: 'value',
                name: '实时客流量',
                // min: 0,
                // max: 250,
                // interval: 50,
                axisLabel: {
                  formatter: '{value}'
                },
                axisPointer: {
                  snap: true
                }
              },
              {
                type: 'value',
                name: 'line',
                // min: 0,
                // max: 25,
                // interval: 5,
                axisLabel: {
                  formatter: '{value}'
                }
              }
            ],
            series: [
              {
                name: '客流量',
                type: 'bar',
                tooltip: {
                  valueFormatter: function (value) {
                    return value;
                  }
                },
                data: [300, 280, 250, 260, 270, 300, 550, 500, 400, 390, 380, 390, 400, 500, 600, 750, 800, 700, 600, 400],
                // data: this.data[1]
              },
              // {
              //   name: 'Hour-line',
              //   type: 'line',
              //   tooltip: {
              //     valueFormatter: function (value) {
              //       return value;
              //     }
              //   },
              //   data: [300, 280, 250, 260, 270, 300, 550, 500, 400, 390, 380, 390, 400, 500, 600, 750, 800, 700, 600, 400]
              // },
              // {
              //   name: 'Hour',
              //   type: 'line',
              //   // yAxisIndex: 1,
              //   tooltip: {
              //     valueFormatter: function (value) {
              //       return value;
              //     }
              //   },
              //   data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
              // }
            ],
            dataZoom: {
              show: true, // 为true 滚动条出现
              // realtime: true,  
              type:'slider', // 有type这个属性，滚动条在最下面，也可以不行，写y：36，这表示距离顶端36px，一般就是在图上面。
              height: 5, // 表示滚动条的高度，也就是粗细
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