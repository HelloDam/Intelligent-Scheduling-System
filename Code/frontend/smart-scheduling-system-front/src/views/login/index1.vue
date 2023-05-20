<template>
  <div id="logintwo" :style="backgroundDiv">
    <div class="avue-home" v-if="loading" style="z-index: 99">
      <div class="avue-home__main">
        <img class="avue-home__loading" src="@/assets/login/img/loading-spin.svg" alt="loading" />
        <div class="avue-home__title">努力加载中...</div>
      </div>
    </div>

    <div style="display: flex; width: 100%; height: 100%; overflow: hidden" v-if="pageType == 0">
      <div class="login-modal">
        <div class="title"
          style="height: 100px;line-height: 100px; font-weight: 650; text-align: center; font-size: 28px;">
          登录
        </div>
        <el-form class="login-form" :rules="loginRules" ref="loginForm" :model="loginForm" label-width="0">
          <el-form-item prop="username">
            <el-input placeholder="请输入用户名" prefix-icon="el-icon-user" v-model="loginForm.username">
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input :type="passwordType" placeholder="请输入密码" prefix-icon="el-icon-lock" v-model="loginForm.password">
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-row :span="24">
              <el-col :span="12">
                <el-checkbox v-model="loginForm.rememberPwd">记住密码</el-checkbox>
              </el-col>
              <el-col :span="12">
                <el-popover placement="top-start" title="" width="200" trigger="hover" content="忘记密码请联系系统管理员">
                  <span style="color: #1890ff; float: right" slot="reference">忘记密码</span>
                </el-popover>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" style="width: 100%" @click.native.prevent="handleLogin" class="login-submit">
              登 录
            </el-button>
          </el-form-item>
          <el-form-item>
            <span @click="toWxLogin" class="weixinLoginSpan">
              <i style="color: #00CC00;" class="iconfont icon-weixindenglu"></i>
              微信
            </span>
            <span style="float: right">
              没有账号？<el-button type="text" @click="turnToRegist()">点击注册</el-button>
            </span>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div style="display: flex; width: 100%; height: 100%; overflow: hidden" v-if="pageType == 1">
      <div class="regist-modal">
        <div class="title" style="height: 75px; line-height: 85px;font-weight: 650;text-align: center;font-size: 28px;">
          注 册
        </div>
        <el-form class="login-form" :rules="loginRules" ref="registForm" :model="registForm" label-width="0">
          <el-form-item prop="username">
            <el-input placeholder="请输入用户名" prefix-icon="el-icon-user" v-model="registForm.username"
              @change="usernameCheck()">
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input :type="passwordType" placeholder="请输入密码" prefix-icon="el-icon-lock" v-model="registForm.password">
            </el-input>
          </el-form-item>
          <el-form-item prop="mail">
            <el-input placeholder="请输入邮箱" prefix-icon="el-icon-message" v-model="registForm.mail">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-row :span="24">
              <el-col :span="18">
                <el-input placeholder="请输入验证码" prefix-icon="el-icon-lock" v-model="registForm.code">
                </el-input>
              </el-col>
              <el-col :span="6">
                <el-button type="text" style="float: right" @click="sendCode()">{{ sendCodeMes }}</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="handleRegist()" class="login-submit">
              注册
            </el-button>
          </el-form-item>
          <el-form-item>
            <span style="float: right">
              已有账号？<el-button type="text" @click="turnToLogin()">点击登陆</el-button>
            </span>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>
  
<script>
import { usernameCheck, sendMailCode, regist } from "@/api/login/user";
import enterpriseApi from "@/api/enterprise/enterprise";

export default {
  name: "logintwo",
  data() {
    return {
      loading: true,
      pageType: 0,
      passwordType: "password",

      ////登录
      loginForm: {
        //用户名
        username: "admin",
        //密码
        password: "123456",
        rememberPwd: false,
      },
      loginRules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 1, message: "密码长度最少为6位", trigger: "blur" },
        ],
      },

      ////注册
      registForm: {
        //用户名
        username: "",
        //密码
        password: "",
        //邮箱
        mail: "",
        code: ""
      },
      sendCodeMes: "发送验证码",
      //倒计时
      countdownNum: 60,
      interval: {},
      isCount: 0,
      //公司选项
      enterpriseOptions: [],

      //背景图片
      backgroundDiv: {
        backgroundImage:
          "url(" + require("@/assets/login/img/渐变背景.jpg") + ")",
      },
    };
  },
  mounted() {
    let that = this;
    setTimeout(function () {
      that.loading = false;
    }, 10);
  },
  created() {
    // 获取路径token里面的值
    this.token = this.$route.query.token
    if (this.token) {//有值才判断
      this.wxLogin(this.token);
    } else if (this.$route.query.message) {
      this.$message.error("该微信还未绑定智能排班系统的账号，请通过账号密码登录")
    }
  },
  methods: {
    //跳转到微信登录页面
    toWxLogin() {
      //跳转到登录扫描页面
      location.href = 'http://localhost:8160/api/ucenter/wx/login'
    },
    showPassword() {
      this.passwordType === ""
        ? (this.passwordType = "password")
        : (this.passwordType = "");
    },
    wxLogin(token) {
      this.$store
        .dispatch("user/weixinLogin", token)
      this.$router.push({ path: this.redirect || "/" });
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.$store
            .dispatch("user/login", this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || "/" });
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    turnToLogin() {
      this.pageType = 0;
    },
    //跳转到注册
    turnToRegist() {
      // this.$router.push({ path: '/regist' })
      // this.$router.push("/regist");
      this.pageType = 1;
    },
    //处理注册
    handleRegist() {
      regist(this.registForm).then((res) => {
        //提示
        this.$message({
          type: "success",
          message: "注册成功，请登录系统",
        });
        this.pageType = 0;
      });
    },
    //用户名查重
    usernameCheck() {
      // console.log("hduashdusahdush");
      usernameCheck(this.registForm.username).then((res) => {
        // console.log("res:" + JSON.stringify(res));
        if (res.isExist == true) {
          //提示
          this.$message({
            type: "error",
            message: "用户名重复，请修改用户名!",
          });
        }
      });
    },
    //发送验证码
    sendCode() {
      if (this.registForm.mail == "") {
        //提示
        this.$message({
          type: "error",
          message: "请先输入邮箱，再发送验证码",
        });
      } else {
        if (this.isCount == 0) {
          this.sendCodeMes = this.countdownNum + "后再发送";
          this.isCount = 1;
          this.interval = setInterval(this.countdown, 1000);
          sendMailCode(this.registForm.mail).then((res) => { });
        }
      }
    },
    //倒计时
    countdown() {
      this.countdownNum--;
      if (this.countdownNum <= 0) {
        //停止运行计时
        clearInterval(this.interval);
        this.isCount = 0;
        this.countdownNum = 60;
        this.sendCodeMes = "发送验证码";
      } else {
        this.sendCodeMes = this.countdownNum + "后再发送";
      }
    },
  },
};
</script>
  
  
<style scoped>
.weixinLoginSpan:hover {
  border: 2px solid #00CC00;
  border-radius: 5px;
  padding: 5px;
}

#logintwo {
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center center;
  overflow: auto;
  position: fixed;
  line-height: 100%;
}

.logo {
  width: auto;
  height: 60px;
  display: flex;
  overflow: hidden;
  background-color: #1fb494;
}

.logo img {
  width: 32px;
  height: 32px;
  margin: 14px 10px 0 15px;
}

.logo .title {
  font-size: 16px;
  text-overflow: ellipsis;
  color: white;
  line-height: 61px;
}

.top-box {
  height: 60px;
  display: flex;
  background-color: #1fb494;
}

.top-box-i {
  width: 32px;
  height: 32px;
  font-size: 24px;
  margin: 16px 10px 0 15px;
  color: #ffffff;
}

.avue-home {
  /*background-color: #303133;*/
  background-color: rgba(39, 51, 59, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.avue-home__main {
  user-select: none;
  width: 100%;
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.avue-home__footer {
  width: 100%;
  flex-grow: 0;
  text-align: center;
  padding: 1em 0;
}

.avue-home__footer>a {
  font-size: 12px;
  color: #ababab;
  text-decoration: none;
}

.avue-home__loading {
  height: 32px;
  width: 32px;
  margin-bottom: 20px;
}

.avue-home__title {
  color: #fff;
  font-size: 14px;
  margin-bottom: 10px;
}

.avue-home__sub-title {
  color: #ababab;
  font-size: 12px;
}

::-webkit-scrollbar {
  width: 7px;
  height: 7px;
  display: none;
  background-color: transparent;
}

::-webkit-scrollbar-thumb {
  border-radius: 5px;
  background-color: rgba(39, 51, 59, 1);
}

::-webkit-scrollbar-track-piece {
  background-color: transparent;
}

a {
  color: #ffffff;
  text-decoration: none;
}

.name {
  line-height: 50px;
  font-size: 30px;
  font-weight: 700;
  color: #ffffff;
  margin-left: 10px;
}

.login-modal {
  position: relative;
  width: 420px;
  height: 450px;
  margin: 0 auto;
  top: 50%;
  margin-top: -225px;
  background-color: #ffffff;
  border-radius: 5px;
}

.regist-modal {
  position: relative;
  width: 500px;
  height: 550px;
  margin: 0 auto;
  top: 50%;
  margin-top: -280px;
  background-color: #ffffff;
  border-radius: 5px;
}

.login-form {
  margin: 20px 40px;
}
</style>
  
  
  