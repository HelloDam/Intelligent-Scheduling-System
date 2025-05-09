<template>
  <div class="roleContainer">
    <div class="contentDiv">
      <!--查询表单-->
      <!-- <div class="search-div">
        <el-form label-width="70px" size="small">
          <el-row>
            <el-col :span="24">
              <el-form-item label="角色名称">
                <el-input style="width: 100%" v-model="searchObj.name" placeholder="角色名称"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row style="display:flex">
            <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchData()">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
          </el-row>
        </el-form>
      </div> -->
      <div class="titleDiv">
        <span style="margin-right: 20px; margin-left: 15px; font-weight: bold">角色列表</span>
        <el-input v-model="searchObj.name" placeholder="角色名称" clearable style="width: 150px; margin-right: 10px"
          @keyup.enter.native="getDataList" size="small" />
        <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchData()">查询</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
      </div>
      <!-- 工具条 -->
      <!-- <div class="tools-div">
        <el-button type="success" icon="el-icon-plus" size="mini" @click="add">添 加</el-button>
        <el-button class="btn-add" size="mini" @click="batchRemove()">批量删除</el-button>
      </div> -->
      <!-- <div style="background: #FFFFFF;padding: 1px;padding-left: 20px;"> -->
      <div class="toolDiv">
        <el-button type="text" size="small" @click="add">
          <i class="iconfont icon-tianjia" style="margin-right: 3px"></i>添加</el-button>
        <el-button id="button-danger" type="text" icon="el-icon-delete" :disabled="selectValue.length <= 0" size="small"
          @click="batchRemove()">批量删除</el-button>
      </div>
      <!-- 表格 -->
      <div class="tableDisplayDiv">
        <div class="tableDiv">
          <el-table v-loading="listLoading" :data="list" style="width: 100%" height="100%"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" />

            <el-table-column label="序号" width="70" align="center">
              <template slot-scope="scope">
                {{ (page - 1) * limit + scope.$index + 1 }}
              </template>
            </el-table-column>

            <el-table-column prop="name" label="角色名称" />
            <!-- <el-table-column prop="roleCode" label="角色编码" /> -->
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column prop="updateTime" label="修改时间" width="160" />
            <el-table-column label="操作" width="200" align="center">
              <template slot-scope="scope">
                <el-button type="text" id="button-primary" icon="el-icon-edit" size="mini"
                  @click="edit(scope.row.id)">修改</el-button>
                <el-button type="text" id="button-danger" icon="el-icon-delete" size="mini"
                  @click="removeDataById(scope.row.id)">删除</el-button>
                <el-button type="text" id="button-primary" icon="el-icon-baseball" size="mini"
                  @click="showAssignAuth(scope.row)">分配权限</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- 分页组件 -->
        <!-- <el-pagination :current-page="page" :total="total" :page-size="limit" style="padding: 30px 0; text-align: center;"
       layout="total, prev, pager, next, jumper" @current-change="fetchData" /> -->

        <pagination v-show="total > 0" :total="total" :page.sync="page" :limit.sync="limit" @pagination="fetchData" />

      </div>
      <el-dialog title="添加/修改" :visible.sync="dialogVisible" width="40%">
        <el-form ref="dataForm" :model="sysRole" label-width="100px" size="small" style="padding-right: 40px">
          <el-form-item label="角色名称">
            <el-input v-model="sysRole.name" />
          </el-form-item>
          <!-- <el-form-item label="角色编码">
                <el-input v-model="sysRole.roleCode"/>
              </el-form-item> -->
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取 消</el-button>
          <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
//引入定义接口的js文件
import api from "@/api/system/role";

export default {
  //定义初始值
  data() {
    return {
      listLoading: false, //是否显示加载
      list: [], //角色列表
      total: 0, //总记录数
      page: 1, //当前页
      limit: 10, //每页显示记录数
      searchObj: {}, //条件查询封装对象
      dialogVisible: false, //弹出框
      sysRole: {}, //封装添加表单数据
      selectValue: [], //复选框选择内容封装数组
    };
  },
  //页面渲染之前执行
  created() {
    //调用列表方法
    this.fetchData();
  },
  methods: {
    //具体方法
    //跳转分配菜单权限
    showAssignAuth(row) {
      this.$router.push(
        "/enterprise/organization/assignAuth?id=" + row.id + "&name=" + row.name
      );
    },
    //复选框发生变化执行方法
    handleSelectionChange(selection) {
      this.selectValue = selection;
      //console.log(this.selectValue)
    },
    //批量删除
    batchRemove() {
      //判断
      if (this.selectValue.length == 0) {
        this.$message.warning("请选择要删除的记录！");
        return;
      }
      this.$confirm("此操作将永久删除该角色, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //数组
        var idList = [];
        //获取多个复选框对应id，封装到数组里面
        // [1,2,3]
        for (var i = 0; i < this.selectValue.length; i++) {
          var obj = this.selectValue[i];
          //id值
          var id = obj.id;
          //放到数组里面
          idList.push(id);
        }
        api.batchRemove(idList).then((response) => {
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
    //修改-数据回显
    edit(id) {
      //弹出框
      this.dialogVisible = true;
      api.getRoleId(id).then((response) => {
        console.log("response.data:" + JSON.stringify(response));
        this.sysRole = response.data;
      });
    },
    //点击确定
    saveOrUpdate() {
      //判断添加还是修改
      if (!this.sysRole.id) {
        //添加
        this.saveRole();
      } else {
        this.updateRole();
      }
    },
    //修改的方法
    updateRole() {
      api.update(this.sysRole).then((response) => {
        //提示
        this.$message({
          type: "success",
          message: "修改成功!",
        });
        //关闭弹框
        this.dialogVisible = false;
        //刷新页面
        this.fetchData();
      });
    },
    //添加的方法
    saveRole() {
      api.saveRole(this.sysRole).then((response) => {
        //提示
        this.$message({
          type: "success",
          message: "添加成功!",
        });
        //关闭弹框
        this.dialogVisible = false;
        //刷新页面
        this.fetchData();
      });
    },
    //点击添加，弹出框
    add() {
      this.dialogVisible = true;
      this.sysRole = {};
    },
    //删除
    removeDataById(id) {
      this.$confirm("此操作将永久删除该角色, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //调用方法删除
        api.removeId(id).then((response) => {
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
    //重置
    resetData() {
      //清空表单
      this.searchObj = {};
      //查询所有数据
      this.fetchData();
    },
    //条件分页查询列表
    //pageNum 查询页数
    fetchData(pageNum = 1) {
      //页数赋值
      this.page = pageNum;
      //ajax
      let params = {
        page: this.page,
        limit: this.limit,
        name: this.searchObj.name,
      };
      api.getPageList(params).then((response) => {
        //this.listLoading = false
        // console.log("response:"+JSON.stringify(response))
        //每页数据列表
        this.list = response.page.list;
        //总记录数
        this.total = response.page.totalCount;
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.roleContainer {
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
</style>