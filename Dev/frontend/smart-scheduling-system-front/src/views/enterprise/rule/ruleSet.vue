<template>
    <div class="ruleContainer" id="bodyDiv" v-loading="updateLoading" element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-text="正在修改任务，修改相关信息需要删除任务计算结果和多算法计算结果，请稍等" element-loading-spinner="el-icon-loading">

        <div class="operationButton">
            <span class="pageTitle">规 则 设 置</span>
            <span class="operButton">
                <el-button type="primary" @click="saveRuleData" size="mini">保 存</el-button>
                <el-button type="info" @click="cancel()" size="mini">取 消</el-button>
            </span>
        </div>

        <el-row :gutter="15" class="elRow">
            <el-col :span="12" class="elCol">
                <div class="firstOuterBorder">
                    <div class="firstTitelDiv" style="width: 100px;">固定规则</div>
                    <div class="firstInsideDiv">
                        <div style="padding: 30px 10px 10px 10px">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:130px">门店工作时间段</div>
                                <div style="padding-top: 10px;margin: 20px 20px 0px 20px;">
                                    <el-row>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="60px"
                                                :model="storeWorkTimeFrame">
                                                <el-form-item label="星期一">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Mon"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="星期三">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Wed"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="星期五">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Fri"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="星期天">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Sun"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="60px"
                                                :model="storeWorkTimeFrame">

                                                <el-form-item label="星期二">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Tue"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="星期四">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Thur"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="星期六">
                                                    <el-time-picker is-range v-model="storeWorkTimeFrame.Sat"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                            </el-form>
                                        </el-col>
                                    </el-row>


                                </div>
                            </div>
                        </div>
                        <div style="padding:10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:80px">工时设置</div>
                                <div style="padding-top: 10px;margin:20px 20px 0px 20px;">
                                    <el-row>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="190px"
                                                :model="schedulingRuleVo">

                                                <el-form-item label="员工每周最多工作多长时间">
                                                    <el-input v-model="schedulingRuleVo.mostWorkHourInOneWeek"
                                                        class="minuteInput" size="mini"></el-input>
                                                    <span>&emsp;小时</span>
                                                </el-form-item>
                                                <el-form-item label="一个班次最短工作时间">
                                                    <el-input v-model="schedulingRuleVo.minShiftMinute" class="minuteInput"
                                                        size="mini"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="190px"
                                                :model="schedulingRuleVo">
                                                <el-form-item label="员工每天最多工作多长时间">
                                                    <el-input v-model="schedulingRuleVo.mostWorkHourInOneDay"
                                                        class="minuteInput" size="mini"></el-input>
                                                    <span>&emsp;小时</span>
                                                </el-form-item>
                                                <el-form-item label="一个班次最长工作时间">
                                                    <el-input v-model="schedulingRuleVo.maxShiftMinute" class="minuteInput"
                                                        size="mini"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                    </el-row>
                                </div>
                            </div>
                        </div>
                        <div style="padding: 10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:120px">吃饭时间设置</div>
                                <div style="padding-top: 10px;margin: 20px 20px 0px 20px;">

                                    <el-row>
                                        <el-col :span="12">

                                            <el-form :label-position="labelPosition" label-width="100px">
                                                <el-form-item label="午餐时间范围">
                                                    <el-time-picker is-range v-model="lunchTimeRule.timeFrame"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                                <el-form-item label="晚餐时间范围">
                                                    <el-time-picker is-range v-model="dinnerTimeRule.timeFrame"
                                                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                                        placeholder="选择时间范围" class="timePickerClass" size="mini"
                                                        format="HH:mm">
                                                    </el-time-picker>
                                                </el-form-item>

                                            </el-form>

                                        </el-col>
                                        <el-col :span="12">

                                            <el-form :label-position="labelPosition" label-width="105px">

                                                <el-form-item label="午餐所需时间">
                                                    <el-input v-model="lunchTimeRule.needMinute" class="minuteInput"
                                                        placeholder="午餐所需话费时间（分钟）" size="mini"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>

                                                <el-form-item label="晚餐所需时间">
                                                    <el-input v-model="dinnerTimeRule.needMinute" class="minuteInput"
                                                        placeholder="晚餐所需话费时间（分钟）" size="mini"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>

                                        </el-col>
                                    </el-row>


                                </div>
                            </div>
                        </div>
                        <div style="padding: 10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:80px">其他设置</div>
                                <div style="padding-top: 10px;margin: 20px 20px 0px 20px;">

                                    <el-row>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="100px">
                                                <el-form-item label="休息时间长度">
                                                    <el-input v-model="schedulingRuleVo.restMinute" size="mini"
                                                        placeholder="" class="minuteInput"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="130px">
                                                <el-form-item label="员工最长连续工时">
                                                    <el-input v-model="schedulingRuleVo.maximumContinuousWorkTime"
                                                        size="mini" placeholder="" class="minuteInput"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                    </el-row>


                                </div>
                            </div>
                        </div>

                    </div>

                </div>
            </el-col>
            <el-col :span="12" class="elCol">
                <div class="firstOuterBorder">
                    <div class="firstTitelDiv" style="width: 120px;">自定义规则</div>
                    <div class="firstInsideDiv">
                        <div style="padding: 30px 10px 10px 10px">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:80px">开店规则</div>
                                <div style="padding-top: 10px;margin: 20px;">
                                    <el-row>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="80px">
                                                <el-form-item label="安排人数">
                                                    <span>=&emsp;门店面积({{ schedulingRuleVo.storeSize
                                                    }}m²)&emsp;/&emsp;</span>
                                                    <el-input v-model="openStoreRule.variableParam" size="mini"
                                                        style="width: 50px;"></el-input>
                                                    <span>&emsp;≈&emsp;{{ Math.ceil(schedulingRuleVo.storeSize /
                                                        openStoreRule.variableParam)
                                                    }}</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="80px">
                                                <el-form-item label="提前时间">
                                                    <div style="dispaly:flex">
                                                        <el-input v-model="openStoreRule.prepareMinute" size="mini"
                                                            placeholder="提前多少分钟开店" class="minuteInput"></el-input>
                                                        <span>&emsp;分钟</span>
                                                    </div>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                    </el-row>

                                    <el-form :label-position="labelPosition" label-width="80px">
                                        <el-form-item label="职位选择">

                                            <div style="display: flex;">
                                                <div class="positionCheckStrDiv">
                                                    {{ getCheckPosition(openStoreRule.positionIdArr) }}</div>
                                                <span style="margin-right: 20px;"></span>
                                                <el-button type="text" style="float: right;" icon="el-icon-edit"
                                                    @click="openStorePositionSelect">
                                                    选择职位</el-button>
                                            </div>
                                        </el-form-item>
                                    </el-form>
                                </div>
                            </div>
                        </div>
                        <div style="padding:10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:80px">关店规则</div>
                                <div style="padding-top: 10px;margin: 20px;">
                                    <el-row>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="80px">
                                                <el-form-item label="安排人数">
                                                    <span>=&emsp;门店面积({{ schedulingRuleVo.storeSize
                                                    }}m²)&emsp;/&emsp;</span>
                                                    <el-input v-model="closeStoreRule.variableParam1" size="mini"
                                                        style="width: 50px;"></el-input>
                                                    <span>&emsp;+&emsp;</span>
                                                    <el-input v-model="closeStoreRule.variableParam2" size="mini"
                                                        style="width: 50px;"></el-input>
                                                    <span>&emsp;≈&emsp;{{ Math.ceil(schedulingRuleVo.storeSize /
                                                        closeStoreRule.variableParam1) + closeStoreRule.variableParam2
                                                    }}</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form :label-position="labelPosition" label-width="80px">
                                                <el-form-item label="收尾时间">
                                                    <el-input v-model="closeStoreRule.closeMinute" placeholder="提前多少分钟开店"
                                                        size="mini" class="minuteInput"></el-input>
                                                    <span>&emsp;分钟</span>
                                                </el-form-item>
                                            </el-form>
                                        </el-col>
                                    </el-row>

                                    <el-form :label-position="labelPosition" label-width="80px">
                                        <el-form-item label="职位选择">

                                            <div style="display: flex;">
                                                <div class="positionCheckStrDiv">
                                                    {{ getCheckPosition(closeStoreRule.positionIdArr) }}</div>
                                                <span style="margin-right: 20px;"></span>
                                                <el-button type="text" style="float: right;" icon="el-icon-edit"
                                                    @click="closeStorePositionSelect">
                                                    选择职位</el-button>
                                            </div>
                                        </el-form-item>
                                    </el-form>
                                </div>
                            </div>
                        </div>
                        <div style="padding: 10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:100px">正常班规则</div>
                                <div style="padding-top: 10px;margin: 20px;">
                                    <el-form :label-position="labelPosition" label-width="80px">
                                        <el-form-item label="安排人数">
                                            <span>=&emsp;预约客流量&emsp;/&emsp;</span>
                                            <el-input v-model="normalRule.variableParam" size="mini"
                                                style="width: 50px;"></el-input>
                                        </el-form-item>
                                        <el-form-item label="职位选择">
                                            <div style="display: flex;">
                                                <div class="positionCheckStrDiv">
                                                    {{ getCheckPosition(normalRule.positionIdArr) }}</div>
                                                <span style="margin-right: 20px;"></span>
                                                <el-button type="text" style="float: right;" icon="el-icon-edit"
                                                    @click="normalPositionSelect">
                                                    选择职位</el-button>
                                            </div>

                                        </el-form-item>
                                    </el-form>
                                </div>
                            </div>
                        </div>
                        <div style="padding: 10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:120px">无客流量值班</div>
                                <div style="padding-top: 10px;margin: 20px;">
                                    <el-form :label-position="labelPosition" label-width="110px">
                                        <el-form-item label="安排员工数量">
                                            <el-input v-model="noPassengerRule.staffNum" placeholder=""
                                                size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </div>
                            </div>
                        </div>
                        <div style="padding: 10px;">
                            <div class="secondOuterBorder">
                                <div class="secondTitelDiv" style="width:120px">其他设置</div>
                                <div style="padding-top: 10px;margin: 20px;">
                                    <el-form :label-position="labelPosition" label-width="110px">
                                        <el-form-item label="最少班次数量">
                                            <el-select v-model="schedulingRuleVo.minimumShiftNumInOneDay" placeholder="请选择"
                                                size="mini">
                                                <el-option v-for="item in shiftNumOptions" :key="item.value"
                                                    :label="item.label" :value="item.value" :disabled="item.disabled">
                                                </el-option>
                                            </el-select>
                                        </el-form-item>
                                    </el-form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </el-col>
        </el-row>


        <el-dialog title="指定职位" :visible.sync="openStoreDialogVisible" width="20%">
            <el-tree ref="openStoreTree" :props="positionProps" :data="positionSelectTree" show-checkbox node-key="id"
                :default-checked-keys="openStoreRule.positionIdArr" @check-change="openStoreHandleCheckChange">
            </el-tree>
            <span slot="footer" class="dialog-footer">
                <el-button @click="openStoreDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveOpenStorePosition">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog title="指定职位" :visible.sync="closeStoreDialogVisible" width="20%">
            <el-tree ref="closeStoreTree" :props="positionProps" :data="positionSelectTree" show-checkbox node-key="id"
                :default-checked-keys="closeStoreRule.positionIdArr">
            </el-tree>
            <span slot="footer" class="dialog-footer">
                <el-button @click="closeStoreDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveCloseStorePosition">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog title="指定职位" :visible.sync="normalDialoVisible" width="20%">
            <el-tree ref="normalTree" :props="positionProps" :data="positionSelectTree" show-checkbox node-key="id"
                :default-checked-keys="normalRule.positionIdArr">
            </el-tree>
            <span slot="footer" class="dialog-footer">
                <el-button @click="noPassengerDialoVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveNoPassengerPosition">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>

import schedulingRuleApi from '@/api/enterprise/schedulingRule'
import schedulingTaskApi from '@/api/shiftScheduling/schedulingTask';

export default {
    name: 'SmartSchedulingSystemRuleSet',

    data() {
        return {
            labelPosition: 'right',

            //规则id
            ruleId: undefined,
            //taskId
            taskId: undefined,
            //从什么路由跳转过来的
            fromPath: "",
            //更新加载
            updateLoading: false,

            ////排班规则设置
            schedulingRuleVo: {},
            ///门店工作时间段
            storeWorkTimeFrame: {},
            ///开店规则
            openStoreRule: {
                //安排人数=门店面积/50
                variableParam: 50,
                prepareMinute: 30,
                positionIdArr: []
            },

            ///关店规则
            closeStoreRule: {
                //安排人数=门店面积/50+2
                variableParam1: 50,
                variableParam2: 2,
                closeMinute: 30,
                positionIdArr: []
            },

            ///正常班规则
            normalRule: {
                //安排人数=预测客流量/50
                variableParam: 3.8,
                positionIdArr: []
            },

            ///无客流量规则
            noPassengerRule: {
                staffNum: 2
            },

            ///正常班次规则
            normalShiftRule: {
                //安排人数=预测客流量/3.8
                variableParam: 3.8,
                positionIdArr: []
            },
            ///午餐时间规则
            lunchTimeRule: {
                //时间范围
                timeFrame: "",
                needMinute: 30
            },
            ///晚餐时间规则
            dinnerTimeRule: {
                //时间范围
                timeFrame: "",
                needMinute: 30
            },

            ///班次数量
            shiftNumOptions: [
                { value: 1, label: 1 },
                { value: 2, label: 2 },
                { value: 3, label: 3 },
                { value: 4, label: 4 },
                { value: 5, label: 5 },
                { value: 6, label: 6 },
                { value: 7, label: 7 },
            ],
            ////职位选择
            //展示的内容对应
            positionProps: {
                label: 'label',
                children: 'children'
            },
            ///开店规则
            openStoreDialogVisible: false,
            ///关店规则
            closeStoreDialogVisible: false,
            ///无客流量规则
            normalDialoVisible: false,
            //职位选择树
            positionSelectTree: [],
        };
    },

    mounted() {


    },

    beforeRouteEnter(to, from, next) {
        next(vm => {
            //要通过方法来设置，不能直接设置
            vm.setFromPath(from.path);
        })
    },

    created() {
        if (this.$route.query.ruleId) {
            this.ruleId = this.$route.query.ruleId;
        }
        if (this.$route.query.taskId) {
            this.taskId = this.$route.query.taskId;
            console.log("this.taskId:" + this.taskId)
        }
        this.initMessage()
    },

    methods: {

        setFromPath(path) {
            this.fromPath = path;
            console.log("fromPath:" + this.fromPath)
        },

        ////初始化信息
        initMessage() {
            if (this.ruleId != undefined) {
                schedulingRuleApi.getSchedulingRuleVoById(this.ruleId).then(
                    res => {
                        this.schedulingRuleVo = res.schedulingRuleVo

                        if (this.schedulingRuleVo.storeWorkTimeFrame != null && this.schedulingRuleVo.storeWorkTimeFrame != "") {
                            this.storeWorkTimeFrame = JSON.parse(this.schedulingRuleVo.storeWorkTimeFrame)
                        }
                        //开店规则
                        if (this.schedulingRuleVo.openStoreRule != null) {
                            this.openStoreRule = JSON.parse(this.schedulingRuleVo.openStoreRule)
                        }
                        if (this.schedulingRuleVo.closeStoreRule != null) {
                            this.closeStoreRule = JSON.parse(this.schedulingRuleVo.closeStoreRule)
                        }
                        if (this.schedulingRuleVo.normalRule != null) {
                            this.normalRule = JSON.parse(this.schedulingRuleVo.normalRule)
                        }
                        if (this.schedulingRuleVo.noPassengerRule != null) {
                            this.noPassengerRule = JSON.parse(this.schedulingRuleVo.noPassengerRule)
                        }
                        if (this.schedulingRuleVo.normalShiftRule != null) {
                            this.normalShiftRule = JSON.parse(this.schedulingRuleVo.normalShiftRule)
                        }
                        if (this.schedulingRuleVo.lunchTimeRule != null) {
                            this.lunchTimeRule = JSON.parse(this.schedulingRuleVo.lunchTimeRule)
                        }
                        if (this.schedulingRuleVo.dinnerTimeRule != null) {
                            this.dinnerTimeRule = JSON.parse(this.schedulingRuleVo.dinnerTimeRule)
                        }

                        //职位选择树
                        this.positionSelectTree = this.schedulingRuleVo.positionSelectTree
                    }
                )
            } else {
                schedulingRuleApi.getSchedulingRuleVoByStoreId().then(
                    res => {
                        this.schedulingRuleVo = res.schedulingRuleVo

                        if (this.schedulingRuleVo.storeWorkTimeFrame != null && this.schedulingRuleVo.storeWorkTimeFrame != "") {
                            this.storeWorkTimeFrame = JSON.parse(this.schedulingRuleVo.storeWorkTimeFrame)
                        }
                        //开店规则
                        if (this.schedulingRuleVo.openStoreRule != null) {
                            this.openStoreRule = JSON.parse(this.schedulingRuleVo.openStoreRule)
                        }
                        if (this.schedulingRuleVo.closeStoreRule != null) {
                            this.closeStoreRule = JSON.parse(this.schedulingRuleVo.closeStoreRule)
                        }
                        if (this.schedulingRuleVo.normalRule != null) {
                            this.normalRule = JSON.parse(this.schedulingRuleVo.normalRule)
                        }
                        if (this.schedulingRuleVo.noPassengerRule != null) {
                            this.noPassengerRule = JSON.parse(this.schedulingRuleVo.noPassengerRule)
                        }
                        if (this.schedulingRuleVo.normalShiftRule != null) {
                            this.normalShiftRule = JSON.parse(this.schedulingRuleVo.normalShiftRule)
                        }
                        if (this.schedulingRuleVo.lunchTimeRule != null) {
                            this.lunchTimeRule = JSON.parse(this.schedulingRuleVo.lunchTimeRule)
                        }
                        if (this.schedulingRuleVo.dinnerTimeRule != null) {
                            this.dinnerTimeRule = JSON.parse(this.schedulingRuleVo.dinnerTimeRule)
                        }

                        //职位选择树
                        this.positionSelectTree = this.schedulingRuleVo.positionSelectTree
                    }
                )
            }

        },

        ////职位选择
        //获取当前所选中的职位名称
        getCheckPosition(positionIdArr) {
            let checkStr = ""
            for (let i = 0; i < this.positionSelectTree.length; i++) {
                for (let j = 0; j < positionIdArr.length; j++) {
                    if (positionIdArr[j] == this.positionSelectTree[i].id) {
                        checkStr += this.positionSelectTree[i].label + " ；"
                    }
                }
            }
            return checkStr
        },
        ///开店规则
        openStorePositionSelect() {
            this.openStoreDialogVisible = true
        },
        //保存开店规则所选中的职位
        saveOpenStorePosition() {
            this.openStoreDialogVisible = false
            this.openStoreRule.positionIdArr = this.$refs.openStoreTree.getCheckedKeys()
        },
        openStoreHandleCheckChange(data, checked, indeterminate) {
            // console.log("data：" + JSON.stringify(data));
            // console.log("checked:" + JSON.stringify(checked));
            // console.log("indeterminate:" + JSON.stringify(indeterminate));
            // console.log("openStorePositionCheckArr:" + JSON.stringify(this.openStorePositionCheckArr))
            // console.log("当前选中的节点:" + this.$refs.openStoreTree.getCheckedKeys())
        },
        ///关店规则
        saveCloseStorePosition() {
            this.closeStoreDialogVisible = false
            this.closeStoreRule.positionIdArr = this.$refs.closeStoreTree.getCheckedKeys()
        },
        closeStorePositionSelect() {
            this.closeStoreDialogVisible = true
        },
        ///无客流量规则
        saveNoPassengerPosition() {
            this.normalDialoVisible = false
            this.normalRule.positionIdArr = this.$refs.normalTree.getCheckedKeys()
        },
        normalPositionSelect() {
            this.normalDialoVisible = true
        },

        ////保存数据
        saveRuleData() {
            console.log("this.storeWorkTimeFrame:" + JSON.stringify(this.storeWorkTimeFrame))
            this.schedulingRuleVo.storeWorkTimeFrame = JSON.stringify(this.storeWorkTimeFrame)
            this.schedulingRuleVo.openStoreRule = JSON.stringify(this.openStoreRule)
            this.schedulingRuleVo.closeStoreRule = JSON.stringify(this.closeStoreRule)
            this.schedulingRuleVo.normalRule = JSON.stringify(this.normalRule)
            this.schedulingRuleVo.noPassengerRule = JSON.stringify(this.noPassengerRule)
            this.schedulingRuleVo.normalShiftRule = JSON.stringify(this.normalShiftRule)
            this.schedulingRuleVo.lunchTimeRule = JSON.stringify(this.lunchTimeRule)
            this.schedulingRuleVo.dinnerTimeRule = JSON.stringify(this.dinnerTimeRule)

            if (this.schedulingRuleVo.id) {
                if (this.taskId) {
                    this.updateLoading = true;
                }
                //--if--修改数据
                schedulingRuleApi.updateById(this.schedulingRuleVo).then(
                    res => {
                        this.$message.success("修改规则成功")
                    }
                )
                if (this.taskId) {

                    schedulingTaskApi.deleteAllResultOfTask(this.taskId).then(
                        res => {
                            this.updateLoading = false;
                        }
                    )
                }
            } else {
                //--if--保存数据
                schedulingRuleApi.save(this.schedulingRuleVo).then(
                    res => {
                        this.$message.success("保存规则成功")
                    }
                )
            }

            //跳转回原来的页面
            if (this.fromPath && this.$route.query.taskId) {
                this.$router.push(this.fromPath);
            }
        },
        /**
         * 取消返回
         */
        cancel() {
            //跳转回原来的页面
            if (this.fromPath && this.$route.query.taskId) {
                this.$router.push(this.fromPath);
            }
        }
    },
};
</script>

<style lang="scss" scoped>
.ruleContainer {
    padding: 20px;
    height: 100%;

    .operationButton {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 7px;
        background: rgba(255, 255, 255, 0.8);
        // margin-bottom: 30px;
        height: 50px;
        position: relative;
        border: 2px solid #409eff;
        border-radius: 5px;
        /* 添加 position 属性 */

        .pageTitle {
            margin-right: auto;
            margin-left: auto;
            font-family: "SimHei", Arial, sans-serif;
            font-size: 24px;
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

        .operButton {
            margin-left: auto;
        }
    }

    .elRow {
        height: calc(100% - 40px);
        // border-radius: 5px;
        // background: rgba(242, 247, 251, 0.6);
        // backdrop-filter: blur(50px);
        // box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
        //     inset 1px 1px 1px rgba(0, 0, 0, 0.1);

        .elCol {
            height: 100%;
            padding-top: 20px;

            .firstOuterBorder {
                height: 100%;
                min-height: 100px;
                width: 100%;
                border: 2px solid #409eff;
                border-radius: 10px;
                position: relative;

                .firstTitelDiv {
                    font-weight: bold;
                    font-size: 20px;
                    height: 40px;
                    line-height: 40px;
                    background: #FFFFFF;
                    position: absolute;
                    top: -20px;
                    left: 20px;
                    text-align: center;
                    z-index: 1000;
                }

                .firstInsideDiv {
                    height: calc(100% - 10px);
                    padding: 5px 0px 5px 5px;
                    //添加垂直滚动条
                    overflow-y: auto;
                    overflow-x: hidden;
                }

                /* 定义滚动条的轨道和滑块样式 */
                .firstInsideDiv:hover::-webkit-scrollbar-track {
                    background-color: #f1f1f1;
                }

                .firstInsideDiv:hover::-webkit-scrollbar-thumb {
                    background-color: #D2D2D2;
                    border-radius: 5px;
                }

                .firstInsideDiv:hover::-webkit-scrollbar-button {
                    background-color: #D2D2D2;
                    height: 5px;
                }

                /* 隐藏滚动条 */
                .firstInsideDiv::-webkit-scrollbar {
                    // 隐藏滚动条宽度
                    // width: 0 !important;
                    // 隐藏滚动条高度
                    // height: 0 !important;
                    // 隐藏滚动条背景
                    background-color: transparent;
                    width: 5px;
                    height: 5px;
                }

                /* 鼠标进来的时候显示滚动条 */
                .firstInsideDiv:hover::-webkit-scrollbar {
                    // background-color: #f1f1f1;
                    width: 5px;
                    height: 5px;
                }
            }
        }


    }
}

// #bodyDiv {
//     margin: 0;
//     /* min-height: 100vh; */
//     height: 100%;
//     background-color: #c4d7fa;
//     background-image: radial-gradient(closest-side, rgb(255, 255, 255), rgba(235, 105, 78, 0)), radial-gradient(closest-side, rgb(250, 203, 203), rgba(243, 11, 164, 0)), radial-gradient(closest-side, rgb(237, 252, 202), rgba(254, 234, 131, 0)), radial-gradient(closest-side, rgb(197, 248, 241), rgba(170, 142, 245, 0)), radial-gradient(closest-side, rgb(206, 200, 243), rgba(248, 192, 147, 0));
//     background-size: 130vmax 130vmax, 80vmax 80vmax, 90vmax 90vmax, 110vmax 110vmax, 90vmax 90vmax;
//     background-position: -80vmax -80vmax, 60vmax -30vmax, 10vmax 10vmax, -30vmax -10vmax, 50vmax 50vmax;
//     background-repeat: no-repeat;
// }


.minuteInput {
    width: 60px
}

.positionCheckStrDiv {
    width: 100%;
    // border: 1px solid #cbd2d9;
    // border-radius: 3px;
}


.secondOuterBorder {
    min-height: 100px;
    width: 100%;
    border: 1px solid #409eff;
    border-radius: 7px;
    position: relative;
}


.secondTitelDiv {
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
    // font-weight: bold;
    font-size: 16px;
    height: 40px;
    line-height: 40px;
    background: #FFFFFF;
    position: absolute;
    top: -20px;
    left: 20px;
    text-align: center
}

.el-row {
    margin-bottom: 0px;

    &:last-child {
        margin-bottom: 0;
    }
}

.el-col {
    border-radius: 4px;
}

// 时间选择器
.timePickerClass {
    width: 230px
}

.bg-purple-dark {
    background: #99a9bf;
}

.bg-purple {
    background: #d3dce6;
}

.bg-purple-light {
    background: #e5e9f2;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}

.row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
}
</style>