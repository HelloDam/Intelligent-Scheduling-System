<template>
    <div class="userContainer">
        <el-row :gutter="10">
            <el-col :span="24" class="elCol">
                <el-card class="cardCss">
                    <div slot="header">
                        <span>系统使用情况</span>
                        <yearMonthSelect ref="yearMonthSelect" style="float: right;" :type="1" @yearMonthChange="yearMonthChange()">
                        </yearMonthSelect>
                    </div>

                    <el-row :gutter="15">
                        <el-col :span="6">
                            <div class="statisticsItem" style="background: #4194cb;">
                                <i class="iconfont icon-qiye"
                                    style="font-size: 50px; font-weight: 60px;color:#FFFFFF ;margin-left: 20px;margin-right: 20px;"></i>
                                <span class="dataSpan">系统企业数量: {{
                                    systemUseStatisticsVo.enterpriseNum }}
                                </span>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="statisticsItem" style="background: #4194cb;">
                                <i class="iconfont icon-81"
                                    style="font-size: 50px; font-weight: 60px;color:#FFFFFF ;margin-left: 20px;margin-right: 20px;"></i>
                                <span class="dataSpan">系统门店数量: {{
                                    systemUseStatisticsVo.storeNum }}
                                </span>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="statisticsItem" style="background: #4194cb;">
                                <i class="iconfont icon-yuangong"
                                    style="font-size: 60px; font-weight: 60px;color:#FFFFFF ;margin-left: 20px;margin-right: 20px;"></i>
                                <span class="dataSpan">系统用户数量: {{
                                    systemUseStatisticsVo.userNum }}
                                </span>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="statisticsItem" style="background: #4194cb;">
                                <i class="iconfont icon-renwu1"
                                    style="font-size: 50px; font-weight: 60px;color:#FFFFFF ;margin-left: 20px;margin-right: 20px;"></i>
                                <span class="dataSpan">系统任务数量: {{
                                    systemUseStatisticsVo.calculatedTaskNum }}
                                </span>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row :gutter="15" style="margin-top: 15px;">
                        <el-col :span="12">
                            <div class="chart-card">
                                <div class="use-title">
                                    指定年份每月的企业注册数量
                                    <el-tooltip content="当月日均工作时间最长的前十员工" placement="top">
                                        <i class="el-icon-question"></i>
                                    </el-tooltip>
                                </div>
                                <yearEnterpriseRegistNumLineChart :timeChange="timeChange" :year="year" style="height: 320px;">
                                </yearEnterpriseRegistNumLineChart>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <div class="chart-card">
                                <div class="use-title">
                                    指定年份每月的用户注册数量
                                    <el-tooltip content="当月日均工作时间最长的前十员工" placement="top">
                                        <i class="el-icon-question"></i>
                                    </el-tooltip>
                                </div>
                                <yearUserRegistNumLineChart :timeChange="timeChange" :year="year" style="height: 320px;">
                                </yearUserRegistNumLineChart>
                            </div>

                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import systemStatisticsApi from '@/api/statistics/systemStatistics';
import yearMonthSelect from '@/components/YearMonthSelect/yearMonthSelect.vue';
import yearEnterpriseRegistNumLineChart from './yearEnterpriseRegistNumLineChart.vue';
import yearUserRegistNumLineChart from './yearUserRegistNumLineChart.vue';

export default {
    name: 'SmartSchedulingSystemUserStatistic',

    components: {
        yearMonthSelect,
        yearEnterpriseRegistNumLineChart,
        yearUserRegistNumLineChart
    },

    data() {
        return {
            //系统使用信息
            systemUseStatisticsVo: {

            },
            year: undefined,
            timeChange: 0,
        };
    },

    mounted() {
        this.year = this.$refs.yearMonthSelect.getYear();
        this.timeChange = this.timeChange + 1;
    },

    created() {
        this.getSystemUseStatisticsVo();
    },

    methods: {
        /**
         * 查询企业数量、门店数量等统计信息
         */
        getSystemUseStatisticsVo() {
            systemStatisticsApi.getSystemUseStatisticsVo().then(
                res => {
                    this.systemUseStatisticsVo = res.systemUseStatisticsVo;
                }
            )
        },
        /**
         * 时间发生改变，刷新echart表格
         */
        yearMonthChange() {
            this.year = this.$refs.yearMonthSelect.getYear();
            this.timeChange = this.timeChange + 1;
        }
    },
};
</script>

<style lang="scss" scoped>
.userContainer {
    .elCol {
        .cardCss {
            background: rgba(255, 255, 255, 0.6);
            border-radius: 10px;
            box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);

            .statisticsItem {
                border-radius: 10px;
                height: 80px;
                box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);
                display: flex;
                align-items: center;

                .dataSpan {
                    color: #ffffff;
                    font-weight: bold;
                    font-size: 24px;
                }
            }

            .transparent {
                background: rgba(255, 255, 255, 0);
            }

            .chart-card {
                height: 350px;
                background: rgba(255, 255, 255, 0.6);
                // box-shadow: 0px 0px 10px 0px rgba(20, 20, 20, 0.116);
                border-radius: 10px;
                box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);

                .use-title {
                    font-size: 15px;
                    font-style: normal;
                    color: #333;
                    padding: 10px;
                }
            }


        }

    }


}

.transparent {
    background: rgba(255, 255, 255, 0);
}
</style>