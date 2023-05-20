<template>
    <div class="containerDiv">
        <div>
            <el-row :gutter="10">
                <el-col :span="4" :xs="24" class="colDiv">
                    <div class="timeTreeDiv">
                        <div class="titleDiv" style="justify-content: space-between;">
                            <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">任务创建时间树</span>
                            <span>
                                <el-button class="iconfont icon-quanbu" @click="searchAll()" type="text"
                                    style="font-weight: bold;font-size: 18px;" id="button-black"></el-button>
                                <el-button id="button-black" style="font-weight: bold;" type="text"
                                    icon="iconfont icon-expand" v-if="isExpand == false" size="mini"
                                    @click="foldOrExpand()"></el-button>
                                <el-button id="button-black" style="font-weight: bold;" type="text"
                                    icon="iconfont icon-zhedie" v-if="isExpand == true" size="mini"
                                    @click="foldOrExpand()"></el-button>
                            </span>
                        </div>
                        <div class="insideContainer">
                            <div style="padding: 10px;0px;0px;10px;">
                                <el-tree class="filter-tree" :data="dateTree" :props="defaultProps"
                                    :filter-node-method="filterNode" :default-expanded-keys="treeExpandedKeyArr"
                                    @node-click="handleNodeClick" ref="dateTree" node-key="value"
                                    :default-expand-all="true">
                                    <span class="custom-tree-node" slot-scope="{ node, data }">
                                        <i class="iconfont icon-nianfen" v-if="(node.label + '').indexOf('年') != -1"></i>
                                        <i class="iconfont icon-yuefen" v-if="(node.label + '').indexOf('月') != -1"></i>
                                        <span style="font-size: 14px;margin-left: 5px;">{{ node.label }}</span>
                                    </span>
                                </el-tree>
                            </div>
                        </div>
                    </div>

                </el-col>
                <el-col :span="20" :xs="24" class="colDiv">
                    <div class="taskDiv">
                        <div class="titleDiv">
                            <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">排班任务</span>
                            <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable
                                style="width: 150px;margin-right: 10px;" @keyup.enter.native="handleQuery" size="small" />
                            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
                        </div>

                        <div class="insideContainer">
                            <div class="toolDiv">
                                <el-button type="text" size="small" @click="handleAdd">
                                    <i class="iconfont icon-tianjia" style="margin-right: 3px;"></i>添加</el-button>
                                <el-button id="button-danger" type="text" icon="el-icon-delete" size="small"
                                    :disabled="dataListSelections.length <= 0" @click="handleDelete()">批量删除</el-button>
                                <el-button id="button-success" type="text" icon="iconfont icon-liujisuan" size="small"
                                    :disabled="dataListSelections.length <= 0" @click="handleCalculate()">
                                    批量计算</el-button>
                            </div>

                            <div class="taskElTableDiv">
                                <el-table :data="taskList" @selection-change="handleSelectionChange" height="100%">
                                    <el-table-column type="selection" width="50" align="center" fixed />
                                    <el-table-column label="任务编号" key="id" prop="id" fixed />
                                    <el-table-column label="任务名称" key="name" prop="name" width="150" />
                                    <el-table-column label="时间段长（分钟）" key="duration" prop="duration" width="150" />
                                    <el-table-column label="排班时间（时间段）" key="intervalc" prop="intervalc" width="150" />
                                    <el-table-column prop="stepOneAlg" label="一阶段算法" width="180">
                                    </el-table-column>
                                    <el-table-column prop="stepTwoAlg" label="二阶段算法" width="180">
                                        <template slot-scope="scope">
                                            <span>{{ scope.row.stepTwoAlg[1] }}</span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="起始日期" key="startDate" prop="startDate" :formatter="formatDate"
                                        width="120" />
                                    <el-table-column label="结束日期" key="endDate" prop="endDate" :formatter="formatDate"
                                        width="120" />
                                    <el-table-column label="任务状态" key="status" prop="status">
                                        <template slot-scope="scope">
                                            <el-tag type="info" v-if="scope.row.status == 0">新创建</el-tag>
                                            <el-tag v-if="scope.row.status == 1"><i class="el-icon-loading"></i>计算中</el-tag>
                                            <el-tag type="success" v-if="scope.row.status == 2">
                                                <!-- <a :href="resultUrl + '?taskId=' + scope.row.id"
                                        style="text-decoration: underline;">查看结果</a> -->
                                                <span @click="viewResultOftask(scope.row.id)">查看结果</span>
                                            </el-tag>
                                            <el-tag type="danger" v-if="scope.row.status == 3">计算失败</el-tag>
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="发布状态" key="isPublish" prop="status" width="200">
                                        <template slot-scope="scope">
                                            <el-switch style="display: block" v-model="scope.row.isPublish"
                                                active-color="#13ce66" inactive-color="#ff4949" active-text="已发布"
                                                inactive-text="未发布"
                                                @change="publistStatusChange(scope.row.id, scope.row.isPublish)">
                                            </el-switch>
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="创建时间" key="createTime" prop="createTime" width="150" sortable />
                                    <el-table-column label="更新时间" key="updateTime" prop="updateTime" width="150" />
                                    <el-table-column label="操作" align="center" width="100"
                                        class-name="small-padding fixed-width" fixed="right">
                                        <template slot-scope="scope">
                                            <el-dropdown @command="handleCommand" trigger="click">
                                                <span class="el-dropdown-link">
                                                    操作按钮<i class="el-icon-arrow-down el-icon--right"></i>
                                                </span>
                                                <el-dropdown-menu slot="dropdown">
                                                    <el-dropdown-item icon="el-icon-edit"
                                                        :command="{ row: scope.row, type: 'handleUpdate' }"
                                                        id="button-primary">修改</el-dropdown-item>

                                                    <el-dropdown-item icon="el-icon-edit"
                                                        :command="{ row: scope.row, type: 'editSchedulingRule' }"
                                                        id="button-primary">修改排班规则</el-dropdown-item>

                                                    <el-dropdown-item icon="iconfont icon-shezhi"
                                                        :command="{ row: scope.row, type: 'workDaySet' }"
                                                        id="button-primary">工作日设置</el-dropdown-item>

                                                    <el-dropdown-item icon="iconfont icon-daoru"
                                                        :command="{ row: scope.row, type: 'passengerFlowImport' }"
                                                        id="button-primary">客流量数据导入</el-dropdown-item>

                                                    <el-dropdown-item icon="el-icon-delete"
                                                        :command="{ row: scope.row, type: 'deleteTaskResult' }"
                                                        id="button-danger">删除计算结果</el-dropdown-item>

                                                    <el-dropdown-item icon="iconfont icon-liujisuan"
                                                        :command="{ row: scope.row, type: 'openAlgoGroupSelectVisible' }"
                                                        id="button-primary">多算法计算</el-dropdown-item>

                                                    <el-dropdown-item icon="iconfont icon-jieguo"
                                                        :command="{ row: scope.row, type: 'openAlgoGroupResultVisible' }"
                                                        id="button-success">多算法结果</el-dropdown-item>
                                                </el-dropdown-menu>
                                            </el-dropdown>

                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                            <pagination v-show="total > 0" :total="total" :page.sync="pageNum" :limit.sync="pageSize"
                                @pagination="getList" />
                        </div>
                    </div>

                </el-col>
            </el-row>

            <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body v-loading="updateLoading"
                element-loading-background="rgba(0, 0, 0, 0.8)" element-loading-text="正在修改任务，修改相关信息需要删除任务计算结果和多算法计算结果，请稍等"
                element-loading-spinner="el-icon-loading">

                <el-form ref="schedulingTaskForm" :model="schedulingTaskForm" :rules="rules" label-width="95px">
                    <el-form-item label="任务名称" prop="name">
                        <el-input v-model="schedulingTaskForm.name" placeholder="请输入任务名称" maxlength="255" size="small" />
                    </el-form-item>
                    <el-form-item label="时间段长" prop="duration">
                        <div style="display: flex">
                            <el-input v-model="schedulingTaskForm.duration" placeholder="以多少分钟为一段" size="small" />
                            <span style="width: 40px;margin-left: 10px;">&emsp;分钟</span>
                        </div>
                    </el-form-item>
                    <el-form-item label="排班时间" prop="intervalc">
                        <div style="display: flex">
                            <el-input v-model="schedulingTaskForm.intervalc" placeholder="以多少段为一个时间单位来进行排班" size="small" />
                            <span style="width: 60px;margin-left: 10px;">&emsp;时间段</span>
                        </div>
                    </el-form-item>

                    <el-form-item label="排班日期" prop="intervalc">
                        <el-date-picker v-model="dateArr" type="daterange" align="left" unlink-panels range-separator="至"
                            start-placeholder="开始日期" end-placeholder="结束日期" @change="dateChange()"
                            :picker-options="pickerOptions" size="small">
                        </el-date-picker>
                    </el-form-item>

                    <el-form-item label="一阶段算法" prop="stepOneAlg">
                        <el-tooltip class="item" effect="light" placement="right-end">
                            <div v-html="alContent" slot="content"></div>
                            <el-select v-model="schedulingTaskForm.stepOneAlg" filterable placeholder="请选择第一阶段算法"
                                size="small" style="width:100%">
                                <el-option v-for="item in stepOneAloptions" :key="item.value" :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-tooltip>

                    </el-form-item>
                    <el-form-item label="二阶段算法" prop="stepTwoAlg">
                        <div style="display: flex;">
                            <el-tooltip class="item" effect="light" placement="right-end">
                                <div v-html="alContent" slot="content"></div>
                                <el-cascader v-model="schedulingTaskForm.stepTwoAlg" :options="stepTwoAloptions"
                                    style="width:100%" @change="stepTwoAlgChange" size="small"></el-cascader>

                            </el-tooltip>
                            <el-tooltip class="item" effect="light" placement="right-end" content="对算法的参数进行设置，不设置使用默认参数"
                                v-if="algoType === 'SA' || algoType === 'TS' || algoType === 'VNS' || algoType === 'ALNS' || algoType === 'AGA' || algoType === 'ILS'">
                                <el-button type="text" style="margin-left: 10px;" @click="algoParamSet()">参数设置</el-button>
                            </el-tooltip>
                        </div>

                    </el-form-item>
                </el-form>

                <!-- 算法参数设置 -->
                <el-dialog width="30%" title="SA算法参数设置" :visible.sync="saInnerVisible" append-to-body>
                    <el-form label-width="80px">
                        <el-form-item label="温度">
                            <el-input v-model="stepTwoAlgParam.sa_T" placeholder="温度"></el-input>
                        </el-form-item>
                        <el-form-item label="降温速度">
                            <el-input v-model="stepTwoAlgParam.sa_a" placeholder="降温速度"></el-input>
                        </el-form-item>
                        <el-form-item label="时间限制">
                            <el-input v-model="stepTwoAlgParam.sa_timer" placeholder="时间限制"></el-input>
                        </el-form-item>
                    </el-form>
                </el-dialog>
                <el-dialog width="30%" title="TS算法参数设置" :visible.sync="tsInnerVisible" append-to-body>
                    <el-form label-width="100px">
                        <el-form-item label="禁忌表长度">
                            <el-input v-model="stepTwoAlgParam.ts_tabuLen" placeholder="禁忌表长度"></el-input>
                        </el-form-item>
                        <el-form-item label="搜索领域个数">
                            <el-input v-model="stepTwoAlgParam.ts_N" placeholder="搜索领域个数"></el-input>
                        </el-form-item>
                        <el-form-item label="时间限制">
                            <el-input v-model="stepTwoAlgParam.ts_timer" placeholder="时间限制"></el-input>
                        </el-form-item>
                    </el-form>
                </el-dialog>
                <el-dialog width="30%" title="VNS算法参数设置" :visible.sync="vnsInnerVisible" append-to-body>
                    <el-form>
                        <el-form-item label="最大可接受未提高解次数">
                            <el-input v-model="stepTwoAlgParam.ils_maxLocalSearchNoLiftCnt"
                                placeholder="局部搜索中最大可接受的没有提高解的次数"></el-input>
                        </el-form-item>
                        <el-form-item label="扰动最优解次数">
                            <el-input v-model="stepTwoAlgParam.ils_disturbanceCnt" placeholder="扰动最优解的次数"></el-input>
                        </el-form-item>
                        <el-form-item label="扰动时对序列进行的分组数">
                            <el-input v-model="stepTwoAlgParam.ils_groupCnt" placeholder="扰动时对序列进行的分组数"></el-input>
                        </el-form-item>
                        <el-form-item label="时间限制">
                            <el-input v-model="stepTwoAlgParam.ils_timer" placeholder="时间限制"></el-input>
                        </el-form-item>
                    </el-form>
                </el-dialog>
                <el-dialog width="900px" title="ALNS算法参数设置" :visible.sync="alnsInnerVisible" append-to-body>
                    <el-row :gutter="20">
                        <el-col :span="6">
                            <el-form>
                                <el-form-item label="模拟退火温度">
                                    <el-input v-model="stepTwoAlgParam.alns_T" placeholder="模拟退火温度"></el-input>
                                </el-form-item>
                                <el-form-item label="降温速度">
                                    <el-input v-model="stepTwoAlgParam.alns_a" placeholder="降温速度"></el-input>
                                </el-form-item>
                                <el-form-item label="权重更新系数">
                                    <el-input v-model="stepTwoAlgParam.alns_ro" placeholder="权重更新系数"></el-input>
                                </el-form-item>
                            </el-form>
                        </el-col>
                        <el-col :span="9">
                            <el-form>

                                <el-form-item label="领域搜索次数限制">
                                    <el-input v-model="stepTwoAlgParam.alns_N" placeholder="领域搜索次数限制"></el-input>
                                </el-form-item>
                                <el-form-item label="如果临时解优于最优解时的得分">
                                    <el-input v-model="stepTwoAlgParam.alns_score1" placeholder="如果临时解优于最优解时的得分"></el-input>
                                </el-form-item>
                                <el-form-item label="如果临时解优于当前解的得分">
                                    <el-input v-model="stepTwoAlgParam.alns_score2" placeholder="如果临时解优于当前解的得分"></el-input>
                                </el-form-item>

                            </el-form>
                        </el-col>
                        <el-col :span="9">
                            <el-form>
                                <el-form-item label="如果满足模拟退火算法Metropolis准则的得分">
                                    <el-input v-model="stepTwoAlgParam.alns_score3"
                                        placeholder="如果满足模拟退火算法Metropolis准则的得分"></el-input>
                                </el-form-item>
                                <el-form-item label="如果以上都没有满足时的得分">
                                    <el-input v-model="stepTwoAlgParam.alns_score4" placeholder="如果以上都没有满足时的得分"></el-input>
                                </el-form-item>
                                <el-form-item label="时间限制">
                                    <el-input v-model="stepTwoAlgParam.alns_timer" placeholder="时间限制"></el-input>
                                </el-form-item>
                            </el-form>
                        </el-col>
                    </el-row>

                </el-dialog>
                <el-dialog width="500px" title="AGA算法参数设置" :visible.sync="agaInnerVisible" append-to-body>
                    <el-form>
                        <el-form-item label="popSize">
                            <el-input v-model="stepTwoAlgParam.aga_popSize" placeholder="种群大小"></el-input>
                        </el-form-item>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="基因变异概率下界">
                                    <el-input v-model="stepTwoAlgParam.aga_mutationRateBoundArr[0]"
                                        placeholder="基因变异概率下界"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="基因变异概率上界">
                                    <el-input v-model="stepTwoAlgParam.aga_mutationRateBoundArr[1]"
                                        placeholder="基因变异概率上界"></el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="基因交叉概率下界">
                                    <el-input v-model="stepTwoAlgParam.aga_crossoverRateBoundArr[0]"
                                        placeholder="基因交叉概率下界"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="基因交叉概率上界">
                                    <el-input v-model="stepTwoAlgParam.aga_crossoverRateBoundArr[1]"
                                        placeholder="基因交叉概率上界"></el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="时间限制">
                            <el-input v-model="stepTwoAlgParam.aga_timer" placeholder="时间限制"></el-input>
                        </el-form-item>
                    </el-form>
                </el-dialog>
                <el-dialog width="30%" title="ILS算法参数设置" :visible.sync="ilsInnerVisible" append-to-body>
                    <el-form label-width="80px">
                        <el-form-item label="时间限制">
                            <el-input v-model="stepTwoAlgParam.vns_timer" placeholder="时间限制"></el-input>
                        </el-form-item>
                    </el-form>
                </el-dialog>

                <div slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确 定</el-button>
                    <el-button @click="cancel">取 消</el-button>
                </div>
            </el-dialog>

            <!-- 工作日设置 -->
            <el-dialog title="工作日设置" :visible.sync="workDaySetDialogVisible" width="50%" v-loading="updateLoading"
                element-loading-background="rgba(0, 0, 0, 0.8)" element-loading-text="正在修改任务，修改相关信息需要删除任务计算结果和多算法计算结果，请稍等"
                element-loading-spinner="el-icon-loading">

                <div style="margin-bottom: 10px;margin-right:20px;text-align: right;">
                    <el-button size="medium" type="text" id="button-primary" style="margin-right:10px"
                        @click="allWorkDaySelect()" v-if="isSelectAllWorkDay === false">
                        <i class="iconfont icon-ziyuan" style="margin-right:5px" />全选</el-button>
                    <el-button size="medium" type="text" id="button-primary" style="margin-right:10px"
                        @click="allWorkDaySelect()" v-if="isSelectAllWorkDay === true">
                        <i class="iconfont icon-ziyuan1" style="margin-right:5px" />全不选</el-button>
                    <el-button size="medium" type="text" id="button-success" style="margin-right:10px"
                        @click="excludeWeekend()" v-if="isExcludeWeekend === true">
                        <i class="iconfont icon-paichu" style="margin-right:5px" />排除周末</el-button>
                    <el-button size="medium" type="text" id="button-success" style="margin-right:10px"
                        @click="excludeWeekend()" v-if="isExcludeWeekend === false">
                        <i class="iconfont icon-paichu" style="margin-right:5px" />包括周末</el-button>
                    <el-button size="medium" type="text" id="button-success" v-if="isExcludeFestival === true"
                        @click="excludeFestival()">
                        <i class="iconfont icon-jiejiarifanganguanli" style="margin-right:5px" />排除节日</el-button>
                    <el-button size="medium" type="text" id="button-success" v-if="isExcludeFestival === false"
                        @click="excludeFestival()">
                        <i class="iconfont icon-jiejiarifanganguanli" style="margin-right:5px" />包括节日</el-button>
                </div>
                <div style="display: block;">
                    <el-calendar :range="dateRange" v-model="todayDate" :key='calendarKey'>
                        <template slot="dateCell" slot-scope="{date, data}">
                            <div @click="dateSelect(date, data)"
                                style="width:100%;height: 100%;display: flex;flex-direction: column;"
                                v-if="new Date(curTask.startDate).getTime() <= new Date(date).getTime() && new Date(date).getTime() <= new Date(curTask.endDate).getTime() + 3600 * 1000 * 24">
                                <div style="flex:1;display: flex; width: 100%;">
                                    <div :class="data.isSelected ? 'is-selected' : ''" style="flex:1;">
                                        {{ data.day.split('-').slice(1).join('-') }}
                                    </div>
                                    <div v-if="judgeWhetherOneDayIsFestival(new Date(date)) != ''"
                                        style="flex:1;justify-content: flex-end;">
                                        <el-tag type="success" size="mini" style="float: right;">{{
                                            judgeWhetherOneDayIsFestival(new Date(date)) }}</el-tag>
                                    </div>
                                </div>
                                <div style="flex:1;display: flex;width: 100%;">
                                    <div style="justify-content: center;align-items:center;display: inline;" v-if="dateVoMap[new Date(date).toLocaleDateString()] != null &&
                                        dateVoMap[new Date(date).toLocaleDateString()].isNeedWork != undefined &&
                                        dateVoMap[new Date(date).toLocaleDateString()].isNeedWork == true">
                                        ✔️
                                    </div>
                                </div>

                            </div>
                        </template>

                    </el-calendar>
                </div>

                <span slot="footer" class="dialog-footer">
                    <el-button @click="workDaySetDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveWorkDay()">确 定</el-button>
                </span>
            </el-dialog>

            <!-- 客流量数据导入 -->
            <el-dialog title="客流量数据导入" :visible.sync="passengerFlowImportDialogVisible" width="30%">

                <el-upload class="upload-demo" :action="excelAction" :data="uploadPostData" :on-preview="handlePreview"
                    :on-remove="handleRemove" :before-remove="beforeRemove" :on-success="handleSuccess"
                    :headers="uploadHeaders" multiple :limit="1" :on-exceed="handleExceed" :file-list="fileList"
                    style="margin-top: 10px;width: 100%;">
                    <div style="display: flex;">
                        <div class="fileNameClass"><el-tag>所导入文件</el-tag>&emsp;{{ fileName }}</div>
                        <el-button size="small" type="primary" style="margin-left: 10px;height: 43px;">文件上传</el-button>
                    </div>
                    <div slot="tip" class="el-upload__tip" style="color: red;">
                        只能上传xlxs、xls文件，且不超过50MB
                    </div>
                </el-upload>

                <span slot="footer" class="dialog-footer">
                    <el-button @click="exitExcelImport">退 出</el-button>
                </span>
            </el-dialog>

            <!-- 算法全组合勾选 -->
            <el-dialog width="40%" title="算法组合勾选" :visible.sync="algoGroupSelectVisible">
                <template>

                    <el-checkbox :indeterminate="algoGroupIsIndeterminate" v-model="algoGroupCheckAll"
                        @change="handleAlgoGroupCheckAllChange">全选</el-checkbox>
                    <div style="margin: 15px 0;"></div>
                    <el-checkbox-group v-model="checkedAlgoGroups" @change="handleCheckedAlgoGroupsChange">
                        <el-checkbox v-for="algoGroup in algoGroupOptions" :label="algoGroup" :key="algoGroup">{{ algoGroup
                        }}</el-checkbox>
                    </el-checkbox-group>

                    <div slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="multiAlgorithmSolve()">执行计算</el-button>
                        <el-button @click="algoGroupSelectVisible = false">取 消</el-button>
                    </div>

                </template>

            </el-dialog>

            <!-- 多算法结果查看 -->
            <el-dialog width="80%" title="多算法结果查看" :visible.sync="allAlgoInnerVisible">
                <el-button type="primary" icon="el-icon-refresh" style="float: right;margin-bottom: 10px;margin-left: 10px;"
                    @click="listMultiAlgorithmResult(2, algoGroupResultVisibleTaskId)">刷新表格</el-button>
                <el-select v-model="orderType" placeholder="请选择数据排序类型" style="float: right;margin-bottom: 10px;"
                    @change="listMultiAlgorithmResult(0, algoGroupResultVisibleTaskId)">
                    <el-option v-for="item in orderOptions" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
                <el-table :data="allAlgoTableData" style="width: 100%">
                    <el-table-column prop="stepOneAlg" label="一阶段算法" width="180">
                    </el-table-column>
                    <el-table-column prop="stepTwoAlg" label="二阶段算法" width="180">
                        <template slot-scope="scope">
                            <span>{{ scope.row.stepTwoAlg[1] }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="totalMinute" label="总班次时长（min）" width="180">
                    </el-table-column>
                    <el-table-column prop="totalAssignedMinute" label="已分配班次时长（min）" width="180">
                    </el-table-column>
                    <el-table-column prop="allocationRatio" label="分配比率" width="180">
                        <template slot-scope="scope">
                            <span> {{ Math.floor(scope.row.allocationRatio * 10000) / 100 }}%</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="calculateTime" label="计算时间（s）" width="180">
                        <template slot-scope="scope">
                            <span> {{ Math.floor(scope.row.calculateTime) / 1000 }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" width="180">
                        <template slot-scope="scope">
                            <el-tag type="info" v-if="scope.row.status == 0">新创建</el-tag>
                            <el-tag v-if="scope.row.status == 1"><i class="el-icon-loading"></i>计算中</el-tag>
                            <el-tag type="success" v-if="scope.row.status == 2">
                                <span @click="viewResultOftask(scope.row.id)">查看结果</span>
                                <!-- <a :href="resultUrl + '?taskId=' + scope.row.id" style="text-decoration: underline;" >查看结果</a> -->
                            </el-tag>
                            <el-tag type="danger" v-if="scope.row.status == 3">计算失败</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="操作" width="180">
                        <template slot-scope="scope">
                            <el-button size="small" type="text" @click="overlayResult(scope.row.id)">覆盖任务结果</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <pagination v-show="allAlgoTotal > 0" :total="allAlgoTotal" :page.sync="allAlgoPageNum"
                    :limit.sync="allAlgoPageSize" @pagination="listMultiAlgorithmResult(1, algoGroupResultVisibleTaskId)" />
            </el-dialog>

            <el-dialog title="任务结果查看" width="80%" :visible.sync="taskResultVisible">
                <div style="height:80vh">
                    <ResultView :taskId="viewResultTaskId" :key="resultViewKey"></ResultView>
                </div>
            </el-dialog>
        </div>

    </div>
</template>

<script>
import schedulingTaskApi from '@/api/shiftScheduling/schedulingTask';
import shiftSchedulingCalculateApi from '@/api/shiftScheduling/shiftSchedulingCalculate';
import festivalApi from '@/api/enterprise/festival';
import { BASE_URL } from "@/utils/request";
import { getToken } from '@/utils/auth'
import shiftSchedulingUtil from '@/utils/shiftScheduling/shiftSchedulingUtil';
import ResultView from '../result/result.vue';
// import backgroundCss from '@/styles/background/backgroundCss.css'

//前端项目地址
const FRONT_END_PATH = process.env.VUE_APP_FRONT_END_PATH
const IP_AND_PORT = process.env.VUE_APP_IP_AND_PORT
const VUE_APP_BASE_API = process.env.VUE_APP_BASE_API
const REQUEST_TIMEOUT = process.env.VUE_APP_REQUEST_TIMEOUT

let socket;

export default {
    name: 'SmartSchedulingSystemList',
    components: { ResultView },
    data() {
        return {
            //查看任务结果的路由
            resultUrl: FRONT_END_PATH + "/?#/scheduling/result",

            //更新任务loading
            updateLoading: false,
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 表单参数
            schedulingTaskForm: {
            },
            filterText: '',

            defaultProps: {
                children: 'children',
                label: 'label'
            },
            // 总条数
            pageNum: 1,
            pageSize: 10,
            total: 4,
            // 查询参数
            queryParams: {
                taskName: undefined,
                taskId: undefined
            },
            // 显示搜索条件
            showSearch: true,
            // 选中数组
            dataListSelections: [],
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            // 表格数据
            taskList: [],
            // 表单校验
            rules: {
                name: [
                    { required: true, message: "任务名称不能为空", trigger: "blur" }
                ]
            },

            ////年份、月份树
            dateTree: [],
            treeExpandedKeyArr: [],
            //是否展开树
            isExpand: true,

            ////日期选择器选项
            dateArr: [],
            pickerOptions: {
                disabledDate: this.disabledDate,
                shortcuts: [{
                    text: '今天',
                    onClick(picker) {
                        picker.$emit('pick', new Date());
                    }
                }, {
                    text: '昨天',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() - 3600 * 1000 * 24);
                        picker.$emit('pick', date);
                    }
                }, {
                    text: '一周前',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                        picker.$emit('pick', date);
                    }
                }]
            },

            ////工作日设置
            workDaySetDialogVisible: false,
            //日期范围
            dateRange: ["2000-01-01", "3000-01-01"],
            //工作日
            datevolist: [],
            dateVoMap: new Map(),
            curTaskId: undefined,
            curTask: undefined,
            //工作日日历显示的月份
            todayDate: undefined,
            //修改这个数值，用来刷新组件
            calendarKey: 0,
            //是否全选所有工作日
            isSelectAllWorkDay: false,
            //是否排除工作日
            isExcludeWeekend: false,
            //是否排除工作日
            isExcludeFestival: false,
            //门店的所有节假日
            festivalList: [],
            //数据库中本门店中任务的最大结束时间
            maxEndDate: undefined,
            //查询出taskId之外的其他任务
            otherTaskList: [],

            ////客流量数据导入
            passengerFlowImportDialogVisible: false,
            //文件上传地址
            excelAction: BASE_URL + "/shift-scheduling-calculate-service/schedulingTask/uploadExcelFile",
            //所导入的文件名
            fileName: "",
            //文件列表
            fileList: [],
            //导入文件的同时向后台提交数据
            uploadPostData: {},
            //上传文件的请求头
            uploadHeaders: {
                token: "",
            },

            ////算法选择
            stepOneAloptions: [
            /*     {
                    value: 'BP',
                    label: 'BP'
                }, {
                    value: 'CG',
                    label: 'CG'
                },  */{
                    value: 'GOA',
                    label: 'GOA'
                },/*  {
                    value: 'SC',
                    label: 'SC'
                } */
            ],
            stepTwoAloptions: [
                {
                    value: 'JinSi',
                    label: '近似算法',
                    children: [{
                        value: 'SAEA',
                        label: 'SAEA'
                    }, {
                        value: 'EASA',
                        label: 'EASA'
                    }/* , {
                        value: 'GMMA',
                        label: 'GMMA'
                    }, {
                        value: 'CG',
                        label: 'CG'
                    } */]
                },
           /*      {
                    value: 'YuanQiFa',
                    label: '元启发式算法',
                    children: [{
                        value: 'SA',
                        label: 'SA 模拟退火'
                    }, {
                        value: 'TS',
                        label: 'TS 禁忌搜索'
                    }, {
                        value: 'VNS',
                        label: 'VNS 变邻域搜索'
                    }, {
                        value: 'ALNS',
                        label: 'ALNS 自适应大邻域'
                    }, {
                        value: 'AGA',
                        label: 'AGA 自适应遗传'
                    }, {
                        value: 'ILS',
                        label: 'ILS 迭代局部搜索'
                    }]
                }, */
            ],
            //算法调用提示
            // alContent: "<p>如果追求<span style='color:red'>极致速度</span>，可以使用GOA+GMMA组合；" +
            //     "<p>如果<span style='color:red'>综合考虑求解速度和质量</span>，可以采用B&P+GMMA或者GOA+B&P的组合；</p>" +
            //     "<p>如果追求<span style='color:red'>最高质量解</span>，可以采用B&P+B&P组合；</p>",
            alContent: "<span style='color:red'>最佳速度</span>: GOA+EASA" +
                "<p><span style='color:red'>最佳质量</span>：SC 或者 B&P+元启发式算法</p>" +
                "<p><span style='color:red'>小规模实例(综合质量与速度)</span>: SC+SAEA 或者元启发式算法、GOA+元启发式算法</p>" +
                "<p><span style='color:red'>中等规模实例(综合质量与速度)</span>: CG+元启发式算法</p>" +
                "<p><span style='color:red'>大规模实例(综合质量与速度)</span>: GOA+元启发式算法</p>",
            //算法类型
            algoType: undefined,
            saInnerVisible: false,
            tsInnerVisible: false,
            vnsInnerVisible: false,
            alnsInnerVisible: false,
            agaInnerVisible: false,
            ilsInnerVisible: false,
            allAlgoInnerVisible: false,
            algoGroupResultVisibleTaskId: undefined,
            //第二阶段算法参数
            stepTwoAlgParam: {
                "sa_T": 100,
                "sa_a": 0.1,
                "sa_timer": 2000,

                "ts_tabuLen": 20,
                "ts_N": 25,
                "ts_timer": 2000,

                "ils_maxLocalSearchNoLiftCnt": 50,
                "ils_disturbanceCnt": 5,
                "ils_groupCnt": 10,
                "ils_timer": 2000,

                "alns_T": 100,
                "alns_a": 0.1,
                "alns_ro": 0.6,
                "alns_N": 25,
                "alns_tabuLen": 20,
                "alns_score1": 1.5,
                "alns_score2": 1.2,
                "alns_score3": 0.8,
                "alns_score4": 0.1,
                "alns_timer": 2000,

                "aga_popSize": 20,
                "aga_mutationRateBoundArr": [0.4, 0.6],
                "aga_crossoverRateBoundArr": [0.9, 0.98],
                "aga_timer": 2000,

                "vns_timer": 0,
            },
            stepTwoAlgParamClone: {
                "sa_T": 100,
                "sa_a": 0.1,
                "sa_timer": 2000,

                "ts_tabuLen": 20,
                "ts_N": 25,
                "ts_timer": 2000,

                "ils_maxLocalSearchNoLiftCnt": 50,
                "ils_disturbanceCnt": 5,
                "ils_groupCnt": 10,
                "ils_timer": 2000,

                "alns_T": 100,
                "alns_a": 0.1,
                "alns_ro": 0.6,
                "alns_N": 25,
                "alns_tabuLen": 20,
                "alns_score1": 1.5,
                "alns_score2": 1.2,
                "alns_score3": 0.8,
                "alns_score4": 0.1,
                "alns_timer": 2000,

                "aga_popSize": 20,
                "aga_mutationRateBoundArr": [0.4, 0.6],
                "aga_crossoverRateBoundArr": [0.9, 0.98],
                "aga_timer": 2000,

                "vns_timer": 0,
            },

            ////算法组合勾选
            allAlgoTableData: [],
            allAlgoPageNum: 1,
            allAlgoPageSize: 10,
            allAlgoTotal: 0,
            //排序类型
            orderType: 0,
            orderOptions: [
                { value: 0, label: "按照总班次时长升序排序" },
                { value: 1, label: "按照分配率降序排序" },
                { value: 2, label: "按照计算时间升序排序" }
            ],
            algoGroupOptions: [],
            algoGroupCheckAll: false,
            checkedAlgoGroups: [],
            algoGroupIsIndeterminate: true,
            algoGroupSelectVisible: false,


            ////算法结果查看
            viewResultTaskId: undefined,
            taskResultVisible: false,
            resultViewKey: 1,
        }
    },

    watch: {
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    },

    created() {
        this.getList();
        this.getDateTree();
        this.initWebSocket();
        this.listAllHolidaysOfOneStore();
        this.uploadHeaders.token = getToken();
        // 推荐直接使用js实现
        // this.$nextTick(_ => {
        //     this.$refs.dialog.style.marginTop
        //         = `calc((100vh${this.$refs.dialog.clientHeight}px) / 2)`
        // })
    },

    methods: {
        /** 查询任务列表 */
        getList() {
            // console.log('加载任务列表数据')
            this.queryParams.page = this.pageNum;
            this.queryParams.limit = this.pageSize;
            schedulingTaskApi.list(this.queryParams).then(response => {
                this.taskList = response.page.list;
                for (let i = 0; i < this.taskList.length; i++) {
                    this.taskList[i].stepTwoAlg = JSON.parse(this.taskList[i].stepTwoAlg);
                    this.taskList[i].isPublish = this.taskList[i].isPublish == 1 ? true : false;
                }
                this.total = response.page.totalCount;
            });
        },
        /**
         * 操作按钮
         * @param {*} command
         */
        handleCommand(command) {
            // console.log("row:" + JSON.stringify(row));
            // console.log("command:" + command);
            switch (command.type) {
                case "handleUpdate":
                    if (command.row.isPublish === true) {
                        this.$message.error("当前任务已经发布，无法修改任务信息");
                    } else {
                        this.handleUpdate(command.row);
                    }

                    break;
                case "editSchedulingRule":
                    if (command.row.isPublish === true) {
                        this.$message.error("当前任务已经发布，无法修改任务规则");
                    } else {
                        this.editSchedulingRule(command.row);
                    }
                    break;
                case "workDaySet":
                    if (command.row.isPublish === true) {
                        this.$message.error("当前任务已经发布，无法进行工作日设置");
                    } else {
                        this.workDaySet(command.row);
                    }
                    break;
                case "passengerFlowImport":
                    if (command.row.isPublish === true) {
                        this.$message.error("当前任务已经发布，无法进行客流量导入");
                    } else {
                        this.passengerFlowImport(command.row);
                    }
                    break;
                case "deleteTaskResult":
                    if (command.row.isPublish === true) {
                        this.$message.error("当前任务已经发布，无法删除任务结果");
                    } else {
                        this.deleteTaskResult(command.row.id);
                    }
                    break;
                case "openAlgoGroupSelectVisible":
                    this.openAlgoGroupSelectVisible(command.row.id);
                    break;
                case "openAlgoGroupResultVisible":
                    this.openAlgoGroupResultVisible(command.row.id);
                    break;
            }
        },
        ////年份、月份树
        /**
         * 查询分类下拉树结构
         */
        getDateTree() {
            schedulingTaskApi.listAllDate().then(res => {
                // console.log("res:" + JSON.stringify(res))
                this.dateTree = res.createTimeTreeItemVoList
            })
        },
        // 一键折叠/展开
        foldOrExpand() {
            this.isExpand = !this.isExpand
            this.expandFunc(this.dateTree)
        },
        // 遍历树形数据，通过设置每一项的expanded属性，实现展开与折叠
        expandFunc(data) {
            data.forEach(item => {
                this.$refs.dateTree.store.nodesMap[item.value].expanded = this.isExpand
                if (item.children != null && item.children.length > 0) {
                    this.expandFunc(item.children)
                }
            })
        },
        /**
         * 查询所有月份的任务
         */
        searchAll() {
            this.queryParams.year = undefined;
            this.queryParams.month = undefined;
            this.handleQuery();
        },

        filterNode(value, data) {
            if (!value) return true;
            return data.label.indexOf(value) !== -1;
        },
        // 节点单击事件
        handleNodeClick(data, node, root) {
            // console.log("时间树节点点击，当前节点数据:" + JSON.stringify(data));
            // console.log("时间树节点点击，父节点数据:" + JSON.stringify(node.parent.data));
            let year = node.parent.data.label.split(" ")[0];
            let month = data.label.split(" ")[0];
            // console.log("year:" + year);
            // console.log("month:" + month);
            this.queryParams.year = year;
            this.queryParams.month = month;
            this.handleQuery();
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.pageNum = 1;
            this.getList();
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.queryParams = {
                taskName: undefined,
                taskId: undefined
            },
                this.handleQuery();
        },
        // 表单重置
        reset() {
            console.log('表单重置')
            this.schedulingTaskForm = {
            };
            this.dateArr = [];
        },
        /** 新增按钮操作 */
        handleAdd() {
            // debugger;
            this.reset();
            // listAllUnitmeasure().then(response =>{
            //     this.measureOptions = response.data;
            // });
            // this.getDateTree();
            // if(this.queryParams.itemTypeId != 0){
            //     this.form.itemTypeId = this.queryParams.itemTypeId;
            // }
            this.listOtherTaskList({ taskId: -1 }).then(() => {
                this.getMaxEndDateOfTask().then(
                    () => {
                        this.dateArr = [];
                        this.dateArr[0] = this.maxEndDate;

                        this.open = true;
                        this.title = "新增任务";
                    }
                )

            })
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.listOtherTaskList({ taskId: row.id }).then(
                () => {
                    schedulingTaskApi.info(row.id).then(response => {
                        // console.log("任务数据：" + JSON.stringify(response.schedulingTask))
                        this.schedulingTaskForm = response.schedulingTask;
                        this.dateArr = [];
                        this.dateArr[0] = this.schedulingTaskForm.startDate;
                        this.dateArr[1] = this.schedulingTaskForm.endDate;
                        // console.log("修改数据之前的info：" + JSON.stringify(this.dateArr))
                        // console.log("当前任务第二阶段算法参数数据" + this.schedulingTaskForm.stepTwoAlgParam)
                        if (this.schedulingTaskForm.stepTwoAlgParam != null && this.schedulingTaskForm.stepTwoAlgParam != "") {
                            this.stepTwoAlgParam = JSON.parse(this.schedulingTaskForm.stepTwoAlgParam);
                        } else {
                            this.stepTwoAlgParam = JSON.parse(JSON.stringify(this.stepTwoAlgParamClone));
                        }
                        if (this.schedulingTaskForm.stepTwoAlg != null && this.schedulingTaskForm.stepTwoAlg.length > 0) {
                            this.schedulingTaskForm.stepTwoAlg = JSON.parse(this.schedulingTaskForm.stepTwoAlg);
                            this.stepTwoAlgChange();
                        }

                        this.open = true;
                        this.title = "修改任务";
                    });
                }
            )

        },

        /**
         * 获取任务的最晚结束日期
         */
        getMaxEndDateOfTask() {
            return new Promise((resolve, reject) => {
                schedulingTaskApi.getMaxEndDateOfTask().then(res => {
                    this.maxEndDate = new Date(res.maxEndDate);
                    // console.log("this.maxEndDate:"+this.maxEndDate.toLocaleDateString())
                    resolve();
                });
            })
        },

        /**
         * 查询出taskId之外的其他任务
         */
        listOtherTaskList(param) {
            return new Promise((resolve, reject) => {
                schedulingTaskApi.listOtherTaskList(param).then(res => {
                    this.otherTaskList = res.taskList;
                    resolve();
                });
            })
        },
        /**
         * 日期处理，判断哪些日期不可选择
         * @param {*} time
         */
        disabledDate(time) {

            for (let i = 0; i < this.otherTaskList.length; i++) {
                let task = this.otherTaskList[i];
                let startDate = new Date(task.startDate);
                let endDate = new Date(task.endDate);

                if (startDate.getTime() <= time.getTime() && time.getTime() <= endDate.getTime()) {
                    return true;
                }
            }

            return false;
        },
        /**
         * 获取最大结束日期的time
         */
        getMaxEndTime() {
            return this.maxEndDate.getTime();
        },

        /** 删除按钮操作 */
        handleDelete(id) {
            let ids = id ? [id] : this.getChooseIds();
            // console.log("ids:" + JSON.stringify(ids))


            let havePublishTasks = false;
            for (let i = 0; i < this.taskList.length; i++) {
                let task = this.taskList[i];
                for (let j = 0; j < ids.length; j++) {
                    if (task.id === ids[j] && task.isPublish == true) {
                        havePublishTasks = true;
                        break;
                    }
                    if (havePublishTasks) {
                        break;
                    }
                }
                if (havePublishTasks) {
                    break;
                }

            }

            if (havePublishTasks) {
                this.$message.warning("所勾选的部分任务已经发布，无法进行删除");
            } else {

                this.$confirm(`确定进行${id ? '删除' : '批量删除'}操作?`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    schedulingTaskApi.deleteBatch(ids)
                        .then(res => {
                            if (res.code === this.ResultCode.success) {
                                this.getList()
                                this.$message({
                                    message: '操作成功',
                                    type: 'success',
                                    duration: 1500,
                                })
                            } else {
                                this.$message.error(data.msg)
                            }
                        })
                })
            }

        },
        /**
         * 获取所选中的任务id
         */
        getChooseIds() {
            let ids = [];
            for (let i = 0; i < this.dataListSelections.length; i++) {
                ids.push(this.dataListSelections[i].id);
            }
            return ids;
        },
        /** 计算按钮操作 */
        handleCalculate(id) {
            // console.log("this.dataListSelections:" + JSON.stringify(this.dataListSelections))
            let ids = id ? [id] : this.getChooseIds();
            // console.log("ids:" + JSON.stringify(ids))

            let haveCalculatingTask = false;
            for (let i = 0; i < this.taskList.length; i++) {
                let task = this.taskList[i];
                for (let j = 0; j < ids.length; j++) {
                    if (task.id === ids[j] && task.status == 1) {
                        haveCalculatingTask = true;
                        break;
                    }
                    if (haveCalculatingTask) {
                        break;
                    }
                }
                if (haveCalculatingTask) {
                    break;
                }
            }

            let havePublishTasks = false;
            for (let i = 0; i < this.taskList.length; i++) {
                let task = this.taskList[i];
                for (let j = 0; j < ids.length; j++) {
                    if (task.id === ids[j] && task.isPublish == true) {
                        havePublishTasks = true;
                        break;
                    }
                    if (havePublishTasks) {
                        break;
                    }
                }
                if (havePublishTasks) {
                    break;
                }

            }

            if (haveCalculatingTask) {
                this.$message.warning("所勾选的部分任务处于计算中，无法重复提交计算");
            } else if (havePublishTasks) {
                this.$message.warning("所勾选的部分任务已经发布，无法进行计算");
            } else {
                shiftSchedulingCalculateApi.solve(ids)
                    .then(res => {
                        for (let i = 0; i < this.taskList.length; i++) {
                            let task = this.taskList[i];
                            for (let j = 0; j < ids.length; j++) {
                                if (task.id === ids[j]) {
                                    task.status = 1;
                                }
                            }
                        }

                        if (res.code === this.ResultCode.success) {
                            this.$message({
                                message: '添加计算成功',
                                type: 'success',
                                duration: 1500,
                            })
                        } else {
                            this.$message.error(data.msg)
                        }
                    })
            }

        },
        // 任务详情
        taskDetails(row) {
            this.$router.push({ path: '../result/result.vue' })
        },
        // 多选框选中数据
        handleSelectionChange(val) {
            this.dataListSelections = val;
        },

        /** 提交按钮 */
        submitForm() {
            // console.log('提交')
            this.$refs["schedulingTaskForm"].validate(valid => {
                if (valid) {
                    this.saveOrUpdate(1)
                }
            });
        },
        /**
         * 添加或修改数据
         * @param {修改类型 1:修改任务信息 2:修改工作日数据} updateType
         */
        saveOrUpdate(updateType) {
            this.schedulingTaskForm.stepTwoAlgParam = JSON.stringify(this.stepTwoAlgParam);
            if (this.dateArr.length > 0) {
                this.schedulingTaskForm.startDate = this.dateArr[0]
                this.schedulingTaskForm.endDate = this.dateArr[1]
            }
            if (this.schedulingTaskForm.stepTwoAlg != null && this.schedulingTaskForm.stepTwoAlg.length > 0) {
                this.schedulingTaskForm.stepTwoAlg = JSON.stringify(this.schedulingTaskForm.stepTwoAlg);
            }
            if (this.schedulingTaskForm.id != undefined) {
                this.updateLoading = true;
                schedulingTaskApi.update(this.schedulingTaskForm, updateType).then(response => {
                    this.updateLoading = false;
                    this.$message.success("修改任务成功");
                    this.open = false;
                    this.getList();
                });
            } else {
                schedulingTaskApi.save(this.schedulingTaskForm).then(response => {
                    this.updateLoading = false;
                    this.$message.success("新增任务成功");
                    this.open = false;
                    this.getList();
                });
            }
        },
        // 取消按钮
        cancel() {
            this.open = false;
        },

        ////日期选择
        dateChange() {
            console.log("日期改变：" + JSON.stringify(this.dateArr))
        },
        //日期格式化
        formatDate(row, column, cellValue, index) {
            let str = new Date(cellValue).toLocaleDateString()
            return str;
        },

        ////工作日设置
        //全选工作日
        allWorkDaySelect() {
            console.log("全选工作日")
            let keys = Object.keys(this.dateVoMap).sort();
            for (let key in keys) {
                this.dateVoMap[keys[key]].isNeedWork = !this.isSelectAllWorkDay;
                console.log("工作日：" + JSON.stringify(this.dateVoMap[keys[key]]))
            }
            this.isSelectAllWorkDay = !this.isSelectAllWorkDay;
            //强制日历组件刷新
            this.calendarKey = new Date().getTime();
            if (this.isSelectAllWorkDay === true) {
                //--if--全选的时候肯定是包含了周末和节日的
                this.isExcludeWeekend = true;
                this.isExcludeFestival = true;
            } else {
                this.isExcludeWeekend = false;
            }
        },
        //排除周末
        excludeWeekend() {
            let keys = Object.keys(this.dateVoMap).sort();
            for (let key in keys) {
                let date = new Date(this.dateVoMap[keys[key]].date);
                //0代表星期天
                if (date.getDay() === 6 || date.getDay() === 0) {
                    this.dateVoMap[keys[key]].isNeedWork = !this.isExcludeWeekend;
                }
            }
            this.isExcludeWeekend = !this.isExcludeWeekend;
            //强制日历组件刷新
            this.calendarKey = new Date().getTime();
        },
        //排除节日
        excludeFestival() {
            let keys = Object.keys(this.dateVoMap).sort();
            for (let key in keys) {
                let date = new Date(this.dateVoMap[keys[key]].date);
                if (this.judgeWhetherOneDayIsFestival(date) != "") {
                    this.dateVoMap[keys[key]].isNeedWork = !this.isExcludeFestival;
                }
            }
            this.isExcludeFestival = !this.isExcludeFestival;
            //强制日历组件刷新
            this.calendarKey = new Date().getTime();
        },
        //工作日设置
        workDaySet(task) {
            schedulingTaskApi.info(task.id).then(response => {
                this.schedulingTaskForm = response.schedulingTask;
                // console.log("工作日设置,response.schedulingTask：" + JSON.stringify(response.schedulingTask))
                if (this.schedulingTaskForm.stepTwoAlgParam != null && this.schedulingTaskForm.stepTwoAlgParam != "") {
                    this.stepTwoAlgParam = JSON.parse(this.schedulingTaskForm.stepTwoAlgParam);
                } else {
                    this.stepTwoAlgParam = JSON.parse(JSON.stringify(this.stepTwoAlgParamClone));
                }
                if (this.schedulingTaskForm.datevolist != null && this.schedulingTaskForm.datevolist != "") {
                    this.dateVoMap = new Map();
                    this.datevolist = JSON.parse(this.schedulingTaskForm.datevolist);
                    for (let i = 0; i < this.datevolist.length; i++) {
                        let datevo = this.datevolist[i];
                        // console.log("datevo:" + JSON.stringify(datevo))
                        this.dateVoMap[datevo.date] = datevo;
                        if (datevo.isNeedWork == true) {
                            //--if--只要有一天是选中为工作日，显示的就是“全不选”
                            this.isSelectAllWorkDay = true;
                        }
                    }

                    // let keys = Object.keys(this.dateVoMap).sort();
                    // for (let key in keys) {
                    //     console.log("this.dateVoMap[keys[key]]:" + JSON.stringify(this.dateVoMap[keys[key]]))
                    // }
                } else {
                    this.dateVoMap = new Map();
                }

                this.workDaySetDialogVisible = true;
                this.curTaskId = task.id;
                this.curTask = task;
                ///由于日历表自定义日期的起始日期只能是星期一，结束日期必须是周日，因此需要计算周一和周日所对应的日期
                this.dateRange = shiftSchedulingUtil.getDateRange(new Date(task.startDat), new Date(task.endDate));
                // 让工作日选择框选择的是这个月份的日期
                this.todayDate = new Date(task.startDate);
                // console.log("起始日期：" + this.getFormatDateStr(startDate))
                // console.log("结束日期：" + this.getFormatDateStr(endDate))
                ///填充日期字典
                if (Object.keys(this.dateVoMap).length == 0) {
                    // console.log("字典为空")
                    //--if--字典长度为0才进行设置
                    let timeOfOneDay = 3600 * 1000 * 24;
                    for (let i = 0; i <= (new Date(task.endDate).getTime() - new Date(task.startDate).getTime()) / timeOfOneDay; i++) {
                        let newDate = new Date();
                        newDate.setTime(i * timeOfOneDay + new Date(task.startDate).getTime());
                        let dateStr = newDate.toLocaleDateString()
                        // console.log("dateStr:" + dateStr)
                        this.dateVoMap[dateStr] = {
                            date: dateStr,
                            isNeedWork: false,
                            passengerFlowVoList: []
                        }
                    }
                }

            });

        },
        //日期选择
        dateSelect(date, data) {
            //date:"2023-04-16T00:00:00.000Z"
            // console.log("date:" + JSON.stringify(date))
            //data:{"isSelected":false,"type":"current-month","day":"2023-04-16"}
            // console.log("data:" + JSON.stringify(data))

            // console.log("日期选择");
            // let keys = Object.keys(this.dateVoMap).sort();
            // for (let key in keys) {
            //     console.log("this.dateVoMap[keys[key]]:" + JSON.stringify(this.dateVoMap[keys[key]]))
            // }

            let realDate = new Date(date);
            let dateStr = realDate.toLocaleDateString()
            let isNeedWork = this.dateVoMap[dateStr].isNeedWork;
            this.dateVoMap[dateStr].isNeedWork = !isNeedWork;
            //强制日历组件刷新
            this.calendarKey = new Date().getTime();
        },
        //保存工作日设置
        saveWorkDay() {
            let keys = Object.keys(this.dateVoMap).sort();
            this.datevolist = [];
            for (let key in keys) {
                this.datevolist.push(this.dateVoMap[keys[key]])
            }
            this.schedulingTaskForm.datevolist = JSON.stringify(this.datevolist);
            this.schedulingTaskForm.id = this.curTaskId;
            if (this.schedulingTaskForm.stepTwoAlg != null && this.schedulingTaskForm.stepTwoAlg.length > 0) {
                this.schedulingTaskForm.stepTwoAlg = JSON.parse(this.schedulingTaskForm.stepTwoAlg);
                this.stepTwoAlgChange();
            }
            this.saveOrUpdate(2);
            this.workDaySetDialogVisible = false;
        },
        /**
         * 将日期格式化为 2020-02-01
         * @param {日期格式 new Date()} date
         */
        getFormatDateStr(date) {
            return date.getFullYear() + "-" + ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + (date.getDate())).slice(-2)
        },
        /**
         * 查询一个门店的所有节假日
         */
        listAllHolidaysOfOneStore() {
            festivalApi.listAllHolidaysOfOneStore().then(
                res => {
                    this.festivalList = res.festivalList;
                }
            )
        },
        /**
         * 判断一天是否是假日 是：返回节日名称 否：返回""
         */
        judgeWhetherOneDayIsFestival(date) {
            console.log("日期：" + date.toLocaleDateString())
            for (let i = 0; i < this.festivalList.length; i++) {

                // console.log("date:" + date.toLocaleDateString());

                let festival = this.festivalList[i];
                let festivalStart = new Date(festival.startDate);
                let festivalEnd = new Date(festival.endDate);


                if (festivalStart.getTime() <= date.getTime() &&
                    date.getTime() <= festivalEnd.getTime() + 24 * 60 * 60 * 1000) {
                    // console.log("festivalStart:" + festivalStart.toLocaleDateString());
                    // console.log("festivalEnd:" + festivalEnd.toLocaleDateString());
                    // console.log(date.toLocaleDateString() + "->" + festival.name)
                    return festival.name;
                }
            }
            return "";
        },


        ////客流量数据导入
        handleSuccess(res) {
            this.fileName = res.fileName;
            let unIncludeDateList = res.unIncludeDateList;
            if (unIncludeDateList.length > 0) {
                let str = "";
                for (let i = 0; i < unIncludeDateList.length; i++) {
                    str += unIncludeDateList[i] + "\n";
                }
                this.$notify({
                    title: this.fileName + ' 导入提示',
                    dangerouslyUseHTMLString: true,
                    message: "以下客流量所在日期不包含在排班日期之内\n" + str + "<p style='color:red'>请注意查看导入的客流量日期和任务起止日期是否可以对应</p>",
                    duration: 0
                });
            }
        },
        handleRemove(file, fileList) {
            console.log(file, fileList);
        },
        handlePreview(file) {
            console.log(file);
        },
        handleExceed(files, fileList) {
            this.$message.warning(
                `当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length
                } 个文件`
            );
        },
        beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${file.name}？`);
        },
        //清除已经上传的文件
        clearFileList() {
            this.fileList = [];
        },
        //退出对话框
        exitExcelImport() {
            this.passengerFlowImportDialogVisible = false;
            this.clearFileList();
            this.fileName = "";
        },
        //打开excel导入对话框
        passengerFlowImport(task) {
            this.passengerFlowImportDialogVisible = true;
            this.uploadPostData.taskId = task.id;
        },

        ////修改排班规则
        editSchedulingRule(task) {
            this.$router.push('/enterprise/store/ruleSet?ruleId=' + task.schedulingRuleId + "&taskId=" + task.id);
        },

        ////算法选择
        /**
         * 第二阶段算法选择发生改变
         */
        stepTwoAlgChange() {
            // console.log("schedulingTaskForm.stepTwoAlg:" + JSON.stringify(this.schedulingTaskForm.stepTwoAlg));
            if (this.schedulingTaskForm.stepTwoAlg[1] === "SA") {
                this.algoType = "SA";
            } else if (this.schedulingTaskForm.stepTwoAlg[1] === "TS") {
                this.algoType = "TS";
            } else if (this.schedulingTaskForm.stepTwoAlg[1] === "VNS") {
                this.algoType = "VNS";
            } else if (this.schedulingTaskForm.stepTwoAlg[1] === "ALNS") {
                this.algoType = "ALNS";
            } else if (this.schedulingTaskForm.stepTwoAlg[1] === "AGA") {
                this.algoType = "AGA";
            } else if (this.schedulingTaskForm.stepTwoAlg[1] === "ILS") {
                this.algoType = "ILS";
            } else {
                this.algoType = undefined;
            }
        },
        /**
         * 算法参数设置
         */
        algoParamSet() {
            if (this.algoType === "SA") {
                this.saInnerVisible = true;
            } else if (this.algoType === "TS") {
                this.tsInnerVisible = true;
            } else if (this.algoType === "VNS") {
                this.vnsInnerVisible = true;
            } else if (this.algoType === "ALNS") {
                this.alnsInnerVisible = true;
            } else if (this.algoType === "AGA") {
                this.agaInnerVisible = true;
            } else if (this.algoType === "ILS") {
                this.ilsInnerVisible = true;
            }
        },

        ////WebSocket及时修改任务的计算状态
        /**
       *
       * @param {任务id} taskId
       * @param {是否计算完成} isSuccess
       */
        editTaskStatus(taskId, isSuccess, cause) {
            // console.log("修改任务状态，taskId："+taskId+",isSuccess:"+isSuccess)
            for (let i = 0; i < this.taskList.length; i++) {
                let task = this.taskList[i];
                if (task.id === taskId) {
                    // console.log("task:"+JSON.stringify(task))
                    if (isSuccess === 1) {
                        this.$message.success("任务 " + taskId + " 计算完成");
                        this.$notify({
                            title: '成功',
                            message: "任务 " + taskId + " 计算完成",
                            type: 'success'
                        });
                        task.status = 2;
                    } else {
                        this.$notify.error({
                            title: '错误',
                            message: "任务 " + taskId + " 计算失败，失败原因：" + cause
                        });
                        task.status = 3;
                    }
                }
            }
        },
        /**
         * 初始化webSocket
         */
        initWebSocket() {
            let _this = this;
            if (typeof (WebSocket) == "undefined") {
                console.log("您的浏览器不支持WebSocket协议");
            } else {
                console.log("您的浏览器支持WebSocket协议");
                let socketUrl = "ws://" + IP_AND_PORT + "/shift-scheduling-calculate-service/imserver/" + getToken();
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
                // 开启一个websocket服务
                socket = new WebSocket(socketUrl);
                //打开日志事件
                socket.onopen = function () {
                    console.log("websocket已打开");
                };
                //浏览器端收消息，获得从服务端发送过来的文本消息
                socket.onmessage = function (msg) {
                    // console.log("收到数据====" + msg.data);
                    // msg.data={"taskId":15,"isSuccess":1}
                    let mesage = JSON.parse(msg.data);
                    if (mesage.type == 0) {
                        _this.editTaskStatus(mesage.taskId, mesage.isSuccess, mesage.cause);
                    } else if (mesage.type == 1) {
                        console.log("多算法计算完成");
                        _this.$notify({
                            title: '成功',
                            message: "任务 " + mesage.taskId + " 多算法计算完成",
                            type: 'success',
                            duration: 0
                        });
                    }

                };
                //关闭事件
                socket.onclose = function () {
                    console.log("websocket已关闭");
                };
                //发生了错误事件
                socket.onerror = function () {
                    console.log("websocket发生了错误");
                }
            }
        },
        /**
         * webSocket发送消息
         */
        webSocketSend(message) {
            socket.send(JSON.stringify(message));
        },

        ////算法组合勾选计算
        ///算法勾选
        /**
        * 初始化算法组合选项
        */
        initAlgoGroupOptions(taskId) {
            shiftSchedulingCalculateApi.getAllAlgorithmGroup(taskId).then(
                res => {
                    this.algoGroupOptions = res.algorithmGroupStrList;
                    this.checkedAlgoGroups = res.haveCalculateAlgoGroups;
                }
            )
        },
        /**
         * 打开算法组合勾选对话框
         */
        openAlgoGroupSelectVisible(taskId) {
            this.initAlgoGroupOptions(taskId);
            this.schedulingTaskForm.id = taskId;
            this.algoGroupSelectVisible = true;
        },
        handleAlgoGroupCheckAllChange(val) {
            this.checkedAlgoGroups = val ? this.algoGroupOptions : [];
            this.algoGroupIsIndeterminate = false;
        },
        handleCheckedAlgoGroupsChange(value) {
            let checkedCount = value.length;
            this.algoGroupCheckAll = checkedCount === this.algoGroupOptions.length;
            this.algoGroupIsIndeterminate = checkedCount > 0 && checkedCount < this.algoGroupOptions.length;
        },
        /**
         * 删除任务的所有结果
         */
        deleteTaskResult(taskId) {
            schedulingTaskApi.deleteAllResultOfTask(taskId).then(
                res => {
                    this.$message.success("删除结果成功，可以重新计算");
                    this.getList();
                }
            )
        },
        /**
         * 执行多任务计算
         */
        multiAlgorithmSolve() {
            this.algoGroupSelectVisible = false;
            this.listVirtualTask(0);
        },
        /**
         * 发送请求执行多任务计算
         * @param {0：添加计算、刷新表格 1：排序 2:查询} type
         */
        listVirtualTask(type) {
            let params = {
                page: this.allAlgoPageNum + "",
                limit: this.allAlgoPageSize + "",
                taskId: this.schedulingTaskForm.id,
                orderType: this.orderType,
                checkedAlgoGroups: this.checkedAlgoGroups
            };
            // console.log("params："+JSON.stringify(params));
            shiftSchedulingCalculateApi.multiAlgorithmSolve(params).then(res => {
                console.log("多算法添加计算成功")
                ///展示提示消息
                if (type == 0) {
                    this.$message.success(res.message);
                } else if (type == 1) {
                    this.$message.success("排序完成");
                } else if (type == 2) {
                    this.$message.success("查询成功");
                }

            })
        },
        ///多算法结果查看
        /**
         * 获取所有算法组合
         */
        listMultiAlgorithmResult(type, taskId) {
            let params = {
                page: this.allAlgoPageNum + "",
                limit: this.allAlgoPageSize + "",
                taskId: taskId,
                orderType: this.orderType
            };
            shiftSchedulingCalculateApi.listMultiAlgorithmResult(params).then(res => {
                // console.log("res:" + JSON.stringify(res));
                ///展示算法组合的计算结果
                this.allAlgoTableData = res.page.list;
                for (let i = 0; i < this.allAlgoTableData.length; i++) {
                    this.allAlgoTableData[i].stepTwoAlg = JSON.parse(this.allAlgoTableData[i].stepTwoAlg);
                }
                this.allAlgoTotal = res.page.totalCount;

                ///展示提示消息
                if (type == 0) {
                    this.$message.success("排序完成");
                } else if (type == 1) {
                    this.$message.success("查询成功");
                } else if (type == 2) {
                    this.$message.success("刷新成功");
                }

            })
        },
        /**
         * 查看算法计算结果
         */
        openAlgoGroupResultVisible(taskId) {
            this.allAlgoInnerVisible = true;
            this.schedulingTaskForm.id = taskId;
            this.algoGroupResultVisibleTaskId = taskId;
            this.listMultiAlgorithmResult(1, taskId);
        },
        /**
         * 用虚拟任务的结果覆盖任务结果
         * @param {虚拟任务id} virtualTaskId
         */
        overlayResult(virtualTaskId) {
            this.$confirm('该操作将会替换原本的结果, 请问是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                shiftSchedulingCalculateApi.overlayResult(virtualTaskId).then(
                    res => {
                        this.$message.success("覆盖结果完成");
                    }
                )
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消覆盖'
                });
            });
        },
        /**
         * 查看任务的计算结果
         * @param {任务id} taskId
         */
        viewResultOftask(taskId) {
            this.viewResultTaskId = taskId;
            this.taskResultVisible = true;
            this.resultViewKey = new Date().getTime();
        },

        ////任务发布
        publistStatusChange(taskId, isPublish) {
            // console.log("taskId:" + taskId);
            // console.log("isPublish:" + isPublish);

            let tip;
            if (isPublish == false) {
                tip = "取消发布任务，将撤回任务排班起止时间内的排班安排,并发送消息通知员工，请问是否继续?";
            } else {
                tip = "发布任务，将发送消息通知员工，请问是否继续?";
            }

            this.$confirm(tip, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                schedulingTaskApi.updateTaskPublishStatus({ taskId: taskId, isPublish: isPublish == true ? 1 : 0 }).then(
                    res => {
                        if (isPublish == false) {
                            this.$message.success("任务撤销成功");
                        } else {
                            this.$message.success("任务发布成功");
                        }
                    }
                )
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消'
                });
            });

        }

    },
}
</script>

<style lang="scss" scoped>
.containerDiv {
    margin: 20px;
    height: calc(100vh - 70px - 20px - 1px);

    .colDiv {
        height: calc(100vh - 70px - 20px - 1px);
        // background: #1989FA;
    }

    .titleDiv {
        display: flex;
        align-items: center;
        padding: 5px 15px 5px 0px;
        height: 45px;
    }

    .toolDiv {
        background: #FFFFFF;
        padding: 1px;
        padding-left: 15px;
        height: 35px;
        display: flex;
        align-items: center;
    }

    .timeTreeDiv {
        background: #D3E6FB;
        border-radius: 5px;
        padding: 1px;
        height: 100%;
    }

    .insideContainer {
        background: #FFFFFF;
        height: calc(100% - 45px);
    }

    .taskDiv {
        background: #D3E6FB;
        border-radius: 5px;
        padding: 1px;
        height: 100%;

        .taskElTableDiv {
            // background: #1989FA;
            height: calc(100% - 35px - 40px);
        }
    }
}



.task-container {
    margin: 20px;
}

.head-container {
    /* margin-top: 20px; */
    margin-left: 20px;
}

.is-selected {
    color: #1989FA;
}

.fileNameClass {
    margin-bottom: 30px;
    border: 1px solid #235ada;
    padding: 5px;
    border-radius: 5px;
    width: 400px;
    text-align: left;
}

.timeTree {
    .el-card__header {
        padding: 8px 0 8px 20px;
    }
}
</style>
