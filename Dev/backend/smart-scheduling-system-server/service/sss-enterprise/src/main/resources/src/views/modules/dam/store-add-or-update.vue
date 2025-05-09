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
    <el-form-item label="省" prop="provinceId">
      <el-input v-model="dataForm.provinceId" placeholder="省"></el-input>
    </el-form-item>
    <el-form-item label="市" prop="cityId">
      <el-input v-model="dataForm.cityId" placeholder="市"></el-input>
    </el-form-item>
    <el-form-item label="区" prop="regionId">
      <el-input v-model="dataForm.regionId" placeholder="区"></el-input>
    </el-form-item>
    <el-form-item label="详细地址" prop="address">
      <el-input v-model="dataForm.address" placeholder="详细地址"></el-input>
    </el-form-item>
    <el-form-item label="工作场所面积" prop="size">
      <el-input v-model="dataForm.size" placeholder="工作场所面积"></el-input>
    </el-form-item>
    <el-form-item label="0：营业中 1：休息中（默认0）" prop="status">
      <el-input v-model="dataForm.status" placeholder="0：营业中 1：休息中（默认0）"></el-input>
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
          provinceId: '',
          cityId: '',
          regionId: '',
          address: '',
          size: '',
          status: ''
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
          provinceId: [
            { required: true, message: '省不能为空', trigger: 'blur' }
          ],
          cityId: [
            { required: true, message: '市不能为空', trigger: 'blur' }
          ],
          regionId: [
            { required: true, message: '区不能为空', trigger: 'blur' }
          ],
          address: [
            { required: true, message: '详细地址不能为空', trigger: 'blur' }
          ],
          size: [
            { required: true, message: '工作场所面积不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '0：营业中 1：休息中（默认0）不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/dam/store/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.createTime = data.store.createTime
                this.dataForm.updateTime = data.store.updateTime
                this.dataForm.isDeleted = data.store.isDeleted
                this.dataForm.name = data.store.name
                this.dataForm.provinceId = data.store.provinceId
                this.dataForm.cityId = data.store.cityId
                this.dataForm.regionId = data.store.regionId
                this.dataForm.address = data.store.address
                this.dataForm.size = data.store.size
                this.dataForm.status = data.store.status
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
              url: this.$http.adornUrl(`/dam/store/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'isDeleted': this.dataForm.isDeleted,
                'name': this.dataForm.name,
                'provinceId': this.dataForm.provinceId,
                'cityId': this.dataForm.cityId,
                'regionId': this.dataForm.regionId,
                'address': this.dataForm.address,
                'size': this.dataForm.size,
                'status': this.dataForm.status
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
