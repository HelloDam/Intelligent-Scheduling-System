<template>
    <div class="operationLogContainer">
        <div class="contentDiv">
            <div class="titleDiv">
                <span style="margin-right: 20px; margin-left: 15px; font-weight: bold">操作日志</span>
                <el-input v-model="dataForm.key" placeholder="请输入日志名称" clearable style="width: 150px; margin-right: 10px"
                    @keyup.enter.native="getDataList()" size="small" />
                <el-button type="primary" icon="el-icon-search" size="mini" @click="getDataList()">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery()">重置</el-button>
            </div>

            <div class="toolDiv">
                <el-button id="button-danger" type="text" icon="el-icon-delete" size="small"
                    :disabled="dataListSelections.length <= 0" @click="deleteHandle()">批量删除</el-button>
            </div>

            <div class="tableDisplayDiv">
                <div class="tableDiv">
                    <el-table :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandle"
                        style="width: 100%" height="100%">
                        <el-table-column type="selection" header-align="left" align="left" width="50">
                        </el-table-column>
                        <el-table-column prop="enterpriseName" header-align="left" align="left" label="企业名称" width="100"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="storeName" header-align="left" align="left" label="门店名称" width="100"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="operName" header-align="left" align="left" label="操作人员">
                        </el-table-column>
                        <el-table-column prop="status" header-align="left" align="left" label="操作状态">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.status == 0" type="success">成功</el-tag>
                                <el-tag v-if="scope.row.status == 1" type="danger">失败</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="operLocation" header-align="left" align="left" label="操作地点"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="operIp" header-align="left" align="left" label="主机地址" width="150">
                        </el-table-column>
                        <el-table-column prop="operatorType" header-align="left" align="left" label="操作类别">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.operatorType == 0" type="warning">其他</el-tag>
                                <el-tag v-if="scope.row.operatorType == 1" type="success">后台用户</el-tag>
                                <el-tag v-if="scope.row.operatorType == 2">手机端用户</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="title" header-align="left" align="left" label="模块标题"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="detail" header-align="left" align="left" label="说明" width="200"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="businessType" header-align="left" align="left" label="业务类型" width="100">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.businessType == 0" type="warning">其他</el-tag>
                                <el-tag v-if="scope.row.businessType == 1" type="success">新增</el-tag>
                                <el-tag v-if="scope.row.businessType == 2">修改</el-tag>
                                <el-tag v-if="scope.row.businessType == 3" type="danger">删除</el-tag>
                                <el-tag v-if="scope.row.businessType == 4">授权</el-tag>
                                <el-tag v-if="scope.row.businessType == 5">导出</el-tag>
                                <el-tag v-if="scope.row.businessType == 6">导入</el-tag>
                                <el-tag v-if="scope.row.businessType == 7" type="danger">强退</el-tag>
                                <el-tag v-if="scope.row.businessType == 8">更新状态</el-tag>
                                <el-tag v-if="scope.row.businessType == 9" type="danger">清空数据</el-tag>
                                <el-tag v-if="scope.row.businessType == 10">通过</el-tag>
                                <el-tag v-if="scope.row.businessType == 11" type="danger">拒绝</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="requestMethod" header-align="left" align="left" label="请求方式">
                        </el-table-column>
                        <el-table-column prop="method" header-align="left" align="left" label="方法名称" width="100">
                            <template slot-scope="scope">
                                <el-popover placement="bottom" title="方法名称" trigger="click" :content="scope.row.method">
                                    <span slot="reference">{{ scope.row.method.length > 5 ? scope.row.method.substr(0, 5) +
                                        '...' :
                                        scope.row.method }}</span>
                                </el-popover>
                            </template>
                        </el-table-column>
                        <!-- <el-table-column prop="operParam" header-align="center" align="center" :show-overflow-tooltip="true"
                        :ellipsis="true"> label="请求参数">
                    </el-table-column> -->
                        <el-table-column prop="operParam" label="请求参数" width="100">
                            <template slot-scope="scope">
                                <el-popover placement="bottom" title="方法名称" trigger="click" :content="scope.row.operParam">
                                    <span slot="reference" v-if="scope.row.operParam != null">{{ scope.row.operParam.length
                                        > 5
                                        ?
                                        scope.row.operParam.substr(0, 5) + '...' :
                                        scope.row.operParam }}</span>
                                </el-popover>
                            </template>
                        </el-table-column>
                        <el-table-column prop="jsonResult" header-align="left" align="left" label="返回参数" width="100">
                            <template slot-scope="scope">
                                <el-popover placement="bottom" title="方法名称" trigger="click" :content="scope.row.jsonResult">
                                    <span slot="reference" v-if="scope.row.jsonResult != null">{{
                                        scope.row.jsonResult.length >
                                        5 ?
                                        scope.row.jsonResult.substr(0, 5) + '...' :
                                        scope.row.jsonResult }}</span>
                                </el-popover>
                            </template>
                        </el-table-column>
                        <el-table-column prop="errorMsg" header-align="left" align="left" label="错误信息">
                            <template slot-scope="scope">
                                <el-popover placement="bottom" title="错误信息" trigger="click" :content="scope.row.errorMsg">
                                    <span slot="reference" v-if="scope.row.errorMsg != null">{{ scope.row.errorMsg.length >
                                        5 ?
                                        scope.row.errorMsg.substr(0, 5) + '...' :
                                        scope.row.errorMsg }}</span>
                                </el-popover>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createTime" header-align="left" align="left" label="创建时间" width="200">
                        </el-table-column>
                        <el-table-column prop="updateTime" header-align="left" align="left" label="更新时间" width="200">
                        </el-table-column>
                        <el-table-column fixed="right" header-align="left" align="left" width="150" label="操作">
                            <template slot-scope="scope">
                                <el-button type="text" size="small" id="button-danger"
                                    @click="deleteHandle(scope.row.id)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
                <pagination v-show="totalPage > 0" :total="totalPage" :page.sync="pageIndex" :limit.sync="pageSize"
                    @pagination="getDataList" />
            </div>

        </div>
    </div>
</template>

<script>
import operationApi from "@/api/system/operationLog";
import { title } from "process";
export default {
    name: "SmartSchedulingSystemList",

    data() {
        return {
            dataForm: {
                key: "",
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalPage: 0,
            //用于接收批量操作中的数据
            dataListSelections: [],
            //表格本身具有一个数据加载表示，通过v-loading绑定
            dataListLoading: false,
        };
    },

    created() {
        this.getDataList();
    },

    methods: {
        handleExpand(row) {
            row.isOverflow = false;
            this.$nextTick(() => {
                row.isOverflow = true;
            });
        },

        //获取数据列表
        getDataList() {
            // this.dataListLoading = true
            let params = {
                page: this.pageIndex,
                limit: this.pageSize,
                // 'key': this.dataForm.key
            };
            operationApi.list(params).then((res) => {
                //请求成功
                //res接口返回的数据
                console.log(JSON.stringify(res));
                if (res.code === this.ResultCode.success) {
                    this.dataList = res.page.list;
                    this.totalPage = res.page.totalCount;
                } else {
                    this.dataList = [];
                    this.totalPage = 0;
                }
                this.dataListLoading = false;
            });
        },
        // 多选
        selectionChangeHandle(val) {
            this.dataListSelections = val
        },
        //重置
        resetQuery() {
            this.dataForm = [];
        },
        // 删除
        deleteHandle(id) {
            var ids = id ? [id] : this.dataListSelections.map(item => {
                return item.id
            })
            this.$confirm(`确定进行 ${id ? '删除' : '批量删除'} 操作?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                operationApi.deleteBatch(ids)
                    .then(res => {
                        if (res.code === this.ResultCode.success) {
                            this.getDataList()
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
};
</script>

<style lang="scss" scoped>
.operationLogContainer {
    padding: 20px 20px 0px 20px;
    // background: rgb(243, 5, 183);
    height: 100%;

    .contentDiv {
        background: #D3E6FB;
        border-radius: 5px;
        padding: 1px;
        height: 100%;

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

        .tableDisplayDiv {
            background: #FFFFFF;
            // height: 300px;
            height: calc(100% - 35px - 45px);

            .tableDiv {
                background: #FFFFFF;
                // height: 300px;
                height: calc(100% - 40px);
            }
        }

    }
}

.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.expand {
    cursor: pointer;
    color: #409eff;
}
</style>
