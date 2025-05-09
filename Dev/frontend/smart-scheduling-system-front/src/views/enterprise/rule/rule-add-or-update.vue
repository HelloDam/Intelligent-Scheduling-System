<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
      <el-form-item label="规则名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="规则名称"></el-input>
      </el-form-item>
      <el-form-item label="值样式" prop="valueStyle">
        <el-input v-model="dataForm.valueStyle" placeholder="值样式"></el-input>
      </el-form-item>
      <el-form-item label="样式说明" prop="detail">
        <el-input v-model="dataForm.detail" type="textarea" autosize placeholder="样式说明"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="type">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label=0>启用</el-radio>
          <el-radio :label=1>停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import ruleApi from '@/api/enterprise/rule'
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        createTime: '',
        updateTime: '',
        isDeleted: '',
        name: '',
        valueStyle: '',
        detail: '',
        status: 0
      },
      dataRule: {
        name: [
          { required: true, message: '规则名称不能为空', trigger: 'blur' }
        ],
        valueStyle: [
          { required: true, message: '规则说明不能为空', trigger: 'blur' }
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
          ruleApi.info(this.dataForm.id)
            .then(res => {
              if (res.code === this.ResultCode.success) {
                this.dataForm.createTime = res.rule.createTime
                this.dataForm.updateTime = res.rule.updateTime
                this.dataForm.isDeleted = res.rule.isDeleted
                this.dataForm.name = res.rule.name
                this.dataForm.valueStyle = res.rule.valueStyle
                this.dataForm.detail = res.rule.detail
                this.dataForm.status = res.rule.status
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
            'name': this.dataForm.name,
            'valueStyle': this.dataForm.valueStyle,
            'detail': this.dataForm.detail,
            'status': this.dataForm.status
          };

          if (this.dataForm.id) {
            ruleApi.updateById(data).then(res => {
              // console.log("this.ResultCode.success:" + this.ResultCode.success)
              if (res.code === this.ResultCode.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
                this.visible = false
                this.$emit('refreshDataList')
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            ruleApi.save(data).then(res => {
              if (res && res.code === this.ResultCode.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
                this.visible = false
                this.$emit('refreshDataList')
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
