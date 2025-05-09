<template>
  <div class="contentClass">
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

.contentClass{
  margin: 20px;
  overflow-y: auto;
  overflow-x: hidden;

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

}

/* 定义滚动条的轨道和滑块样式 */
.contentClass:hover::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.contentClass:hover::-webkit-scrollbar-thumb {
  background-color: #D2D2D2;
  border-radius: 5px;
}

.contentClass:hover::-webkit-scrollbar-button {
  background-color: #D2D2D2;
  height: 5px;
}

/* 隐藏滚动条 */
.contentClass::-webkit-scrollbar {
  // 隐藏滚动条宽度
  // width: 0 !important;
  // 隐藏滚动条高度
  // height: 0 !important;
  // 隐藏滚动条背景
  background-color: transparent;
  width: 10px;
  height: 5px;
}

/* 鼠标进来的时候显示滚动条 */
.contentClass:hover::-webkit-scrollbar {
  // background-color: #f1f1f1;
  width: 10px;
  height: 5px;
}

</style>
