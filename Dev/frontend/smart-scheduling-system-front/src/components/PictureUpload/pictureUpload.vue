<template>
  <div>
    <div class="logo">
      <div class="block" @click="openAvatarChangeDialog()">
        <div class="noPictureDiv" v-if="!picUrl" fit="contain" lazy>
          <i class="el-icon-picture-outline" style="font-size: 50px;"></i>
        </div>
        <el-image class="image" v-if="picUrl" :src="picUrl" fit="contain">
          <div slot="error" class="image-slot">
            <span>加载失败</span>
          </div>
        </el-image>
      </div>
    </div>
    <!-- 头像上传对话框 -->
    <el-dialog title="上传logo" :visible.sync="avatarChangeDialogVisible" width="400px" append-to-body>
      <div style="display: flex;justify-content: center;">
        <el-upload class="avatar-uploader" :action="ossPath"
                   :data="dataObj" list-type="picture" :multiple="false" :show-file-list="showFileList"
                   :file-list="fileList" :before-upload="beforeUpload" :on-remove="handleRemove"
                   :on-success="handleUploadSuccess" :on-preview="handlePreview"
        >
          <div class="centerDiv">
            <div class="display: flex;justify-content: center;align-items: center;">
              <i class="el-icon-plus avatar-uploader-icon" style="font-size: 50px;margin: 50px;"></i>
            </div>
            <div slot="tip" class="el-upload__tip">
              只能上传jpg/png文件，且不超过10MB
            </div>
          </div>
        </el-upload>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="fileList[0].url" alt=""/>
    </el-dialog>
  </div>
</template>

<script>
import enterpriseApi from '@/api/enterprise/enterprise'
import ossApi from '@/api/thirdParty/oss'
import uuidApi from '@/utils/uuid'

export default {
  name: 'SmartSchedulingSystemPictureUpload',

  props: {
    pictureUrl: {
      type: String
    }
  },

  data() {
    return {

      //oss上传地址
      ossPath: process.env.VUE_APP_OSS_PATH,

      ///图片上传
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
      picUrl: undefined
    }
  },

  created() {
    this.picUrl = this.pictureUrl
  },

  mounted() {

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

  methods: {
    ///图片上传
    openAvatarChangeDialog() {
      this.avatarChangeDialogVisible = true
      this.picUrl = ''
    },
    handleRemove(file, fileList) {

    },
    handlePreview(file) {
      this.dialogVisible = true
    },
    beforeUpload(file) {
      //判断文件类型和大小是否合适
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 10
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 和 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 10MB!')
      }
      //获取OSS签名
      return new Promise((resolve, reject) => {
        ossApi.getPolicy()
          .then((response) => {
            console.log('policy response:' + JSON.stringify(response))
            // debugger;
            this.dataObj.policy = response.data.policy
            this.dataObj.signature = response.data.signature
            this.dataObj.ossaccessKeyId = response.data.accessId
            this.dataObj.key = response.data.dir + uuidApi.getUUID() + '_${filename}'
            this.dataObj.dir = response.data.dir
            this.dataObj.host = response.data.host
            console.log('获取policy成功')
            resolve(true)
          })
          .catch((err) => {
            console.log('获取policy失败')
            reject(false)
          })
      })

    },
    handleUploadSuccess(res, file) {
      console.log('上传成功...')
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
      this.picUrl = this.fileList[0].url
      this.avatarChangeDialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.noPictureDiv {
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
  /* 添加阴影 */
  background-color: #f0f0f0;
  /* 添加背景色 */
}

.image {
  height: 150px;
  border-radius: 10px;
  box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
  transition: all 0.3s ease-in-out;
}
</style>
