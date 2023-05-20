<template>
    <div id="panel1Container" style="height: 100%"></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
    name: 'SmartSchedulingSystemInstrumentPanel1',

    data() {
        return {
            myChart: undefined,
        };
    },

    mounted() {
        this.initChart();
        window.addEventListener('resize', this.myChart.resize());
    },

    methods: {
        initChart() {
            let dom = document.getElementById('panel1Container');
            this.myChart = echarts.init(dom, null, {
                renderer: 'canvas',
                useDirtyRect: false
            });
            this.setOptions();
        },
        setOptions() {
            let option = {
                series: [
                    {
                        type: 'gauge',
                        progress: {
                            show: true,
                            width: 10
                        },
                        axisLine: {
                            lineStyle: {
                                width: 10
                            }
                        },
                        axisTick: {
                            show: false
                        },
                        splitLine: {
                            length: 10,
                            lineStyle: {
                                width: 2,
                                color: '#999'
                            }
                        },
                        axisLabel: {
                            distance: 25,
                            color: '#999',
                            fontSize: 10
                        },
                        anchor: {
                            show: true,
                            showAbove: true,
                            size: 25,
                            itemStyle: {
                                borderWidth: 10
                            }
                        },
                        title: {
                            show: false
                        },
                        detail: {
                            valueAnimation: true,
                            fontSize: 14,
                            offsetCenter: [0, '70%']
                        },
                        data: [
                            {
                                value: 80
                            }
                        ]
                    }
                ]
            };

            if (option && typeof option === 'object') {
                this.myChart.setOption(option);
            }
        }
    },
};
</script>

<style lang="scss" scoped></style>