<template>
    <div class="container">
        <el-container>
            <el-aside width="150px">
                <el-radio-group v-model="selectedType" class="el-group-list" @change="onSelected">
                    <el-radio-button v-for="dict in skillType" :key="dict.value"
                        :label="dict.value">{{ dict.label }}</el-radio-button>
                </el-radio-group>
            </el-aside>
            <el-main>
                <el-calendar v-loading="loading" v-model="date">
                    <template slot="dateCell" slot-scope="{date, data}">
                        <div style="height: 100%" :class="data.isSelected ? 'is-selected' : ''" @click="dateClick(data)">
                            <div>
                                <el-row>
                                    <el-col :span="6">
                                        <div class="solar">
                                            {{ data.day.split('-')[2] }}
                                        </div>
                                    </el-col>
                                    <!-- <el-col :span="12">
                                    <div class="lunar" :class="{ festival : isFestival(date, data) }">{{ solarDate2lunar(data.day) }}</div>
                                </el-col>
                                <el-col :span="6">
                                    <el-tag v-if="holidayList.indexOf(data.day) ==-1" effect="dark">班</el-tag>
                                    <el-tag v-else effect="dark" type="success">休</el-tag>
                                </el-col> -->
                                </el-row>
                            </div>
                        </div>
                    </template>
                </el-calendar>
            </el-main>
        </el-container>

        <el-dialog :title=dtitle :visible.sync="dialogVisible" width="90%" :before-close="handleClose">
            <div class="wrapper">
                <div class="container">
                    <GanttChar class="left-container" ref="ganttChar" :optType="ganttOptType" :tasks="tasks"></GanttChar>
                </div>
            </div>
            <!-- <GanttChar class="left-container" ref="ganttChar" :tasks="tasks"></GanttChar> -->
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import GanttChar from "../ganttx.vue";
export default {
    name: 'SkillView',
    components: { GanttChar },
    data() {
        return {
            // 遮罩层
            loading: false,
            date: new Date(),
            dialogVisible: false,
            dtitle: '',
            selectedType: null,
            ganttOptType: 'view',
            skillType: [
                {
                    value: 0,
                    label: '前端'
                },
                {
                    value: 1,
                    label: '后端'
                },
                {
                    value: 2,
                    label: '测试'
                }
            ],
            tasks: {
                data: [
                    { id: 1, text: '张三', eid: '121212', dep: '销售部', start_date: '2023-02-27 07:00:00', end_date: '2023-02-27 10:30:00', progress: 1.0 },
                    { id: 2, text: '李四', eid: '121212', dep: '会计部', start_date: '2023-02-27 07:00:00', end_date: '2023-02-27 11:00:00', progress: 0.8 }
                ],
                // links: [
                // {id: 1, source: 1, target: 2, type: '0'}
                // ]
            },
        }
    },
    created() {

    },
    methods: {
        //点击类型 根据类型刷新日历数据
        onSelected(calType) {
            this.loading = false;
            console.log(calType)
        },

        //点击日历日期格
        dateClick(data) {
            this.dtitle = data.day
            console.log(data)

            // 根据选中日期返回当日日程甘特图数据载入tasks

            this.dialogVisible = true
        },
        handleClose(done) {
            this.$confirm('确认关闭？')
                .then(_ => {
                    done();
                })
                .catch(_ => { });
        }
    }
}
</script>

<style>
.container {
    height: 100%;
}

.grid-content {
    padding: 5px 0;
}

.el-group-list.el-radio-group {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    padding: 8px 24px;
    background: #eef1f6;
}

.el-group-list.el-radio-group .el-radio-button:first-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button:last-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button:first-child .el-radio-button__inner,
.el-group-list.el-radio-group .el-radio-button__inner {
    border-radius: 0px !important;
    border: none !important;
}

.el-group-list.el-radio-group .el-radio-button {
    border-bottom: 1px solid #F7F7F7 !important;
}

.el-group-list.el-radio-group {
    border: 1px solid #dcdfe6;
}

.el-group-list.el-radio-group>label>span {
    width: 100%;
    text-align: left;
    padding-left: 20px;
}

.wrapper {
    height: 450px;
}

.container {
    height: 100%;
    width: 100%;
}

.left-container {
    overflow: hidden;
    position: relative;
    height: 100%;
}
</style>