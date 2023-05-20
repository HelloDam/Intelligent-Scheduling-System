<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="创建时间" prop="createTime">
        <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
      </el-form-item>
      <el-form-item label="更新时间" prop="updateTime">
        <el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
      </el-form-item>
      <el-form-item label="是否删除(0-未删, 1-已删)" prop="isDeleted">
        <el-input v-model="dataForm.isDeleted" placeholder="是否删除(0-未删, 1-已删)"></el-input>
      </el-form-item>
      <el-form-item label="" prop="username">
        <el-input v-model="dataForm.username" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="" prop="ipaddr">
        <el-input v-model="dataForm.ipaddr" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="" prop="status">
        <el-input v-model="dataForm.status" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="" prop="msg">
        <el-input v-model="dataForm.msg" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="" prop="accessTime">
        <el-input v-model="dataForm.accessTime" placeholder=""></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import loginLogApi from '@/api/system/loginLog'
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        createTime: '',
        updateTime: '',
        isDeleted: '',
        username: '',
        ipaddr: '',
        status: '',
        msg: '',
        accessTime: ''
      },
      dataRule: {
        createTime: [
          { required: true, message: '创建时间不能为空', trigger: 'blur' }
        ],
        updateTime: [
          { required: true, message: '更新时间不能为空', trigger: 'blur' }
        ],
        isDeleted: [
          { required: true, message: '是否删除(0-未删, 1-已删)不能为空', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        ipaddr: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        msg: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        accessTime: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          loginLogApi.info(this.dataForm.id).then(res => {
            if (res.code === this.ResultCode.success) {
              this.dataForm.createTime = data.loginLog.createTime
              this.dataForm.updateTime = data.loginLog.updateTime
              this.dataForm.isDeleted = data.loginLog.isDeleted
              this.dataForm.username = data.loginLog.username
              this.dataForm.ipaddr = data.loginLog.ipaddr
              this.dataForm.status = data.loginLog.status
              this.dataForm.msg = data.loginLog.msg
              this.dataForm.accessTime = data.loginLog.accessTime
            }
          })

        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {

          let data = {
            'id': this.dataForm.id || undefined,
            'createTime': this.dataForm.createTime,
            'updateTime': this.dataForm.updateTime,
            'isDeleted': this.dataForm.isDeleted,
            'username': this.dataForm.username,
            'ipaddr': this.dataForm.ipaddr,
            'status': this.dataForm.status,
            'msg': this.dataForm.msg,
            'accessTime': this.dataForm.accessTime
          };

          if (this.dataForm.id) {
            loginLogApi.updateById(data).then(res => {
              // console.log("this.ResultCode.success:" + this.ResultCode.success)
              if (res.code === this.ResultCode.success) {
                this.visible = false
                this.$emit('refreshDataList')
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            loginLogApi.save(data).then(res => {
              if (res && res.code === this.ResultCode.success) {
                this.visible = false
                this.$emit('refreshDataList')
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,

                })
              } else {
                this.$message.error(message.message)
              }
            })
          }
        }
      })
    }
  }
}
</script>
