<template>
  <div class="body">
    <div class="content">
      <div class="left">
        <img src="@/assets/login/xiaoBaiTemplate/img2.png" class="people p-animtion" alt="people">
        <img src="@/assets/login/xiaoBaiTemplate/img1.png" class="sphere s-animtion" alt="sphere">
      </div>
      <div class="right">
        <!-- <div class="top">
        <div class="top-item">
            <span class="top-text" @click="switchLoginOrRegister">{{ loginOrRegisterTitle }}</span>
        </div>
        <div class="top-item">
            <span class="top-text">联系我们</span>
        </div>
        <div class="top-item">
            <span class="top-text">帮 助</span>
        </div>
    </div> -->
        <div class="form-wrappepr">
          <div style="display: block;width: 100%;">
            <h1 style="margin-bottom: 5vh;font-weight: bold;display: block;" v-if="pageType === 0">欢迎进入智能排班系统
            </h1>
            <h1 style="margin-bottom: 5vh;font-weight: bold;display: block;" v-if="pageType === 1">欢迎进行用户注册</h1>
            <!-- <h1 style="margin-bottom: 5vh;font-weight: bold;display: block;" v-if="pageType === 2">欢迎进行企业注册</h1> -->
          </div>

          <!-- 登录表单 -->
          <el-form class="login-form" :rules="loginRules" ref="loginForm" :model="loginForm" label-width="0"
                   v-if="pageType === 0"
          >
            <input type="text" class="inputs user" placeholder="请输入用户名" v-model="loginForm.username">
            <input type="password" class="inputs pwd" placeholder="请输入密码" v-model="loginForm.password">

            <div style="display: flex;margin: 0px;padding: 0px;">
              <input type="text" class="inputs user" placeholder="请输入验证码" v-model="loginForm.verificationCode"
                     style="display: flex;margin: 0px;"
              >
              <el-tooltip class="item" effect="dark" content="验证码看不清？点击图片可以刷新" placement="right-end">
                <img :src="'data:image/png;base64,' + verifyCodeImg" alt="image"
                     style="border-radius: 10px;margin-left: 20px;" @click="freshVerificationCode()"
                />
              </el-tooltip>
            </div>

            <div style="margin-top: 30px;">
              <span class="tips">忘记密码</span>
            </div>

            <button v-if="pageType === 0" @click="handleLogin()" class="custom-button">登 录</button>

            <div>
                            <span style="float: left">
                                <el-button type="text" @click="enterEnterpriseRegist()">企业注册</el-button>
                            </span>
              <span style="float: right">
                                没有账号？<el-button type="text" @click="enterRegister()">点击注册</el-button>
                            </span>
            </div>


          </el-form>

          <!-- 注册表单 -->
          <el-form class="regist-form" :rules="loginRules" ref="registForm" :model="registForm" label-width="0"
                   v-if="pageType === 1"
          >
            <input type="text" class="inputs user" placeholder="请输入用户名" v-model="registForm.username"
                   @change="usernameCheck()"
            >
            <input type="password" class="inputs pwd" placeholder="请输入密码" v-model="registForm.password">
            <input type="text" class="inputs user" placeholder="请输入邮箱" v-model="registForm.mail">
            <div style="display: flex;margin: 0px;padding: 0px;">
              <input type="text" class="inputs user" placeholder="请输入验证码" v-model="registForm.code"
                     style="display: flex;margin: 0px;"
              >
              <el-button type="text" class="custom-button"
                         style="  background-color: rgb(99, 146, 248);width: 170px; height: 60px;margin:0px 0px 0px 20px;"
                         @click="sendCode()"
              >{{ sendCodeMes1 }}
              </el-button>
            </div>


            <button v-if="pageType === 1" class="custom-button" @click="handleRegist()">注 册</button>
            <div>
                            <span style="float: left">
                                <el-button type="text" @click="enterEnterpriseRegist()">企业注册</el-button>
                            </span>
              <span style="float: right">
                                已有账号？<el-button type="text" @click="enterLogin()">点击登录</el-button>
                            </span>
            </div>

          </el-form>

          <!-- 企业注册表单 -->
          <el-form class="enterprise-regist-form" ref="entertpriseRegistForm" :model="registForm" label-width="0"
                   v-if="pageType === 2"
          >
            <div class="logo">
              <div class="block" @click="openAvatarChangeDialog()">
                <el-image style="width:150px;height: 150px" v-if="!entertpriseRegistForm.logo" fit="contain"
                          lazy
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <el-image style="height: 150px" v-if="entertpriseRegistForm.logo"
                          :src="entertpriseRegistForm.logo" :fit="contain"
                >
                  <div slot="error" class="image-slot">
                    <span>加载失败</span>
                  </div>
                </el-image>
              </div>
            </div>

            <input type="text" class="inputs user" placeholder="请输入企业名称" v-model="entertpriseRegistForm.name">
            <input type="text" class="inputs user" placeholder="请输入企业描述" v-model="entertpriseRegistForm.detail"
                   @click="writeEnterpriseDetail()"
            >
            <input type="text" class="inputs user" placeholder="请输入邮箱" v-model="entertpriseRegistForm.mail">
            <div style="display: flex;margin: 0px;padding: 0px;">
              <input type="text" class="inputs user" placeholder="请输入验证码" v-model="entertpriseRegistForm.code"
                     style="display: flex;margin: 0px;"
              >
              <el-button type="text" class="custom-button"
                         style="  background-color: rgb(99, 146, 248);width: 170px; height: 60px;margin:0px 0px 0px 20px;"
                         @click="sendCode()"
              >{{ sendCodeMes2 }}
              </el-button>
            </div>


            <button v-if="pageType === 2" class="custom-button" @click="handleEnterpriseRegist()">企 业 注 册</button>
            <div>
                            <span style="float: right">
                                企业已注册？<el-button type="text" @click="enterLogin()">回到登录</el-button>
                            </span>
            </div>

            <!-- 企业详情 -->
            <el-dialog title="企业详情" :visible.sync="enterpriseDetailVisible" width="30%" append-to-body>
              <el-input type="textarea" :rows="5" placeholder="请输入内容" v-model="entertpriseRegistForm.detail">
              </el-input>
              <span slot="footer" class="dialog-footer">
                                <el-button type="primary" @click="enterpriseDetailVisible = false">确 定</el-button>
                            </span>
            </el-dialog>
            <!-- 头像上传对话框 -->
            <el-dialog title="上传logo" :visible.sync="avatarChangeDialogVisible" width="400px" append-to-body>
              <div style="display: flex;justify-content: center;">
                <el-upload class="avatar-uploader"
                           :action="ossPath"
                           :data="dataObj" list-type="picture" :multiple="false" :show-file-list="showFileList"
                           :file-list="fileList" :before-upload="beforeUpload" :on-remove="handleRemove"
                           :on-success="handleUploadSuccess" :on-preview="handlePreview"
                >
                  <div class="centerDiv">
                    <div class="display: flex;justify-content: center;align-items: center;">
                      <i class="el-icon-plus avatar-uploader-icon"
                         style="font-size: 50px;margin: 50px;"
                      ></i>
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

          </el-form>

          <div class="other-login" v-if="pageType === 0">
            <div class="divider">
              <span class="line"></span>
              <span class="divider-text">使用其他方式登录</span>
              <span class="line"></span>
            </div>
            <div class="other-login-wrapper">
              <div class="other-login-item">
                <img src="@/assets/login/xiaoBaiTemplate/QQ.png" alt="QQ">
              </div>
              <div class="other-login-item" @click="toWxLogin()">
                <img src="@/assets/login/xiaoBaiTemplate/WeChat.png" alt="WeChat">
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>


<script>
//引入css
import xiaoBaiLoginStyle from '@/styles/xiaoBaiLogin/style.css'

//引入js方法
import { usernameCheck, sendMailCode, regist, generateVerificationCode, enterpriseRegister } from '@/api/login/user'
import enterpriseApi from '@/api/enterprise/enterprise'
import ossApi from '@/api/thirdParty/oss'
import uuidApi from '@/utils/uuid'

//组件
import verificationCode from './verificationCode.vue'

export default {
  name: 'SmartSchedulingSystemIndex',
  components: {
    verificationCode
  },

  data() {
    return {
      //oss上传地址
      ossPath: process.env.VUE_APP_OSS_PATH,

      ////页面
      loginOrRegisterTitle: '注 册',
      //页面类型 0：登录 1：用户注册 2：企业注册
      pageType: 0,

      ////登录
      loginForm: {
        //用户名
        username: 'one',
        //密码
        password: '123456',
        rememberPwd: false,
        uuid: '',
        verificationCode: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, message: '密码长度最少为6位', trigger: 'blur' }
        ]
      },
      ///验证码
      //uuid
      uuid: '',
      //验证码图片
      verifyCodeImg: 'iVBORw0KGgoAAAANSUhEUgAAAHgAAAA8CAIAAAAiz+n/AAABAElEQVR42u3ZvwnCQBTA4dtKEKwdyQnsncHCxsIpXMAFnMAdFGyChpA/L7lc8sGvuvIjvPDu0uP50gQlBKBBCzRo0BRAgxZo0KApgAYt0KBBCzRogQYNWqBBCzRo0AJdabe5fwI9X+jr+dgQ6LAWC3057RvKLr6c0REF/Z0V/+WFvh221fJDtzksC/qHuJP1dDM6ZG5khK5lbW+d1qMcAt3+fL3QIZ9z7+mRSlEGvSLoWS8sscqF/glHhw5cUnooNy+EXdGHzI2JoHNddMRC11rPYnTELtxFX90VA1268ojQQ5Rj12730R2gvbAINGjQAg1aoEGDFmjQAg0atECX3htauJtUKiEdmQAAAABJRU5ErkJggg==',

      ////注册
      registForm: {
        //用户名
        username: '',
        //密码
        password: '',
        //邮箱
        mail: '',
        code: ''
      },
      sendCodeMes1: '发送验证码',
      //倒计时
      countdownNum1: 60,
      interval1: {},
      isCount1: 0,
      //公司选项
      enterpriseOptions: [],

      ////企业注册
      entertpriseRegistForm: {
        //企业名称
        name: '',
        //企业描述
        detail: '',
        //邮箱
        mail: '',
        code: ''
      },
      sendCodeMes2: '发送验证码',
      //倒计时
      countdownNum2: 60,
      interval2: {},
      isCount2: 0,
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
      avatarUrl: '',
      ///企业详情
      enterpriseDetailVisible: false
    }
  },

  created() {
    // 获取路径token里面的值
    this.token = this.$route.query.token
    if (this.token) {//有值才判断
      this.wxLogin(this.token)
    } else if (this.$route.query.message) {
      this.$message.error('该微信还未绑定智能排班系统的账号，请通过账号密码登录')
    }

    this.freshVerificationCode()

    console.log("ossPath:"+this.ossPath)
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
    /**
     * 开启星球的动画事件
     */
    document.querySelector('.people').addEventListener('animationend', function() {
      this.classList.remove('p-animtion')
      this.classList.add('p-other-animtion')
    })
    document.querySelector('.sphere').addEventListener('animationend', function() {
      this.classList.remove('s-animtion')
      this.classList.add('s-other-animtion')
    })
  },

  methods: {

    /**
     * 切换登录
     */
    enterLogin() {
      this.pageType = 0
      this.loginOrRegisterTitle = '登 录'
    },
    /**
     * 切换注册
     */
    enterRegister() {
      this.pageType = 1
      this.loginOrRegisterTitle = '注 册'
    },
    /**
     * 进入企业注册
     */
    enterEnterpriseRegist() {
      this.pageType = 2
      this.loginOrRegisterTitle = '企 业 注 册'
    },
    //跳转到微信登录页面
    toWxLogin() {
      //跳转到登录扫描页面
      location.href = 'http://localhost:8160/api/ucenter/wx/login'
    },
    showPassword() {
      this.passwordType === ''
        ? (this.passwordType = 'password')
        : (this.passwordType = '')
    },
    wxLogin(token) {
      this.$store
        .dispatch('user/weixinLogin', token)
      this.$router.push({ path: this.redirect || '/' })
      //进入首页的时候刷新一下首页，不然样式会残留(相当于f5)
      location.reload(true)
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          this.loginForm.uuid = this.uuid
          console.log('this.loginForm:' + JSON.stringify(this.loginForm))
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({
                path: this.redirect || '/'
              })
              //进入首页的时候刷新一下首页，不然样式会残留(相当于f5)
              location.reload(true)
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    //处理注册
    handleRegist() {
      regist(this.registForm).then((res) => {
        //提示
        this.$message({
          type: 'success',
          message: '注册成功，请登录系统'
        })
        this.pageType = 0
      })
    },
    //用户名查重
    usernameCheck() {
      // console.log("hduashdusahdush");
      usernameCheck(this.registForm.username).then((res) => {
        // console.log("res:" + JSON.stringify(res));
        if (res.isExist == true) {
          //提示
          this.$message({
            type: 'error',
            message: '用户名重复，请修改用户名!'
          })
        }
      })
    },
    //发送验证码
    sendCode() {
      if (this.pageType == 1) {
        if (this.registForm.mail == '') {
          //提示
          this.$message({
            type: 'error',
            message: '请先输入邮箱，再发送验证码'
          })
        } else {
          if (this.isCount1 == 0) {
            this.registForm.code = ''
            this.sendCodeMes1 = this.countdownNum1 + '后再发送'
            this.isCount1 = 1
            this.interval1 = setInterval(this.countdown, 1000)
            sendMailCode(this.registForm.mail, 0).then((res) => {
            })
          }
        }
      } else if (this.pageType == 2) {
        if (this.entertpriseRegistForm.mail == '') {
          //提示
          this.$message({
            type: 'error',
            message: '请先输入邮箱，再发送验证码'
          })
        } else {
          if (this.isCount2 == 0) {
            this.entertpriseRegistForm.code = ''
            this.sendCodeMes2 = this.countdownNum2 + '后再发送'
            this.isCount2 = 1
            this.interval2 = setInterval(this.countdown, 1000)
            sendMailCode(this.entertpriseRegistForm.mail, 1).then((res) => {
            })
          }
        }
      }

    },
    //倒计时
    countdown() {
      if (this.pageType == 1) {
        this.countdownNum1--
        if (this.countdownNum1 <= 0) {
          //停止运行计时
          clearInterval(this.interval1)
          this.isCount1 = 0
          this.countdownNum1 = 60
          this.sendCodeMes1 = '发送验证码'
        } else {
          this.sendCodeMes1 = this.countdownNum1 + '后再发送'
        }
      } else if (this.pageType == 2) {
        this.countdownNum2--
        if (this.countdownNum2 <= 0) {
          //停止运行计时
          clearInterval(this.interval2)
          this.isCount2 = 0
          this.countdownNum2 = 60
          this.sendCodeMes2 = '发送验证码'
        } else {
          this.sendCodeMes2 = this.countdownNum2 + '后再发送'
        }
      }
    },
    /**
     * 刷新登录验证码
     */
    freshVerificationCode() {
      generateVerificationCode().then(
        res => {

          this.verifyCodeImg = res.image
          this.uuid = res.uuid
          console.log('uuid:' + this.uuid)
        }
      )
    },

    ////企业注册
    ///企业详情
    writeEnterpriseDetail() {
      this.enterpriseDetailVisible = true
    },
    ///图片上传
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
            debugger;
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
      this.avatarUrl = this.fileList[0].url
      this.avatarChangeDialogVisible = false
      this.entertpriseRegistForm.logo = this.avatarUrl
    },
    /**
     * 企业注册
     */
    handleEnterpriseRegist() {
      enterpriseRegister(this.entertpriseRegistForm).then((res) => {
        //提示
        this.$message({
          type: 'success',
          message: '已经发送企业注册请求，请耐心等候，我们将在七个工作日内为您处理，请关注邮箱通知'
        })
      })
    }
  }
}


</script>


<style lang="scss" scoped>

</style>
