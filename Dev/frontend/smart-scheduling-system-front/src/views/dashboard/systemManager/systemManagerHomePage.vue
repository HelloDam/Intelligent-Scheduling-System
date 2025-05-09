<template>
    <div class="container">
        <div class="backgroundDiv">
            <div class="chartTitleDiv">
                <span class="jump">
                    <a href="#user" style="margin-right: 10px;" class="aLabel">使用情况</a>
                    <a href="#redis" style="margin-right: 10px;" class="aLabel">缓存监控</a>
                    <a href="#system" class="aLabel">系统信息</a>
                </span>
                <!-- <span class="pageTitle">系 统 信 息 监 控</span> -->
                <dynamicTitle class="pageTitle" style="height: 40px;width: 200px;" title="系 统 信 息 监 控"
                    :fontSize=titleFontSize></dynamicTitle>
                <!-- <yearMonthSelect ref="yearMonthSelect" class="yearMonthSelect" @yearMonthChange="yearMonthChange()">
                </yearMonthSelect> -->
            </div>
            <div class="statisticDiv">
                <a name="user"></a>
                <userStatistic></userStatistic>
                <a name="redis"></a>
                <redisStatistic></redisStatistic>
                <a name="system"></a>
                <el-row :gutter="10" class="elRow" style="margin-top: 10px;">

                    <el-col :span="18" class="elCol">
                        <el-card class="cardCss">
                            <div slot="header">
                                <span>服务器信息</span>
                            </div>
                            <div class="el-table el-table--enable-row-hover el-table--medium transparent">
                                <table cellspacing="0" style="width: 100%;" class="transparent">
                                    <tbody class="transparent">
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">服务器名称</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.sys">{{ server.sys.computerName }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">操作系统</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.sys">{{ server.sys.osName }}</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">服务器IP</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.sys">{{ server.sys.computerIp }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">系统架构</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.sys">{{ server.sys.osArch }}</div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </el-card>
                        <el-card class="cardCss" style="margin-top: 10px;">
                            <div slot="header">
                                <span>磁盘状态</span>
                            </div>
                            <div class="el-table el-table--enable-row-hover el-table--medium transparent">
                                <table cellspacing="0" style="width: 100%;" class="transparent">
                                    <thead class="transparent">
                                        <tr class="transparent">
                                            <th class="el-table__cell el-table__cell is-leaf transparent">
                                                <div class="cell">盘符路径</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">文件系统</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">盘符类型</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">总大小</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">可用大小</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">已用大小</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">已用百分比</div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody v-if="server.sysFiles" class="transparent">
                                        <tr v-for="(sysFile, index) in server.sysFiles" :key="index" class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.dirName }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.sysTypeName }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.typeName }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.total }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.free }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">{{ sysFile.used }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" :class="{ 'text-danger': sysFile.usage > 80 }">{{
                                                    sysFile.usage }}%</div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </el-card>
                        <el-card class="cardCss" style="margin-top: 10px;">
                            <div slot="header">
                                <span>Java虚拟机信息</span>
                            </div>
                            <div class="el-table el-table--enable-row-hover el-table--medium transparent">
                                <table cellspacing="0" style="width: 100%;" class="transparent">
                                    <tbody class="transparent">
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">Java名称</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.name }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">Java版本</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.version }}</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">启动时间</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.startTime }}</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">运行时长</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.runTime }}</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td colspan="1" class="el-table__cell is-leaf">
                                                <div class="cell">安装路径</div>
                                            </td>
                                            <td colspan="3" class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.home }}</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td colspan="1" class="el-table__cell is-leaf">
                                                <div class="cell">项目路径</div>
                                            </td>
                                            <td colspan="3" class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.sys">{{ server.sys.userDir }}</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td colspan="1" class="el-table__cell is-leaf">
                                                <div class="cell">运行参数</div>
                                            </td>
                                            <td colspan="3" class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.inputArgs }}</div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="6" class="elCol">
                        <el-card class="cardCss">
                            <div slot="header"><span>CPU</span></div>
                            <div class="el-table el-table--enable-row-hover el-table--medium transparent">
                                <table cellspacing="0" style="width: 100%; " class="transparent">
                                    <thead class="transparent">
                                        <tr class="transparent">
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">属性</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">值</div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody class="transparent">
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">核心数</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.cpu">{{ server.cpu.cpuNum }}</div>
                                            </td>
                                        </tr>
                                        <!-- <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">用户使用率</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.cpu">{{ server.cpu.used }}%</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">系统使用率</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.cpu">{{ server.cpu.sys }}%</div>
                                            </td>
                                        </tr> -->
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">当前空闲率</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.cpu">{{ server.cpu.free }}%</div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <!-- <instrumentPanel1 style="height: 200px;"></instrumentPanel1> -->

                            <div style="display: flex;">
                                <div class="panelDiv">
                                    <div class="titleDiv">
                                        <span>用户使用率</span>
                                    </div>
                                    <customGauge ref="customGauge1" style="height: 150px;" :rate="server.cpu.used"
                                        >
                                    </customGauge>
                                </div>
                                <div class="panelDiv">
                                    <div class="titleDiv">
                                        <span>系统使用率</span>
                                    </div>
                                    <customGauge style="height: 150px;" :rate="server.cpu.sys"  ref="customGauge2">
                                    </customGauge>
                                </div>
                            </div>

                        </el-card>
                        <el-card class="cardCss" style="margin-top: 10px;">
                            <div slot="header"><span>内存</span></div>
                            <div class="el-table el-table--enable-row-hover el-table--medium transparent">
                                <table cellspacing="0" style="width: 100%;" class="transparent">
                                    <thead class="transparent">
                                        <tr class="transparent">
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">属性</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">内存</div>
                                            </th>
                                            <th class="el-table__cell is-leaf transparent">
                                                <div class="cell">JVM</div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody class="transparent">
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">总内存</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.mem">{{ server.mem.total }}G</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.total }}M</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">已用内存</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.mem">{{ server.mem.used }}G</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.used }}M</div>
                                            </td>
                                        </tr>
                                        <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">剩余内存</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.mem">{{ server.mem.free }}G</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm">{{ server.jvm.free }}M</div>
                                            </td>
                                        </tr>
                                        <!-- <tr class="transparent">
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell">使用率</div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.mem"
                                                    :class="{ 'text-danger': server.mem.usage > 80 }">{{ server.mem.usage
                                                    }}%
                                                </div>
                                            </td>
                                            <td class="el-table__cell is-leaf">
                                                <div class="cell" v-if="server.jvm"
                                                    :class="{ 'text-danger': server.jvm.usage > 80 }">{{ server.jvm.usage
                                                    }}%
                                                </div>
                                            </td>
                                        </tr> -->
                                    </tbody>
                                </table>
                            </div>
                            <div style="display: flex;">
                                <div class="panelDiv">
                                    <div class="titleDiv">
                                        <span>内存使用率</span>
                                    </div>
                                    <customGauge style="height: 150px;" :rate="server.mem.usage"  ref="customGauge3">
                                    </customGauge>
                                </div>
                                <div class="panelDiv">
                                    <div class="titleDiv">
                                        <span>jvm使用率</span>
                                    </div>
                                    <customGauge style="height: 150px;" :rate="server.jvm.usage"  ref="customGauge4">
                                    </customGauge>
                                </div>
                            </div>
                        </el-card>

                    </el-col>
                </el-row>

            </div>
        </div>
    </div>
</template>

<script>

////组件
import yearMonthSelect from '@/components/YearMonthSelect/yearMonthSelect.vue';
import dynamicTitle from '@/components/DynamicTitle/dynamicTitle.vue';
import instrumentPanel1 from '@/components/InstrumentPanel/instrumentPanel1';
//进度表盘
import customGauge from '@/components/InstrumentPanel/customGauge.vue';
import redisStatistic from '@/views/dashboard/systemManager/redisStatistic';
import userStatistic from './userStatistic.vue';

////js
import systemMonitorApi from '@/api/statistics/systemMonitor'

export default {
    name: 'SmartSchedulingSystemSystemManagerHomePage',
    components: {
        yearMonthSelect,
        dynamicTitle,
        instrumentPanel1,
        customGauge,
        redisStatistic,
        userStatistic
    },

    data() {
        return {
            ////标题
            titleFontSize: 24,

            ////系统信息
            server: {
                //防止一开始数据还没有加载导致前端报错
                cpu: {
                    used: 0,
                },
                mem: {
                    usage: 0,
                },
                jvm: {
                    usage: 0,
                }
            },
            //定时任务
            scheduledTask: undefined,
            timeout: undefined,
        };
    },

    created() {
        this.getServerInfo();
    },

    mounted() {
        // this.setScheduledTask();
    },

    methods: {
        /**
         * 获取服务器数据
         */
        getServerInfo() {
            systemMonitorApi.getServerInfo().then(
                res => {
                    this.server = res.server;
                    this.sleep();
                }
            )
        },
        /**
         * 使用定时任务，子组件获取到的数据是空的，因此改成sleep
         */
        sleep() {
            if (this.timeout) {
                window.clearTimeout(this.timeout);
            }
            this.timeout = setTimeout(() => {
                this.getServerInfo();
            }, 500);
        },
        /**
         * 设置定时任务
         */
        // setScheduledTask() {
        //     this.scheduledTask = setInterval(function () {
        //         systemMonitorApi.getServerInfo().then(
        //             res => {
        //                 this.server = res.server;
        //                 console.log("server.cpu.used:" + this.server.cpu.used)
        //                 //通知表盘刷新数据
        //                 // this.curTime = new Date().getTime();
        //                 // this.$refs.customGauge1.setOptions();
        //                 // console.log("this.curTime:" + this.curTime)
        //             }
        //         )
        //     }, 2000);
        // }
    },
};
</script>

<style lang="scss" scoped>
.container {
    height: 100%;
    width: 100%;

    .backgroundDiv {
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

        .chartTitleDiv {
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

            .jump {
                margin-left: auto;

                .aLabel {
                    /*设置链接文字颜色*/
                    color: #3982b8;
                    /*去掉下划线*/
                    text-decoration: none;
                }

                .aLabel:hover {
                    /*设置鼠标悬停链接文字颜色*/
                    color: #ff0000;
                    /*添加下划线*/
                    text-decoration: underline;
                }

            }

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

        .statisticDiv {
            height: calc((100% - 60px));
            // background: #235284;
            //添加垂直滚动条
            overflow-y: auto;
            overflow-x: hidden;

            .elRow {
                // padding: 5px;

                .elCol {
                    // padding: 10px;
                    // height: 100px;
                    // background: rgba(255, 255, 255, 0.6);
                    // border-radius: 10px;
                    // box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);

                    .cardCss {
                        background: rgba(255, 255, 255, 0.6);
                        border-radius: 10px;
                        box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);


                        .transparent {
                            background: rgba(255, 255, 255, 0);
                        }

                        .panelDiv {
                            flex: 1;

                            .titleDiv {
                                margin-top: 10px;
                                display: flex;
                                justify-content: center;
                            }

                        }
                    }
                }
            }

            .el-row {
                margin-bottom: 20px;

                &:last-child {
                    margin-bottom: 0;
                }
            }



        }

        /* 定义滚动条的轨道和滑块样式 */
        .statisticDiv:hover::-webkit-scrollbar-track {
            background-color: #f1f1f1;
        }

        .statisticDiv:hover::-webkit-scrollbar-thumb {
            background-color: #D2D2D2;
            border-radius: 5px;
        }

        .statisticDiv:hover::-webkit-scrollbar-button {
            background-color: #D2D2D2;
            height: 5px;
        }

        /* 隐藏滚动条 */
        .statisticDiv::-webkit-scrollbar {
            // 隐藏滚动条宽度
            // width: 0 !important;
            // 隐藏滚动条高度
            // height: 0 !important;
            // 隐藏滚动条背景
            background-color: transparent;
            width: 10px;
            // width: 0px;
            height: 5px;
        }

        /* 鼠标进来的时候显示滚动条 */
        .statisticDiv:hover::-webkit-scrollbar {
            // background-color: #f1f1f1;
            width: 10px;
            height: 5px;
        }

    }

}
</style>