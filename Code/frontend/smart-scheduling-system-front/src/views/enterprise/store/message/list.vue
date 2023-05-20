<template>
  <div class="messagerContainer">

    <el-row :gutter="20" style="height: 100%;">
      <el-col :span="6" style="height: 100%;">
        <div class="contentDiv">
          <div class="titleDiv" style="justify-content: space-between;">
            <span style="margin-right: 20px;margin-left:15px; font-weight: bold;">定时任务</span>
            <el-button type="primary" size="mini" @click="addOrUpdateSystemScheduledNotice()"
              icon="el-icon-document-checked">保存</el-button>
          </div>

          <div class="scheduledTasksDiv">
            <el-card class="box-card" style="    margin-bottom: 20px;">
              <div slot="header" class="clearfix">
                <span>上班通知</span>
                <!-- <el-button style="float: right; padding: 3px 0" type="text"  @click="addOrUpdateSystemScheduledNotice()">保存</el-button> -->
              </div>
              <el-form label-position="top" label-width="80px" :model="systemScheduledNotice">
                <el-form-item label="是否启用上班通知">
                  <el-select v-model="systemScheduledNotice.workNoticeUse" clearable placeholder="是否启用上班通知"
                    style="width: 100%;">
                    <el-option v-for="item in noticeUseOptions" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="通知方式">
                  <el-select v-model="systemScheduledNotice.workNoticeType" clearable placeholder="请选择通知方式"
                    style="width: 100%;">
                    <el-option v-for="item in noticeTypeOptions" :key="item.value" :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="通知时间">
                  <el-time-picker v-model="systemScheduledNotice.workNoticeTime" :picker-options="{
                    selectableRange: '00:00:00 - 23:59:59'
                  }" placeholder="通知时间" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss">
                  </el-time-picker>
                </el-form-item>
              </el-form>
            </el-card>

            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>休假通知</span>
                <!-- <el-button style="float: right; padding: 3px 0" type="text"  @click="addOrUpdateSystemScheduledNotice()">保存</el-button> -->
              </div>
              <el-form label-position="top" label-width="80px" :model="systemScheduledNotice">
                <el-form-item label="是否启用休假通知">
                  <el-select v-model="systemScheduledNotice.holidayNoticeUse" clearable placeholder="是否启用休假通知"
                    style="width: 100%;">
                    <el-option v-for="item in noticeUseOptions" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="通知方式">
                  <el-select v-model="systemScheduledNotice.holidayNoticeType" clearable placeholder="请选择通知方式"
                    style="width: 100%;">
                    <el-option v-for="item in noticeTypeOptions" :key="item.value" :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="通知时间">
                  <el-time-picker v-model="systemScheduledNotice.holidayNoticeTime" :picker-options="{
                    selectableRange: '00:00:00 - 23:59:59'
                  }" placeholder="通知时间" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss">
                  </el-time-picker>
                </el-form-item>
              </el-form>
            </el-card>

          </div>

        </div>
      </el-col>
      <el-col :span="18" style="height: 100%;">

        <div class="contentDiv">
          <div class="titleDiv">
            <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">消息管理</span>
            <el-input v-model="dataForm.key" placeholder="请输入消息主题/内容" clearable style="width: 200px;margin-right: 10px;"
              @keyup.enter.native="handleQuery" size="small" />

            <el-select v-model="dataForm.isPublish" placeholder="请选择发布状态" size="small"
              style="margin-right: 10px;width: 150px;" clearable>
              <el-option v-for="item in publishOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="getDataList()">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </div>

          <div class="toolDiv">
            <el-button type="text" size="small" @click="addOrUpdateHandle()">
              <i class="iconfont icon-tianjia" style="margin-right: 3px;"></i>添加</el-button>
            <el-button id="button-danger" type="text" icon="el-icon-delete" size="small"
              :disabled="dataListSelections.length <= 0" @click="deleteHandle()">批量删除</el-button>
          </div>

          <div class="tableDisplayDiv">
            <div class="tableDiv">
              <el-table :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandle"
                style="width: 100%;" height="100%">
                <el-table-column type="selection" header-align="center" align="center" width="50">
                </el-table-column>
                <el-table-column prop="subject" header-align="center" align="center" label="消息主题" width="150">
                </el-table-column>
                <el-table-column header-align="center" align="center" label="消息内容" width="600">
                  <template slot-scope="scope">
                    <span v-html="scope.row.content"></span>
                  </template>
                </el-table-column>
                <el-table-column prop="isPublish" header-align="center" align="center" label="发布状态" width="200">
                  <template slot-scope="scope">
                    <el-switch style="display: block" v-model="scope.row.isPublish" active-color="#13ce66"
                      inactive-color="#ff4949" active-text="已发布" inactive-text="未发布"
                      @change="publistStatusChange(scope.row.id, scope.row.isPublish)">
                    </el-switch>
                  </template>
                </el-table-column>
                <el-table-column prop="publishTime" header-align="center" align="center" label="发布时间" width="150">
                </el-table-column>
                <el-table-column prop="createTime" header-align="center" align="center" label="创建时间" width="150">
                </el-table-column>
                <el-table-column prop="updateTime" header-align="center" align="center" label="更新时间" width="150">
                </el-table-column>
                <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
                    <el-button id="button-danger" type="text" size="small"
                      @click="deleteHandle(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>

            </div>
            <pagination v-show="totalPage > 0" :total="totalPage" :page.sync="pageIndex" :limit.sync="pageSize"
              @pagination="getDataList()" />
          </div>

        </div>


      </el-col>
    </el-row>

  </div>
</template>

<script>
import messageApi from '@/api/enterprise/message'
import AddOrUpdate from './message-add-or-update'
import TinymceEditor from '@/views/editor/tinymceEditor'
import systemScheduledNoticeApi from '@/api/system/systemScheduledNotice'

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
      addOrUpdateVisible: false,

      publishOptions: [
        { value: 0, label: "未发布" },
        { value: 1, label: "已发布" },
      ],

      ////系统定时通知
      systemScheduledNotice: {},
      isEndNoticeLoading: false,
      noticeUseOptions: [
        { value: 0, label: "禁用" },
        { value: 1, label: "启用" },
      ],
      noticeTypeOptions: [
        { value: 0, label: "系统发送消息" },
        { value: 1, label: "发送邮件" },
        { value: 2, label: "系统发送消息+发送邮件" },
      ]

    }
  },
  components: {
    AddOrUpdate,
    TinymceEditor
  },
  created() {
    if (this.$route.query.listParams) {
      // console.log("this.$route.query.listParams:" + JSON.stringify(this.$route.query.listParams))
      this.pageIndex = this.$route.query.listParams.page;
      this.pageSize = this.$route.query.listParams.limit;
      this.dataForm.key = this.$route.query.listParams.key;
      this.dataForm.isPublish = this.$route.query.listParams.isPublish;
    }
    this.getDataList();
    this.infoSystemScheduledNotice();
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true

      let params = {
        'page': this.pageIndex,
        'limit': this.pageSize,
        'key': this.dataForm.key,
        'isPublish': this.dataForm.isPublish,
      };

      messageApi.list(params)
        .then(res => {
          if (res.code === this.ResultCode.success) {
            this.dataList = res.page.list
            for (let i = 0; i < this.dataList.length; i++) {
              this.dataList[i].isPublish = this.dataList[i].isPublish == 1 ? true : false;
            }
            this.totalPage = res.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      let params = {
        'page': this.pageIndex,
        'limit': this.pageSize,
        'key': this.dataForm.key,
        'isPublish': this.dataForm.isPublish,
      };
      if (id) {
        this.$router.push({ path: '/enterprise/store/messageAddOrUpdate', query: { id: id, listParams: params } })
      } else {
        this.$router.push({ path: '/enterprise/store/messageAddOrUpdate', query: { listParams: params } })
      }

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
        messageApi.deleteBatch(ids)
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
    },
    /**
    * 清除表单
    */
    resetQuery() {
      this.dataForm = [];
    },

    ////消息发布
    publistStatusChange(messageId, isPublish) {

      let tip;
      if (isPublish == false) {
        tip = "取消发布消息，请问是否继续?";
      } else {
        tip = "将发送消息消息员工，请问是否继续?";
      }

      this.$confirm(tip, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        messageApi.updateMessagePublishStatus({ messageId: messageId, isPublish: isPublish == true ? 1 : 0 }).then(
          res => {
            if (isPublish == false) {
              this.$message.success("消息撤回成功");
            } else {
              this.$message.success("消息发布成功");
            }
            this.getDataList()
          }
        )
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        });
      });

    },


    ////系统定时任务
    //查询系统定时任务
    infoSystemScheduledNotice() {
      systemScheduledNoticeApi.infoByToken().then(
        res => {
          if (res.systemScheduledNotice != null) {
            this.systemScheduledNotice = res.systemScheduledNotice;
          }
          this.isEndNoticeLoading = true;
        }
      )
    },
    //添加或者更新定时任务
    addOrUpdateSystemScheduledNotice() {
      if (this.isEndNoticeLoading = false) {
        //--if--如果通知设置信息还没有被加载出来，就直接return，防止用户快速保存，同一个门店的数据出现多条
        return;
      }
      if (this.systemScheduledNotice.id) {
        systemScheduledNoticeApi.update(this.systemScheduledNotice).then(
          res => {
            this.$message.success("修改定时任务设置成功");
          }
        )
      } else {
        systemScheduledNoticeApi.save(this.systemScheduledNotice).then(
          res => {
            this.$message.success("保存定时任务设置成功");
          }
        )
      }
    }

  }
}
</script>
<style lang="scss" scoped>
.messagerContainer {
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

    .scheduledTasksDiv {
      background: #FFFFFF;
      // height: 300px;
      height: calc(100% - 45px);
      padding: 20px 10px 20px 20px;
      overflow-y: auto;
      overflow-x: hidden;
    }

    /* 定义滚动条的轨道和滑块样式 */
    .scheduledTasksDiv:hover::-webkit-scrollbar-track {
      background-color: #f1f1f1;
    }

    .scheduledTasksDiv:hover::-webkit-scrollbar-thumb {
      background-color: #D2D2D2;
      border-radius: 5px;
    }

    .scheduledTasksDiv:hover::-webkit-scrollbar-button {
      background-color: #D2D2D2;
      height: 5px;
    }

    /* 隐藏滚动条 */
    .scheduledTasksDiv::-webkit-scrollbar {
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
    .scheduledTasksDiv:hover::-webkit-scrollbar {
      // background-color: #f1f1f1;
      width: 10px;
      height: 5px;
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



.box-card {}
</style>
