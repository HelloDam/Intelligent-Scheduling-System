<template>
    <div class="loginLogContainer">
        <div class="contentDiv">
            <div class="titleDiv">
                <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">登录日志</span>
                <el-input v-model="dataForm.key" placeholder="请输入任务名称" clearable style="width: 150px;margin-right: 10px;"
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
                        style="width: 100%;" height="100%">
                        <el-table-column type="selection" header-align="center" align="center" width="50">
                        </el-table-column>
                        <el-table-column prop="enterpriseName" header-align="left" align="left" label="企业名称" width="100"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="storeName" header-align="left" align="left" label="门店名称" width="100"
                            :show-overflow-tooltip="true">
                        </el-table-column>
                        <el-table-column prop="username" header-align="center" align="center" label="用户名">
                        </el-table-column>
                        <el-table-column prop="ipaddr" header-align="center" align="center" label="ip地址">
                        </el-table-column>
                        <el-table-column prop="status" header-align="center" align="center" label="状态">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.status == 0" type="success">成功</el-tag>
                                <el-tag v-if="scope.row.status == 1" type="danger">失败</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="msg" header-align="center" align="center" label="消息">
                        </el-table-column>
                        <el-table-column prop="browser" header-align="center" align="center" label="浏览器">
                        </el-table-column>
                        <el-table-column prop="os" header-align="center" align="center" label="操作系统">
                        </el-table-column>
                        <el-table-column prop="createTime" header-align="center" align="center" label="创建时间">
                        </el-table-column>
                        <el-table-column prop="updateTime" header-align="center" align="center" label="更新时间">
                        </el-table-column>
                        <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
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




        <!-- 弹窗, 新增 / 修改 -->
        <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
</template>
  
<script>
import loginLogApi from '@/api/system/loginLog'
import AddOrUpdate from './loginlog-add-or-update'
export default {
    data() {
        return {
            dataForm: {
                key: ''
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalPage: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false
        }
    },
    components: {
        AddOrUpdate
    },
    created() {
        this.getDataList()
    },
    methods: {
        // 获取数据列表
        getDataList() {
            this.dataListLoading = true
            let params = {
                'page': this.pageIndex,
                'limit': this.pageSize,
                'key': this.dataForm.key
            }
            loginLogApi.list(params)
                .then(res => {
                    // console.log(JSON.stringify(res))
                    if (res.code === this.ResultCode.success) {
                        this.dataList = res.page.list
                        this.totalPage = res.page.totalCount
                    } else {
                        this.dataList = []
                        this.totalPage = 0
                    }
                    this.dataListLoading = false
                })
        },
        resetQuery() {
            this.dataForm = [];
        },
        // 每页数
        sizeChangeHandle(val) {
            this.pageSize = val
            this.pageIndex = 1
            this.getDataList()
        },
        // 当前页
        currentChangeHandle(val) {
            this.pageIndex = val
            this.getDataList()
        },
        // 多选
        selectionChangeHandle(val) {
            this.dataListSelections = val
        },
        // 新增 / 修改
        addOrUpdateHandle(id) {
            this.addOrUpdateVisible = true
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init(id)
            })
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
                loginLogApi.deleteBatch(ids)
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
    }
}
</script>

<style lang="scss" scoped>
.loginLogContainer {
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
</style>
  