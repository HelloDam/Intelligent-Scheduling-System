<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="详情" prop="detail">
        <el-input type="textarea" :autosize="{ minRows: 6, maxRows: 10 }" placeholder="详情" v-model="dataForm.detail">
        </el-input>
      </el-form-item>
      <el-form-item label="logo" prop="logo">
        <pictureUpload :pictureUrl="dataForm.logo" ref="pictureUpload" :key="dataForm.logo"></pictureUpload>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel()">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import enterpriseApi from '@/api/enterprise/enterprise'
import pictureUpload from '@/components/PictureUpload/pictureUpload.vue'
export default {
  components: {
    pictureUpload
  },
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        name: '',
        detail: '',
      },
      dataRule: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ],
        detail: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ],
      }
    }
  },
  methods: {
    init(id) {
      this.dataForm = {
        id: 0,
        name: '',
        detail: '',
      };
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          enterpriseApi.info(this.dataForm.id)
            .then(res => {
              if (res.code === this.ResultCode.success) {
                this.dataForm = res.enterprise
                console.log("企业查询this.dataForm:" + JSON.stringify(this.dataForm.logo))
              }
            })
        }
      })
    },
    cancel() {
      this.visible = false
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          let logo = this.$refs.pictureUpload.picUrl != undefined ? this.$refs.pictureUpload.picUrl : this.dataForm.pictureUrl;
          let data = {
            'id': this.dataForm.id || undefined,
            'name': this.dataForm.name,
            'detail': this.dataForm.detail,
            'status': this.dataForm.status,
            'logo': logo
          };

          if (this.dataForm.id) {
            enterpriseApi.update(data).then(res => {
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
            enterpriseApi.save(data).then(res => {
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
