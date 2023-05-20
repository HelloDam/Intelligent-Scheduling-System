<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="创建时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
    </el-form-item>
    <el-form-item label="更新时间" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
    </el-form-item>
    <el-form-item label="是否删除 0：未删除 1：已删除" prop="isDeleted">
      <el-input v-model="dataForm.isDeleted" placeholder="是否删除 0：未删除 1：已删除"></el-input>
    </el-form-item>
    <el-form-item label="名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="名称"></el-input>
    </el-form-item>
    <el-form-item label="类型 0：省 1：市 2：区" prop="type">
      <el-input v-model="dataForm.type" placeholder="类型 0：省 1：市 2：区"></el-input>
    </el-form-item>
    <el-form-item label="没有父元素设置为-1" prop="parentId">
      <el-input v-model="dataForm.parentId" placeholder="没有父元素设置为-1"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          createTime: '',
          updateTime: '',
          isDeleted: '',
          name: '',
          type: '',
          parentId: ''
        },
        dataRule: {
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '更新时间不能为空', trigger: 'blur' }
          ],
          isDeleted: [
            { required: true, message: '是否删除 0：未删除 1：已删除不能为空', trigger: 'blur' }
          ],
          name: [
            { required: true, message: '名称不能为空', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '类型 0：省 1：市 2：区不能为空', trigger: 'blur' }
          ],
          parentId: [
            { required: true, message: '没有父元素设置为-1不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/dam/provincecityregion/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.createTime = data.provinceCityRegion.createTime
                this.dataForm.updateTime = data.provinceCityRegion.updateTime
                this.dataForm.isDeleted = data.provinceCityRegion.isDeleted
                this.dataForm.name = data.provinceCityRegion.name
                this.dataForm.type = data.provinceCityRegion.type
                this.dataForm.parentId = data.provinceCityRegion.parentId
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/dam/provincecityregion/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'isDeleted': this.dataForm.isDeleted,
                'name': this.dataForm.name,
                'type': this.dataForm.type,
                'parentId': this.dataForm.parentId
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
