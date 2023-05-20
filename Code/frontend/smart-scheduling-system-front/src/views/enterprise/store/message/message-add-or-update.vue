<template>
  <div style="margin: 20px;">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>{{ !dataForm.id ? '新建消息' : '修改消息' }}</span>
      </div>

      <el-form ref="form" :model="dataForm" label-width="80px">
        <el-form-item label="消息主题">
          <el-input v-model="dataForm.subject"></el-input>
        </el-form-item>
        <el-form-item label="消息内容">
          <TinymceEditor v-model="dataForm.content"></TinymceEditor>
        </el-form-item>
      </el-form>

      <span style="float: right;margin: 10px 0">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
      </span>
    </el-card>
  </div>
</template>

<script>
import messageApi from '@/api/enterprise/message'
import TinymceEditor from '@/views/editor/tinymceEditor'
export default {
  data() {
    return {
      dataForm: {
        id: 0,
        createTime: '',
        updateTime: '',
        isDeleted: '',
        type: '',
        content: '',
        storeId: '',
        enterpriseId: '',
        isPublish: '',
        publishTime: ''
      },
      dataRule: {
        content: [
          { required: true, message: '消息内容不能为空', trigger: 'blur' }
        ],
      },
      //存储上一个页面跳转过来之前的查询参数，跳转回去的时候也带上这个参数
      listParams: {}
    }
  },
  components: {
    TinymceEditor
  },
  created() {
    if (this.$route.query.id) {
      this.dataForm.id = this.$route.query.id;
    }
    if (this.$route.query.listParams) {
      // console.log("this.$route.query.listParams:" + JSON.stringify(this.$route.query.listParams))
      this.listParams = this.$route.query.listParams;
    }
    this.init();
  },
  methods: {
    init() {
      if (this.dataForm.id) {
        messageApi.info(this.dataForm.id).then(res => {
          // console.log(JSON.stringify(res))
          this.dataForm = res.message;
        })
      }
    },
    cancel() {
      this.$router.push('/enterprise/store/message')
    },
    // 表单提交
    dataFormSubmit() {
      let data = {
        'content': this.dataForm.content,
        'subject': this.dataForm.subject,
      }

      if (this.dataForm.id) {
        data.id = this.dataForm.id;
        messageApi.updateById(data).then(res => {
          // console.log("this.ResultCode.success:" + this.ResultCode.success)
          // console.log("res:" + JSON.stringify(res))
          if (res.code === this.ResultCode.success) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
            })
            this.$router.push({ path: '/enterprise/store/message', query: { listParams: this.listParams } })
            // this.$emit('refreshDataList')
          } else {
            this.$message.error(res.msg)
          }
        })
      } else {
        messageApi.save(data).then(res => {
          if (res && res.code === this.ResultCode.success) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
            })
            this.$router.push({ path: '/enterprise/store/message', query: { listParams: this.listParams } })
            // this.$emit('refreshDataList')
          } else {
            this.$message.error(message.message)
          }
        })
      }

    }
  }
}
</script>

<style lang="scss" scoped>
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  width: 100%;
}
</style>