<template>
    <div>
        <div id="container" style="height: 100%"></div>
    </div>
</template>

<script>
import * as echarts from 'echarts'
var app = {};

export default {
    name: 'SmartSchedulingSystemDynamicTitle',

    data() {
        return {

        };
    },

    props: {
        title: {
            type: String
        },
        fontSize: {
            type: Number
        }
    },

    mounted() {
        var option = {
            graphic: {
                elements: [
                    {
                        type: 'text',
                        left: 'center',
                        top: 'center',
                        style: {
                            text: this.title,
                            fontSize: this.fontSize,
                            // fontWeight: 'bold',
                            lineDash: [0, 200],
                            lineDashOffset: 0,
                            fill: 'transparent',
                            stroke: '#235284',
                            lineWidth: 1
                        },
                        keyframeAnimation: {
                            duration: 3000,
                            loop: true,
                            keyframes: [
                                {
                                    percent: 0.7,
                                    style: {
                                        fill: 'transparent',
                                        lineDashOffset: 200,
                                        lineDash: [200, 0]
                                    }
                                },
                                {
                                    // Stop for a while.
                                    percent: 0.8,
                                    style: {
                                        fill: 'transparent'
                                    }
                                },
                                {
                                    percent: 1,
                                    style: {
                                        fill: '#235284'
                                    }
                                }
                            ]
                        }
                    }
                ]
            }
        };

        var dom = document.getElementById('container');
        var myChart = echarts.init(dom, null, {
            renderer: 'canvas',
            useDirtyRect: false
        });
        if (option && typeof option === 'object') {
            myChart.setOption(option);
        }

        window.addEventListener('resize', myChart.resize);
    },

    methods: {

    },
};
</script>

<style lang="scss" scoped></style>