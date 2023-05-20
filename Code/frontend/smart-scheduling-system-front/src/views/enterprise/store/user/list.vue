<template>
  <div class="storeUsercontainer">
    <el-row :gutter="10" style="height: 100%;">
      <el-col :span="4" :xs="24" class="colDiv">
        <div class="contentDiv">
          <div class="titleDiv" style="  justify-content: space-between;">
            <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">职位选择</span>
            <span>
              <el-button id="button-black" style="font-weight: bold;" type="text" icon="iconfont icon-ziyuan"
                v-if="isSelectAll == false" size="mini" @click="selectAllOrNot()"></el-button>
              <el-button id="button-black" style="font-weight: bold;" type="text" icon="iconfont icon-ziyuan1"
                v-if="isSelectAll == true" size="mini" @click="selectAllOrNot()"></el-button>
              <el-button id="button-black" style="font-weight: bold;" type="text" icon="iconfont icon-expand"
                v-if="isExpand == false" size="mini" @click="foldOrExpand()"></el-button>
              <el-button id="button-black" style="font-weight: bold;" type="text" icon="iconfont icon-zhedie"
                v-if="isExpand == true" size="mini" @click="foldOrExpand()"></el-button>
            </span>
          </div>
          <div style="background: #FFFFFF;height:calc(100% - 45px) ;">
            <div style="padding: 10px;0px;0px;10px;">
              <positionTree :isExpand="isExpand" :isSelectAll="isSelectAll" @positionIdArrChange="positionIdArrChange()"
                ref="positionTree">
              </positionTree>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="20" :xs="24" class="colDiv">
        <div class="contentDiv">
          <div class="titleDiv">
            <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">用户列表</span>
            <el-input style="width: 250px;margin-right: 10px;" v-model="searchObj.keyword" placeholder="用户名/姓名/手机号码/邮箱"
              clearable size="small"></el-input>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchData()">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
          </div>

          <div class="toolDiv">
            <el-button type="text" size="small" @click="add">
              <i class="iconfont icon-tianjia" style="margin-right: 3px;"></i>添加</el-button>
            <el-button id="button-danger" type="text" icon="el-icon-delete" :disabled="multipleSelection.length <= 0"
              size="small" @click="removeBatch()">批量删除</el-button>
          </div>

          <div class="tableDisplayDiv">
            <div class="tableDiv">
              <!-- 列表 -->
              <el-table v-loading="listLoading" :data="list" stripe style="width: 100%; "
                @selection-change="handleSelectionChange" height="100%">
                <el-table-column type="selection" width="50" align="center">
                </el-table-column>
                <el-table-column label="序号" width="70" align="left" fixed>
                  <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                  </template>
                </el-table-column>

                <el-table-column prop="username" label="用户名" fixed width="120" />

                <!-- <el-table-column label="头像" width="60">
              <template slot-scope="scope">
                <el-avatar shape="square" size="medium" :src="scope.row.avatar"></el-avatar>
              </template>
            </el-table-column> -->
                <el-table-column prop="nickname" label="昵称" width="150" />
                <el-table-column prop="phone" label="手机" width="150" />
                <el-table-column prop="mail" label="邮箱地址" width="180" />
                <el-table-column prop="positionName" label="职位" width="100" />
                <el-table-column prop="enterpriseName" label="企业名称" width="150" />
                <el-table-column prop="storeName" label="门店名称" width="150" />
                <el-table-column prop="qq" label="QQ" width="150" />
                <el-table-column prop="age" label="年龄" />
                <el-table-column label="性别">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.gender == 0">男</el-tag>
                    <el-tag type="danger" v-if="scope.row.gender == 1">女</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="账号状态" width="80">
                  <template slot-scope="scope">
                    <el-switch v-model="scope.row.status === 0" @change="switchStatus(scope.row)">
                    </el-switch>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="150" />
                <el-table-column prop="updateTime" label="修改时间" width="150" />
                <el-table-column label="操作" align="center" fixed="right" width="220">
                  <template slot-scope="scope">
                    <el-button type="text" icon="el-icon-edit" id="button-primary" size="mini"
                      @click="edit(scope.row.id)">修改</el-button>
                    <el-button type="text" icon="el-icon-delete" id="button-danger" size="mini"
                      @click="removeDataById(scope.row.id)">删除</el-button>
                    <el-button type="text" icon="el-icon-baseball" id="button-primary" size="mini"
                      @click="showAssignRole(scope.row)">分配角色</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <pagination v-show="total > 0" :total="total" :page.sync="page" :limit.sync="limit"
              @pagination="fetchData()" />
          </div>
        </div>
      </el-col>
    </el-row>


    <el-dialog title="分配角色" :visible.sync="dialogRoleVisible">
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input disabled :value="sysUser.username"></el-input>
        </el-form-item>

        <el-form-item label="角色列表">
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
          <div style="margin: 15px 0"></div>
          <el-checkbox-group v-model="userRoleIds" @change="handleCheckedChange">
            <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">{{ role.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="assignRole" size="small">保存</el-button>
        <el-button @click="dialogRoleVisible = false" size="small">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配门店" :visible.sync="dialogStoreVisible" width="40%">
      <el-form label-width="80px;">
        <!-- <el-form-item label="用户名">
          <el-input disabled :value="sysUser.username"></el-input>
        </el-form-item> -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="门店列表">
              <el-select v-model="sysUser.storeId" filterable placeholder="请选择门店" @change="storeChange()">
                <el-option v-for="item in storeOptions" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位列表">
              <el-select v-model="sysUser.positionId" filterable placeholder="请选择职位">
                <el-option v-for="item in sonPositionOptions" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="saveStore()" size="small">保存</el-button>
        <el-button @click="dialogStoreVisible = false" size="small">取消</el-button>
      </div>
    </el-dialog>

    <!-- 添加/修改用户 -->
    <el-dialog :title="userTitle" :visible.sync="dialogVisible" width="40%">
      <el-form ref="dataForm" :model="sysUser" label-width="80px" size="small" style="padding-right: 40px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="sysUser.username" />
        </el-form-item>
        <el-form-item v-if="!sysUser.id" label="密码" prop="password">
          <el-input v-model="sysUser.password" type="password" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="sysUser.name" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="sysUser.phone" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId" v-if="userInfo.userType == 0">
          <el-select v-model="sysUser.enterpriseId" filterable placeholder="请选择">
            <el-option v-for="item in enterpriseOptions" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型" prop="type" v-if="userInfo.userType == 1">
          <el-select v-model="sysUser.type" filterable placeholder="请选择用户类型">
            <el-option v-for="item in userTypeOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取 消</el-button>
        <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import userApi from "@/api/system/user";
import roleApi from "@/api/system/role";
import { getToken } from "@/utils/auth";
import { getInfo } from "@/api/login/user";
import enterpriseApi from "@/api/enterprise/enterprise";
import storeApi from "@/api/enterprise/store";
// 组件
import positionTree from "@/views/enterprise/store/position/positionTree.vue";
import positionApi from '@/api/enterprise/position'

export default {
  components: { positionTree },
  data() {
    return {
      ////所登陆的用户信息
      userInfo: {},

      ////企业信息
      enterpriseOptions: [],
      //添加还是修改
      userTitle: "添加用户",
      listLoading: false, // 数据是否正在加载
      list: null, // 列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象

      createTimes: [],

      dialogVisible: false,
      sysUser: {},
      //用户类型
      userTypeOptions: [{ value: 2, label: "门店管理员" }, { value: 10, label: "普通用户" }],
      //多选数组
      multipleSelection: [],

      dialogRoleVisible: false,
      allRoles: [], // 所有角色列表
      userRoleIds: [], // 用户的角色ID的列表
      isIndeterminate: false, // 是否是不确定的
      checkAll: false, // 是否全选

      ////分配门店
      dialogStoreVisible: false,
      //门店列表
      storeOptions: [],

      ////职位选择
      positionIdArr: [],
      isExpand: true,
      isSelectAll: false,
      sonPositionOptions: []
    };
  },
  mounted() {
  },
  created() {
    //获取用户信息
    this.getUserInfo();
  },
  //监听数据变化
  watch: {
    // positionIdArr: {
    //   handler(newValue, oldValue) {
    //     console.Console("哈哈哈哈")
    //     this.fetchData()
    //   },
    //   // 如果设置了false，那么在页面第一次渲染以后不会触发侦听器
    //   // immediate: true,
    //   // 深度监听，可以监听对象里面的属性变化
    //   // deep: true
    // }
  },
  methods: {
    /**
     * 职位选择发生变化
     */
    positionIdArrChange() {
      this.positionIdArr = this.$refs.positionTree.getPositionIdArr();
      this.fetchData();
    },

    /**
     * 查询出企业的所有门店
     * @param {} row 
     */
    showAssignStore(row) {
      this.dialogStoreVisible = true
      //调用接口用户的门店信息
      userApi.userInfoVo(row.id).then((response) => {
        this.sysUser = response.data;
        // console.log("编辑用户,sysUser：" + JSON.stringify(this.sysUser))
        //查询门店信息
        this.listAllSonPosition();
        // this.sysUser.positionId = this.sysUser.positionId + "";

      });
      //查询企业所对应的门店有哪些
      storeApi.listAllStoreByEnterpriseId().then(res => {
        this.storeOptions = res.list
      })
    },

    /**
     * 修改所选择的门店
     */
    storeChange() {
      // console.log("切换门店")
      this.listAllSonPosition();
      this.sysUser.positionId = undefined
    },

    /**
     * 根据门店id查询出所有叶子节点数据
     */
    listAllSonPosition() {
      positionApi.listAllSonPosition(this.sysUser.storeId).then(
        res => {
          this.sonPositionOptions = res.sonPositionList;
          // console.log("sonPositionOptions" + JSON.stringify(this.sonPositionOptions))
        }
      )
    },

    //保存商店信息
    saveStore() {
      this.saveOrUpdate()
      this.dialogStoreVisible = false
    },

    //展示分配角色
    showAssignRole(row) {
      this.sysUser = row;
      this.dialogRoleVisible = true;
      roleApi.getRolesByUserId(row.id).then((response) => {
        this.allRoles = response.data.allRoles;
        console.log(this.allRoles);
        this.userRoleIds = response.data.userRoleIds;
        console.log(this.userRoleIds);
        this.checkAll = this.userRoleIds.length === this.allRoles.length;
        this.isIndeterminate =
          this.userRoleIds.length > 0 &&
          this.userRoleIds.length < this.allRoles.length;
      });
    },

    /*
    全选勾选状态发生改变的监听
    */
    handleCheckAllChange(value) {
      // value 当前勾选状态true/false
      // 如果当前全选, userRoleIds就是所有角色id的数组, 否则是空数组
      this.userRoleIds = value ? this.allRoles.map((item) => item.id) : [];
      // 如果当前不是全选也不全不选时, 指定为false
      this.isIndeterminate = false;
    },

    /*
    角色列表选中项发生改变的监听
    */
    handleCheckedChange(value) {
      const { userRoleIds, allRoles } = this;
      this.checkAll =
        userRoleIds.length === allRoles.length && allRoles.length > 0;
      this.isIndeterminate =
        userRoleIds.length > 0 && userRoleIds.length < allRoles.length;
    },

    //分配角色
    assignRole() {
      let assginRoleVo = {
        userId: this.sysUser.id,
        roleIdList: this.userRoleIds,
      };
      roleApi.assignRoles(assginRoleVo).then((response) => {
        this.$message.success(response.message || "分配角色成功");
        this.dialogRoleVisible = false;
        this.fetchData();
      });
    },
    //更改用户状态
    switchStatus(row) {
      //判断，如果当前用户可用，修改禁用
      row.status = row.status === 1 ? 0 : 1;
      userApi.updateStatus(row.id, row.status).then((response) => {
        this.$message.success(response.message || "操作成功");
        this.fetchData();
      });
    },
    //删除
    removeDataById(id) {
      this.$confirm("此操作将永久删除该用户, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //调用方法删除
        userApi.removeById(id).then((response) => {
          //提示
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          //刷新
          this.fetchData();
        });
      });
    },
    /**
     * 批量删除
     * @param {*} id 
     */
    removeBatch() {
      this.$confirm("此操作将永久删除该批用户, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //调用方法删除
        userApi.deleteBatch(this.getChooseIds()).then((response) => {
          //提示
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          //刷新
          this.fetchData();
        });
      });
    },
    //根据id查询，数据回显
    edit(id) {
      this.userTitle = "修改用户";
      //弹出框
      this.dialogVisible = true;
      //调用接口查询
      userApi.getUserId(id).then((response) => {
        this.sysUser = response.data;
      });
    },
    //添加或者修改方法
    saveOrUpdate() {
      if (!this.sysUser.id) {
        this.save();
      } else {
        this.update();
      }
    },
    //修改
    update() {
      userApi.update(this.sysUser).then((response) => {
        //提示
        this.$message.success("操作成功");
        //关闭弹框
        this.dialogVisible = false;
        //刷新
        this.fetchData();
      });
    },
    //添加
    save() {
      if (this.userInfo.userType == 0) {
        //--if--系统管理员创建的是企业管理员
        this.sysUser.type = 1;
      } else if (this.userInfo.userType == 1) {
        //--if--企业管理员可以创建门店管理员、普通用户
        this.sysUser.type = 2;
        //设置一般用户的企业id
        this.sysUser.enterpriseId = this.userInfo.enterpriseId;
      } else if (this.userInfo.userType == 2) {
        //--if--门店管理员可以创建普通用户
        this.sysUser.type = 10;
        //设置一般用户的企业id
        this.sysUser.enterpriseId = this.userInfo.enterpriseId;
      }
      userApi.save(this.sysUser).then((response) => {
        //提示
        this.$message.success("操作成功");
        //关闭弹框
        this.dialogVisible = false;
        //刷新
        this.fetchData();
      });
    },
    //添加弹框的方法
    add() {
      this.userTitle = "添加用户"
      this.sysUser = {};
      this.dialogVisible = true;
      this.listEnterprise();
    },
    // 重置查询表单
    resetData() {
      console.log("重置查询表单");
      this.searchObj = {};
      this.createTimes = [];
      this.fetchData();
    },
    //列表
    fetchData(pageObject = { "page": this.page, "limit": this.limit }) {
      this.listLoading = true;
      // console.log("page:" + JSON.stringify(pageObject))
      this.page = pageObject.page;
      this.limit = pageObject.limit;
      // console.log("this.userInfo:" + JSON.stringify(this.userInfo));
      this.searchObj.userType = this.userInfo.userType;
      this.searchObj.enterpriseId = this.userInfo.enterpriseId;
      if (this.createTimes && this.createTimes.length == 2) {
        this.searchObj.createTimeBegin = this.createTimes[0];
        this.searchObj.createTimeEnd = this.createTimes[1];
      }
      this.searchObj.positionIdArr = this.positionIdArr;
      // console.log("this.searchObj:" + JSON.stringify(this.searchObj));
      userApi
        .getPageList(this.page, this.limit, this.searchObj)
        .then((response) => {
          this.listLoading = false;
          // console.log("response:" + JSON.stringify(response));
          this.list = response.page.list;
          this.total = response.page.totalCount;
        });
    },
    /**
     * 选择的数据发送改变执行
     * @param {*} val 
     */
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    /**
    * 获取所选中的任务id
    */
    getChooseIds() {
      let ids = [];
      for (let i = 0; i < this.multipleSelection.length; i++) {
        ids.push(this.multipleSelection[i].id);
      }
      return ids;
    },

    //获取用户信息
    getUserInfo() {
      const token = getToken();
      getInfo(token).then((res) => {
        this.userInfo = res.data;
        // console.log("this.userInfo:"+JSON.stringify(this.userInfo ))
      });
    },
    //查询企业列表
    listEnterprise() {
      enterpriseApi.listAll().then((res) => {
        this.enterpriseOptions = res.list;
      });
    },

    ////职位选择
    // 一键折叠/展开
    foldOrExpand() {
      this.isExpand = !this.isExpand
    },
    //是否全选
    selectAllOrNot() {
      this.isSelectAll = !this.isSelectAll;
    },
  },
};
</script>
<style lang="scss" scoped>
.storeUsercontainer {
  padding: 10px 10px 0px 10px;
  height: 100%;
  // background: #1989FA;

  .colDiv {
    height: calc(100vh - 70px - 10px - 1px);
    // background: #1989FA;
  }

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

.titleDiv {
  display: flex;

  align-items: center;
  padding: 5px 15px 5px 0px;
}
</style>

