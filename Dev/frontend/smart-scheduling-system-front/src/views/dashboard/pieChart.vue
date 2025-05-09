<template>
  <div ref="pieChart" style="width:100%;height:295px"></div>
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
          this.chart = echarts.init(this.$refs.pieChart)
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
              title: {
                text: '满足员工需求比例',
                // subtext: 'Fake Data',
                left: 'center'
              },
              // legend: {
              //   top: 'bottom'
              // },
              toolbox: {
                show: true,
                feature: {
                  mark: { show: true },
                  dataView: { show: true, readOnly: false },
                  restore: { show: true },
                  saveAsImage: { show: true }
                }
              },
              tooltip: {
                trigger: 'item',
                // axisPointer: {
                //   type: 'cross',
                //   crossStyle: {
                //     color: '#999'
                //   }
                // }
              },
              series: [
                {
                  name: '满足员工需求比例',
                  type: 'pie',
                  radius: [10, 70],
                  center: ['50%', '50%'],
                  // roseType: 'radius',
                  itemStyle: {
                    borderRadius: 4,
                    
                  },
                  data: [
                    { value: 98, name: '已满足(%)' },
                    { value: 2, name: '不满足(%)' },
                    // { value: 32, name: 'rose 3' },
                    // { value: 30, name: 'rose 4' },
                    // { value: 28, name: 'rose 5' },
                    // { value: 26, name: 'rose 6' },
                    // { value: 22, name: 'rose 7' },
                    // { value: 18, name: 'rose 8' }
                  ]
                }
              ]
            })
        }
    }
}
</script>

<style>

</style>