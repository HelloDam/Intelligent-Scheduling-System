<template>
    <!-- <div class="titleDiv">占用率</div> -->
    <div id="customGaugeContainer" ref="panel" style="height: 100%;"></div>
</template>

<script>
import * as echarts from 'echarts'

var app = {};
var ROOT_PATH = 'https://echarts.apache.org/examples';
// var _panelImageURL = ROOT_PATH + '/data/asset/img/custom-gauge-panel.png';
var _panelImageURL = require("@/assets/panel/panelBackground_green.png");
var _animationDuration = 1000;
var _animationDurationUpdate = 1000;
var _animationEasingUpdate = 'quarticInOut';
//这个不改
var _valOnRadianMax = 100;
//这个不改
var _pointerInnerRadius = 60;
//外轮廓半径
var _outerRadius = 110;
//内轮廓半径
var _innerRadius = 100;
//里面白色远的半径
var _insidePanelRadius = 100;
var _fontSize = 50;
var _currentDataIndex = 0;
var _shadowColor = '#20A53A';
var _textColor = '#20A53A';
var _shadowBlur = 20;

export default {
    name: 'SmartSchedulingSystemCustomGauge',
    props: {
        rate: {
            type: Number
        },
        timeChange: {
            type: Number
        }
    },

    watch: {
        rate: {
            handler(newValue, oldValue) {
                // console.log("刷新表盘数据")
                // console.log("oldValue:" + oldValue)
                // console.log("newValue:" + newValue)
                // console.log("rate:" + this.rate)
                //刷新表盘数据
                this.setOptions();
            },
            deep: true
        },
    },

    data() {
        return {
            myChart: undefined,
            scheduledTask: undefined,
        };
    },

    mounted() {
        this.$nextTick(() => {
            this.initChart();
            window.addEventListener('resize', this.myChart.resize());
            // this.setScheduledTask();
        })
    },

    methods: {
        initChart() {
            let dom = document.getElementById('customGaugeContainer');
            let clientHeight = dom.clientHeight;
            // console.log("clientHeight:" + clientHeight)
            _outerRadius = (clientHeight / 2) * 0.9;
            _innerRadius = (clientHeight / 2) * 0.8;
            _insidePanelRadius = (clientHeight / 2) * 0.8;
            _fontSize = (clientHeight / 2) * 0.3;
            _shadowBlur = (clientHeight / 2) * 0.2;

            // this.myChart = echarts.init(dom, null, {
            //     renderer: 'canvas',
            //     useDirtyRect: false
            // });
            this.myChart = echarts.init(this.$refs.panel);
            this.setOptions();
        },
        setOptions() {
            if (this.rate > 80) {
                _shadowColor = '#F33333';
                _textColor = '#F33333';
                _panelImageURL = require("@/assets/panel/panelBackground_red.png");
            } else {
                _shadowColor = '#20A53A';
                _textColor = '#20A53A';
                _panelImageURL = require("@/assets/panel/panelBackground_green.png");
            }
            let option = {
                animationEasing: _animationEasingUpdate,
                animationDuration: _animationDuration,
                animationDurationUpdate: _animationDurationUpdate,
                animationEasingUpdate: _animationEasingUpdate,
                //数据展示
                dataset: {
                    //100可以看成是百分比
                    source: [[1, this.rate]]
                },
                tooltip: {},
                angleAxis: {
                    type: 'value',
                    startAngle: 0,
                    show: false,
                    min: 0,
                    max: _valOnRadianMax
                },
                radiusAxis: {
                    type: 'value',
                    show: false
                },
                polar: {},
                series: [
                    {
                        type: 'custom',
                        coordinateSystem: 'polar',
                        renderItem: renderItem
                    }
                ]
            };

            if (option && typeof option === 'object') {
                this.myChart.setOption(option);
            }
        },
        /**
              * 设置定时任务
              */
        setScheduledTask() {
            this.scheduledTask = setInterval(function () {
                console.log("rate:" + this.rate)
            }, 2000);
        }
    },

};


function renderItem(params, api) {
    var valOnRadian = api.value(1);
    var coords = api.coord([api.value(0), valOnRadian]);
    var polarEndRadian = coords[3];
    var imageStyle = {
        image: _panelImageURL,
        x: params.coordSys.cx - _outerRadius,
        y: params.coordSys.cy - _outerRadius,
        width: _outerRadius * 2,
        height: _outerRadius * 2
    };
    return {
        type: 'group',
        children: [
            {
                type: 'image',
                style: imageStyle,
                clipPath: {
                    type: 'sector',
                    shape: {
                        cx: params.coordSys.cx,
                        cy: params.coordSys.cy,
                        r: _outerRadius,
                        r0: _innerRadius,
                        startAngle: 0,
                        endAngle: -polarEndRadian,
                        transition: 'endAngle',
                        enterFrom: { endAngle: 0 }
                    }
                }
            },
            // {
            //     type: 'image',
            //     style: imageStyle,
            //     clipPath: {
            //         type: 'polygon',
            //         shape: {
            //             points: makePionterPoints(params, polarEndRadian)
            //         },
            //         extra: {
            //             polarEndRadian: polarEndRadian,
            //             transition: 'polarEndRadian',
            //             enterFrom: { polarEndRadian: 0 }
            //         },
            //         during: function (apiDuring) {
            //             apiDuring.setShape(
            //                 'points',
            //                 makePionterPoints(params, apiDuring.getExtra('polarEndRadian'))
            //             );
            //         }
            //     }
            // },
            //白色中心圆
            {
                type: 'circle',
                shape: {
                    cx: params.coordSys.cx,
                    cy: params.coordSys.cy,
                    r: _insidePanelRadius
                },
                style: {
                    fill: '#fff',
                    shadowBlur: _shadowBlur,
                    shadowOffsetX: 0,
                    shadowOffsetY: 0,
                    //轮廓阴影颜色
                    shadowColor: _shadowColor
                }
            },
            {
                type: 'text',
                extra: {
                    valOnRadian: valOnRadian,
                    transition: 'valOnRadian',
                    enterFrom: { valOnRadian: 0 }
                },
                style: {
                    text: makeText(valOnRadian),
                    fontSize: _fontSize,
                    fontWeight: 700,
                    x: params.coordSys.cx,
                    y: params.coordSys.cy,
                    //字体颜色
                    fill: _textColor,
                    align: 'center',
                    verticalAlign: 'middle',
                    enterFrom: { opacity: 0 }
                },
                during: function (apiDuring) {
                    apiDuring.setStyle(
                        'text',
                        makeText(apiDuring.getExtra('valOnRadian'))
                    );
                }
            }
        ]
    };
}
function convertToPolarPoint(renderItemParams, radius, radian) {
    return [
        Math.cos(radian) * radius + renderItemParams.coordSys.cx,
        -Math.sin(radian) * radius + renderItemParams.coordSys.cy
    ];
}
function makePionterPoints(renderItemParams, polarEndRadian) {
    return [
        convertToPolarPoint(renderItemParams, _outerRadius, polarEndRadian),
        convertToPolarPoint(
            renderItemParams,
            _outerRadius,
            polarEndRadian + Math.PI * 0.03
        ),
        convertToPolarPoint(renderItemParams, _pointerInnerRadius, polarEndRadian)
    ];
}
function makeText(valOnRadian) {
    // Validate additive animation calc.
    if (valOnRadian < -10) {
        alert('illegal during val: ' + valOnRadian);
    }
    return ((valOnRadian / _valOnRadianMax) * 100).toFixed(1) + '%';
}

</script>

<style lang="scss" scoped>
#customGaugeContainer {
    // height: calc(100% - 20px);
    height: 100%;
}
</style>