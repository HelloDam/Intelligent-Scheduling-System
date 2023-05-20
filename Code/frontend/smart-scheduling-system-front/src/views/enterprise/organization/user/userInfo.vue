<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="用户昵称" prop="nickname">
      <el-input v-model="user.nickname" maxlength="30" />
    </el-form-item>
    <el-form-item label="用户姓名" prop="name">
      <el-input v-model="user.name" maxlength="30" />
    </el-form-item>
    <el-form-item label="手机号码" prop="phone">
      <el-input v-model="user.phone" maxlength="11" />
    </el-form-item>
    <el-form-item label="QQ" prop="qq">
      <el-input v-model="user.qq" maxlength="10" />
    </el-form-item>
    <el-form-item label="邮箱" prop="mail">
      <el-input v-model="user.mail" maxlength="50" />
    </el-form-item>
    <el-form-item label="年龄" prop="age">
      <el-input v-model="user.age" maxlength="3" />
    </el-form-item>
    <el-form-item label="微信" prop="openid">

      <div style="display: flex;border: 1.5px solid #DCDFE6;border-radius:3px;">
        <el-avatar shape="medium" :size="50" :src="user.wechatAvatar" style="margin: 5px;border-radius:3px;" v-if="user.openid != null"></el-avatar>
        <div style="margin-top: 10px;margin-left: 5px;width: :100%;" v-if="user.openid != null">{{ user.wechatName }}
        </div>
        <el-button type="text" @click="bindWechat()" v-if="user.openid == null">绑定微信</el-button>
      </div>

    </el-form-item>
    <el-form-item label="性别">
      <el-radio v-model="user.gender" label="0">男</el-radio>
      <el-radio v-model="user.gender" label="1">女</el-radio>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <!-- <el-button type="danger" size="mini" @click="close">关闭</el-button> -->
    </el-form-item>
  </el-form>
</template>

<script>
import userApi from '@/api/system/user'
import { getToken } from '@/utils/auth';

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    return {

      // 表单校验
      rules: {
        nickname: [
          // { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        mail: [
          // { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          {
            pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phone: [
          // { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ],
        age: [
          // { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            // 只能输入一至三位数字
            pattern: /^\d{1,3}$/,
            message: "请输入正确的年龄",
            trigger: "blur"
          }
        ],
      }
    };
  },
  created() {

  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          userApi.update(this.user).then(response => {
            this.$message.success("修改成功");
          });
        } else {
          this.$message.error("数据校验失败");
        }
      });
    },
    close() {
      this.$tab.closePage();
    },

    ////绑定微信
    bindWechat() {
      let path = 'http://localhost:8160/api/ucenter/wx/login?token=' + getToken()
      //跳转到登录扫描页面
      location.href = path;
    },
  }
};
</script>
