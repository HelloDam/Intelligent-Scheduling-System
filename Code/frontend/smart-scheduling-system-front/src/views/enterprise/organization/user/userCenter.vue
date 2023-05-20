<template>
  <div class="mainContainer">
    <el-row :gutter="20">
      <el-col :span="6">
        <div>

          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>个人信息</span>
            </div>
            <div>
              <div class="text-center">
                <!-- 头衔缩略图 -->
                <!-- <pan-thumb :image="user.avatar" /> -->
                <div class="block">
                  <el-avatar :size="150" :src="user.avatar"></el-avatar>
                </div>
              </div>
              <div class="text-center">
                <!-- 文件上传按钮 -->
                <el-button type="text" icon="el-icon-upload" @click="openAvatarChangeDialog()"
                           size="medium"
                >更换头像
                </el-button>
              </div>
              <!-- 头像上传对话框 -->
              <el-dialog title="更换头像" :visible.sync="avatarChangeDialogVisible" width="400px">
                <div style="display: flex;justify-content: center;">
                  <el-upload class="avatar-uploader"
                             :action="ossPath"
                             :data="dataObj" list-type="picture" :multiple="false" :show-file-list="showFileList"
                             :file-list="fileList" :before-upload="beforeUpload" :on-remove="handleRemove"
                             :on-success="handleUploadSuccess" :on-preview="handlePreview"
                  >
                    <div class="centerDiv">
                      <img v-if="avatarUrl" :src="avatarUrl" class="avatar"
                           style="margin-bottom: 10px;"
                      >
                      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                      <div slot="tip" class="el-upload__tip">
                        只能上传jpg/png文件，且不超过10MB
                      </div>
                    </div>
                  </el-upload>
                </div>

                <span slot="footer" class="dialog-footer">
                                    <el-button @click="avatarChangeDialogVisible = false">取 消</el-button>
                                    <el-button type="primary" @click="changeAvatar()">更 换</el-button>
                                </span>
              </el-dialog>
              <el-dialog :visible.sync="dialogVisible">
                <img width="100%" :src="fileList[0].url" alt=""/>
              </el-dialog>

              <ul class="list-group list-group-striped">
                <li class="list-group-item">
                  <i class="el-icon-user-solid"> 用户名称</i>
                  <div class="pull-right">{{ user.username }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-user"> 用户昵称</i>
                  <div class="pull-right">{{ user.nickname }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-user"> 用户姓名</i>
                  <div class="pull-right">{{ user.name }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-phone"> 手机号码</i>
                  <div class="pull-right">{{ user.phone }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-message"> 用户邮箱</i>
                  <div class="pull-right">{{ user.mail }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-s-shop"> 所属企业</i>
                  <div class="pull-right">{{ user.enterpriseName }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-s-shop"> 所属门店</i>
                  <div class="pull-right">{{ user.storeName }}</div>
                </li>
                <li class="list-group-item">
                  <i class="el-icon-s-data"> 用户职位</i>
                  <div class="pull-right">{{ user.positionName }}</div>
                </li>
                <!-- <li class="list-group-item">
                                                                    <i class="el-icon-user-solid">用户权限</i>
                                                                    <div class="pull-right">{{ user.createTime }}</div>
                                                                </li> -->
              </ul>
            </div>
          </el-card>

        </div>
      </el-col>
      <el-col :span="18">
        <div>

          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>自定义内容</span>
            </div>
            <div>
              <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane label="基本资料" name="first">
                  <userInfo :user="user" style="width:35%"/>
                </el-tab-pane>
                <el-tab-pane label="修改密码" name="second">
                  <resetPwd style="width:35%"/>
                </el-tab-pane>
                <el-tab-pane label="工作偏好" name="third">
                  <workPreference :user="user"/>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-card>

        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import PanThumb from '@/components/PanThumb'
import ossApi from '@/api/thirdParty/oss'
import uuidApi from '@/utils/uuid'
import { getToken } from '@/utils/auth'
import userApi from '@/api/system/user'
import userInfo from './userInfo'
import resetPwd from './resetPwd'
import workPreference from './workPreference.vue'

export default {
  name: 'SmartSchedulingSystemUserCenter',

  components: { PanThumb, userInfo, resetPwd, workPreference },

  data() {
    return {
      //oss上传地址
      ossPath: process.env.VUE_APP_OSS_PATH,

      token: '',
      user: {
        workDayPreferenceList: new Array(),
        timeSlotList: new Array()
      },

      ////头像上传
      //上传弹框组件是否显示
      avatarChangeDialogVisible: false,
      dataObj: {
        policy: '',
        signature: '',
        key: '',
        ossaccessKeyId: '',
        dir: '',
        host: ''
        // callback:'',
      },
      dialogVisible: false,
      avatarUrl: '',

      ////自定义内容
      //默认激活tab
      activeName: 'first'
    }
  },

  computed: {
    imageUrl() {
      return this.value
    },
    imageName() {
      if (this.value != null && this.value !== '') {
        return this.value.substr(this.value.lastIndexOf('/') + 1)
      } else {
        return null
      }
    },
    fileList() {
      return [
        {
          name: this.imageName,
          url: this.imageUrl
        }
      ]
    },
    showFileList: {
      get: function() {
        return (
          this.value !== null && this.value !== '' && this.value !== undefined
        )
      },
      set: function(newValue) {
      }
    }
  },

  mounted() {

  },

  created() {
    this.initUserMessage()
  },

  methods: {

    //初始化用户信息
    initUserMessage() {
      userApi.getUserInfoVoByToken().then(res => {
        // console.log("res.userInfoVo:" + JSON.stringify(res.userInfoVo))
        this.user = res.userInfoVo
        this.user.gender = this.user.gender + ''
        // console.log("this.user.id:" + this.user.id)
        ///解析工作时间偏好
        this.user.timeSlotList = this.parseTimeSlotList(this.user.workTimePreference + '')
      })
    },
    //解析工作时间偏好
    parseTimeSlotList(workTimePreference) {
      let timeSlotList = new Array()
      let splict = workTimePreference.split('|')
      for (let i = 0; i < splict.length; i++) {
        // console.log("splict[i]:" + splict[i])
        let timeSlot = splict[i].split('~')
        timeSlotList.push({ startTime: timeSlot[0], endTime: timeSlot[1] })
      }
      // console.log("timeSlotList:" + JSON.stringify(timeSlotList))
      return timeSlotList

    },
    //更新用户信息
    updateUserInfo(user) {
      userApi.update(user).then(res => {
        this.$message.success('保存成功')
      })
    },
    //修改用户头像
    changeAvatar() {
      // console.log("this.user.id:" + this.user.id)
      let user = {
        id: this.user.id,
        avatar: this.avatarUrl
      }
      // console.log("user:" + JSON.stringify(user))
      this.updateUserInfo(user)
      this.avatarChangeDialogVisible = false
      this.initUserMessage()
    },
    //打开头像更换对话框
    openAvatarChangeDialog() {
      this.avatarChangeDialogVisible = true
      this.avatarUrl = ''
    },
    handleRemove(file, fileList) {

    },
    handlePreview(file) {
      this.dialogVisible = true
    },
    beforeUpload(file) {
      ////判断文件类型和大小是否合适
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 10
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 和 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 10MB!')
      }
      ////获取OSS签名
      return new Promise((resolve, reject) => {
        ossApi.getPolicy()
          .then((response) => {
            console.log('policy response:' + JSON.stringify(response))
            this.dataObj.policy = response.data.policy
            this.dataObj.signature = response.data.signature
            this.dataObj.ossaccessKeyId = response.data.accessId
            this.dataObj.key = response.data.dir + uuidApi.getUUID() + '_${filename}'
            this.dataObj.dir = response.data.dir
            this.dataObj.host = response.data.host
            resolve(true)
          })
          .catch((err) => {
            reject(false)
          })
      })

    },
    handleUploadSuccess(res, file) {
      // console.log("上传成功...");
      this.showFileList = true
      this.fileList.pop()
      this.fileList.push({
        name: file.name,
        url:
          this.dataObj.host +
          '/' +
          this.dataObj.key.replace('${filename}', file.name)
      })
      // console.log("this.fileList[0].url:" + this.fileList[0].url)
      this.avatarUrl = this.fileList[0].url
    },
    //切换tab
    handleClick() {

    }

  }
}
</script>

<style lang="scss" scoped>
.el-row {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.el-col {
  border-radius: 4px;
}

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}

.mainContainer {
  margin: 20px;
}

.text-center {
  text-align: center
}

.pull-right {
  float: right !important;
}

.list-group-striped > .list-group-item {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}

.list-group {
  padding-left: 0px;
  list-style: none;
}

.list-group-item {
  border-bottom: 1px solid #e7eaec;
  border-top: 1px solid #e7eaec;
  margin-bottom: -1px;
  padding: 11px 0px;
  font-size: 13px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  text-align: center;
}

.centerDiv {
  display: flex;
  justify-content: center;
  // /* 子元素水平居中 */
  align-items: center;
  flex-direction: column;
  width: 100%;
}

.el-dialog .el-dialog__body {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
