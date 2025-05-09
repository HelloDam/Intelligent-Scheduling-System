<template>
  <el-dialog
    title="选择员工"
    :visible.sync="personSelectVisible"
    :modal="false"
    width="80%"
    center
    :before-close="handleClose"
  >
    <el-row :gutter="20">
      <!-- 选择门店 -->
      <el-col :span="4" :xs="24">
        <div style="background: #d3e6fb; border-radius: 5px; padding: 1px">
          <div style="padding: 5px">
            <span
              style="margin-right: 20px; margin-left: 15px; font-weight: bold"
              >职位选择</span
            >
            <el-button
              id="button-black"
              style="font-weight: bold"
              type="text"
              icon="iconfont icon-ziyuan"
              v-if="isSelectAll == false"
              size="mini"
              @click="selectAllOrNot()"
            ></el-button>
            <el-button
              id="button-black"
              style="font-weight: bold"
              type="text"
              icon="iconfont icon-ziyuan1"
              v-if="isSelectAll == true"
              size="mini"
              @click="selectAllOrNot()"
            ></el-button>
            <el-button
              id="button-black"
              style="font-weight: bold"
              type="text"
              icon="iconfont icon-expand"
              v-if="isExpand == false"
              size="mini"
              @click="foldOrExpand()"
            ></el-button>
            <el-button
              id="button-black"
              style="font-weight: bold"
              type="text"
              icon="iconfont icon-zhedie"
              v-if="isExpand == true"
              size="mini"
              @click="foldOrExpand()"
            ></el-button>
          </div>
          <div style="background: #ffffff; height: 100%">
            <div style="padding: 10px;0px;0px;10px;">
              <positionTree
                :isExpand="isExpand"
                :isSelectAll="isSelectAll"
                @positionIdArrChange="positionIdArrChange()"
                ref="positionTree"
              >
              </positionTree>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="20" :xs="24">
        <div style="background: #d3e6fb; border-radius: 5px; padding: 1px">
          <div style="padding: 5px">
            <span
              style="margin-right: 20px; margin-left: 15px; font-weight: bold"
              >用户列表</span
            >

            <el-input
              style="width: 230px; margin-right: 10px"
              v-model="searchObj.keyword"
              placeholder="用户名/姓名/手机号码/邮箱"
              clearable
              size="small"
            >
            </el-input>

            <el-select
              clearable
              style="width: 150px; margin-right: 10px"
              v-model="searchObj.busyStatus"
              placeholder="请选择用户状态"
              size="small"
              v-if="personType == 1"
            >
              <el-option
                v-for="item in busyOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>

            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="fetchData()"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetData"
              >重置</el-button
            >
          </div>

          <div style="background: #ffffff; padding: 1px; padding-left: 20px">
            <span v-if="personType == 1"> 指定方式：</span>
            <el-switch
              v-model="isReplaceStaff"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="替换"
              inactive-text="追加"
              style="margin-right: 20px"
              v-if="personType == 1"
            >
            </el-switch>
            <el-button
              id="button-primary"
              type="text"
              icon="iconfont icon-huichangfuwu3-01"
              :disabled="selectUserList.length <= 0"
              size="medium"
              @click="appointMemberForShift()"
              >指定人员</el-button
            >
            <el-button
              id="button-danger"
              type="text"
              icon="iconfont icon-qingkong"
              :disabled="selectUserList.length <= 0"
              size="medium"
              @click="emptyMemberList()"
              >清空人员</el-button
            >
          </div>

          <!-- 列表 -->
          <!-- @selection-change="handleSelectionChange"  -->
          <!-- @select='onSelect' @select-all="selectAll" -->
          <el-table
            :data="userList"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            ref="memberTable"
            :key="memberTableKey"
            v-model="selectedRows"
            :row-key="getRowKey"
          >
            <el-table-column
              type="selection"
              width="50"
              align="center"
              :reserve-selection="true"
            >
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
            <!-- <el-table-column prop="nickname" label="昵称" width="150" /> -->
            <el-table-column prop="name" label="姓名" width="150" />
            <!-- <el-table-column prop="phone" label="手机" width="150" /> -->
            <el-table-column prop="positionName" label="职位" width="100" />
            <el-table-column
              prop="enterpriseName"
              label="企业名称"
              width="150"
            />
            <el-table-column prop="storeName" label="门店名称" width="150" />
            <el-table-column prop="age" label="年龄" />
            <el-table-column label="性别">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.gender == 0">男</el-tag>
                <el-tag type="danger" v-if="scope.row.gender == 1">女</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" v-if="this.personType == 1">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.isBusy == false">空 闲</el-tag>
                <el-tag type="danger" v-if="scope.row.isBusy == true"
                  >繁 忙</el-tag
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="page"
          :limit.sync="limit"
          @pagination="fetchData"
        />
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
// 组件
import positionTree from "@/views/enterprise/store/position/positionTree.vue";
import { getToken } from "@/utils/auth";
import userApi from "@/api/system/user";
import { getInfo } from "@/api/login/user";

export default {
  name: "UserSelect",
  components: { positionTree },
  data() {
    return {
      ////职位选择
      positionIdArr: [],
      isExpand: true,
      isSelectAll: false,
      sonPositionOptions: [],

      ////所登陆的用户信息
      userInfo: {},

      ////用户数据查询
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象
      busyOptions: [
        { value: "0", label: "空闲" },
        { value: "1", label: "繁忙" },
      ],
      //多选数组
      selectUserList: [],
      // 表格数据
      userList: [],
      // 选中的用户集合
      selectUserList: [],
      // 选中的用户id
      userIdList: [],
      // 单选框
      selectedId: null,
      selectedRow: null,
      // 传入班次信息
      shiftdata: undefined,
      ///翻页勾选
      selectedRows: [],
      currentPageSelectedRows: [],
      memberTableKey: 0,

      ////是否显示
      personSelectVisible: false,

      ////指定用户
      //true：替换班次原本的员工 false：给班次追加员工
      isReplaceStaff: false,
      //0：替换班次原本的员工 1：给班次追加员工
      appointType: 0,
    };
  },

  created() {
    //获取用户信息
    this.getUserInfo();
  },

  props: {
    //0：普通选择人员 1：为班次指定人员
    personType: {
      type: Number,
    },
    taskPeriod: {
      type: Object,
    }
  },

  watch: {
    personSelectVisible: {
      handler(newVal, oldVal) {
        // console.log('变化')
        // console.log(newVal, oldVal)
        this.shiftdata = this.$parent.linetask;
        // console.log("this.shiftdata:" + JSON.stringify(this.shiftdata))
      },
      deep: true,
      immediate: true,
    },
  },

  methods: {
    ////职位选择
    // 一键折叠/展开
    foldOrExpand() {
      this.isExpand = !this.isExpand;
    },
    //是否全选
    selectAllOrNot() {
      this.isSelectAll = !this.isSelectAll;
    },
    /**
     * 职位选择发生变化
     */
    positionIdArrChange() {
      this.positionIdArr = this.$refs.positionTree.getPositionIdArr();
      this.fetchData();
    },

    ////查询用户
    //列表
    fetchData(pageObject = { page: this.page, limit: this.limit }) {
      // console.log("page:" + JSON.stringify(pageObject))
      this.page = pageObject.page;
      this.limit = pageObject.limit;
      // console.log("this.userInfo:" + JSON.stringify(this.userInfo));
      this.searchObj.userType = this.userInfo.userType;
      this.searchObj.enterpriseId = this.userInfo.enterpriseId;
      if (
        this.shiftdata != undefined &&
        this.shiftdata.length > 0 &&
        this.personType == 1
      ) {
        this.searchObj.isNeedSearchBusyStatus = true;
        // console.log("班次信息this.shiftdata:" + JSON.stringify(this.shiftdata));
        this.searchObj.shiftStartDate = this.shiftdata[0].startDate;
        this.searchObj.shiftEndDate = this.shiftdata[0].endDate;
        // console.log("班次信息:" + JSON.stringify(this.searchObj));
      }
      if (this.createTimes && this.createTimes.length == 2) {
        this.searchObj.createTimeBegin = this.createTimes[0];
        this.searchObj.createTimeEnd = this.createTimes[1];
      }
      this.searchObj.positionIdArr = this.positionIdArr;
      if(this.taskPeriod){
        this.searchObj.startDate = this.taskPeriod.startDate;
        this.searchObj.endDate = this.taskPeriod.endDate;
      }
      // console.log(this.taskPeriod)
      // console.log('当前测试位置')
      // console.log("this.searchObj:" + JSON.stringify(this.searchObj));
      userApi
        .getPageList(this.page, this.limit, this.searchObj)
        .then((response) => {
          // console.log("response:" + JSON.stringify(response));
          this.userList = response.page.list;
          this.total = response.page.totalCount;
        });
    },
    // 重置查询表单
    resetData() {
      console.log("重置查询表单");
      this.searchObj = {};
      this.createTimes = [];
      this.fetchData();
    },

    ///翻页勾选
    getRowKey(row) {
      return row.id;
    },
    /**
     * 选择的数据发送改变执行
     * @param {*} val
     */
    handleSelectionChange(val) {
      this.selectUserList = val;
    },
    /**
     * 清空员工
     */
    emptyMemberList() {
      this.clearMemberList();
      if (this.personType == 0) {
        //--if--根据员工查询日历信息才需要通知父组件
        this.$emit("userIdListChange", this.userIdList);
      }
      this.$message.success("清空成功");
    },
    /**
     * 清空已选择员工列表
     */
    clearMemberList() {
      this.selectUserList = [];
      this.memberTableKey = new Date().getTime();
    },
    /**
     * 获取所选中的员工id
     */
    getSelectedUserIdList() {
      let ids = [];
      for (let i = 0; i < this.selectUserList.length; i++) {
        ids.push(this.selectUserList[i].id);
      }
      return ids;
    },
    /**
     * 获取所选中的员工
     */
    getSelectedUserList() {
      return this.selectUserList;
    },
    //获取用户信息
    getUserInfo() {
      const token = getToken();
      getInfo(token).then((res) => {
        this.userInfo = res.data;
        // console.log("this.userInfo:"+JSON.stringify(this.userInfo ))
        this.fetchData();
      });
    },

    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },

    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },

    /** 搜索操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置操作 */
    resetQuery() {
      this.dateRange = [];
      this.$refs["queryForm"].resetFields();
      this.handleQuery();
    },

    // 选中数据
    handleChange(row) {
      if (row) {
        this.selectedRow = row;
      }
    },

    handleRowChange(row) {
      if (row) {
        this.selectedRow = row;
      }
    },

    /**
     * 给班次指定员工
     */
    appointMemberForShift() {
      this.personSelectVisible = false;
      this.userIdList = this.getSelectedUserIdList();
      // console.log(this.getChooseIds())
      
      this.$emit("userIdListChange");
    },

    /**
     * 获取指定方式
     */
    getAppointType() {
      return this.isReplaceStaff == true ? 0 : 1;
    },

    /**
     * 关闭选择人员弹框清理已选数据
     */
    handleClose(){
      this.personSelectVisible = false
      this.isSelectAll= false
      // this.positionIdArr = []
      this.clearMemberList()
    }
  },
};
</script>

<style>
.tree-container {
  padding: 8px 24px;
  background: #eef1f6;
}
</style>
