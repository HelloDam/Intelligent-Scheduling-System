<template>
    <div v-if="registerVoList.length > 0">
        <div class="notification" @click="viewEnterpriseRegisterMessage()">
            <div class="icon">
                <i class="iconfont icon-xiaoxi" style="color: black;"></i>
                <span class="badge">{{ registerVoList.length }}</span>
            </div>
            <div class="message">您有{{ registerVoList.length }}条未读消息。</div>
        </div>
        <el-dialog title="企业注册信息处理" :visible.sync="enterpriseRegisterDialogVisible" width="60%">
            <el-table :data="registerVoList" style="width: 100%">
                <el-table-column prop="logo" label="logo" width="100">
                    <template slot-scope="scope">
                        <el-image style="height: 50px;border-radius: 10px;" :src="scope.row.logo" fit="contain"></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="企业名称">
                </el-table-column>
                <el-table-column prop="detail" label="企业详情">
                </el-table-column>
                <el-table-column prop="mail" label="申请人邮箱">
                </el-table-column>
                <el-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                        <el-button @click="passEnterpriseRegister(scope.row)" type="text" size="small">通过</el-button>
                        <el-button id="button-danger" @click="openRegisterInneVisibale(scope.row)" type="text"
                            size="small">拒绝</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-dialog width="40%" title="请输入拒绝原因" :visible.sync="enterpriseRegisterInnerVisible" append-to-body>
                <el-input type="textarea" :autosize="{ minRows: 5, maxRows: 8 }" placeholder="请输入拒绝原因" v-model="rejectEnterpriseRegisterVo.rejectReason">
                </el-input>
                <span slot="footer" class="dialog-footer">
                <el-button @click="enterpriseRegisterInnerVisible = false">取 消</el-button>
                <el-button type="primary" @click="rejectEnterpriseRegister()">确 定</el-button>
            </span>
            </el-dialog>
            <span slot="footer" class="dialog-footer">
                <el-button @click="enterpriseRegisterInnerVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import enterpriseApi from '@/api/enterprise/enterprise';
export default {
    name: 'SmartSchedulingSystemSystemMessage',

    data() {
        return {
            ////企业注册
            enterpriseRegisterDialogVisible: false,
            enterpriseRegisterInnerVisible: false,
            registerVoList: [],
            rejectEnterpriseRegisterVo: {}
        };
    },

    mounted() {

    },

    created() {
        this.listAllEnterpriseRegisterVoFromRedis();
    },

    methods: {
        viewEnterpriseRegisterMessage() {
            // console.log("哈哈哈哈")
            this.enterpriseRegisterDialogVisible = true;
        },
        /**
         * 获取申请注册的企业信息
         */
        listAllEnterpriseRegisterVoFromRedis() {
            enterpriseApi.listAllEnterpriseRegisterVoFromRedis().then(
                res => {
                    this.registerVoList = res.registerVoList;
                    // console.log("this.registerVoList:" + JSON.stringify(this.registerVoList))
                }
            )
        },
        /**
         * 通过企业注册
         */
        passEnterpriseRegister(enterpriseRegisterVo) {
            enterpriseApi.passEnterpriseRegister(enterpriseRegisterVo).then(
                res => {
                    this.$message.success("通过企业注册成功");
                    this.listAllEnterpriseRegisterVoFromRedis();
                }
            )
        },
        openRegisterInneVisibale(enterpriseRegisterVo) {
            this.rejectEnterpriseRegisterVo = enterpriseRegisterVo;
            this.enterpriseRegisterInnerVisible = true;
        },
        /**
         * 拒绝企业注册
         */
        rejectEnterpriseRegister() {
          console.log("拒绝企业注册成功")
          enterpriseApi.rejectEnterpriseRegister(this.rejectEnterpriseRegisterVo).then(
                res => {
                    this.$message.success("拒绝企业注册成功");
                    this.enterpriseRegisterInnerVisible = false;
                    this.listAllEnterpriseRegisterVoFromRedis();
                }
            )
        }
    },
};
</script>

<style lang="scss" scoped>
.item {
    // margin-top: 0px;
    // margin-right: 10px;
}

.notification {
    display: flex;
    align-items: center;
    background-color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
    height: 40px;
    padding: 0px 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    /* Add the following style to create a click effect */
    transition: all 0.2s ease-in-out;
    cursor: pointer;
}

.notification:hover {
    background-color: #f5f5f5;
}

.notification:active {
    transform: scale(0.95);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
    background-color: #e6e6e6;
}


.notification .icon {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 20px;
    width: 20px;
    //   background-color: #ccc;
    border-radius: 50%;
    margin-right: 10px;
    position: relative;
}

.notification .icon i {
    color: #fff;
    font-size: 20px;
}

.notification .badge {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f00;
    color: #fff;
    font-size: 12px;
    height: 15px;
    width: 15px;
    border-radius: 50%;
    position: absolute;
    top: -5px;
    right: -5px;
}

.notification .message {
    font-size: 14px;
    color: #666;
}

.el-table {
    border: 1px solid #ebeef5;
    font-size: 14px;
}

.el-table th,
.el-table td {
    padding: 12px;
    text-align: center;
}

.el-table th {
    background-color: #f5f7fa;
    color: #909399;
}

.el-table td {
    color: #606266;
}

.el-image__inner {
    border-radius: 50%;
}
</style>