<template>
  <div class="positionContainer">
    <div class="contentDiv">
      <!-- <div style="background: #d3e6fb; border-radius: 5px; padding: 1px"> -->
      <!-- 工具条 -->
      <!-- <div class="tools-div">
        <el-button type="success" icon="el-icon-plus" size="mini" @click="add()">添 加</el-button>
      </div> -->
      <div class="titleDiv">
        <span style="margin-right: 20px; margin-left: 15px; font-weight: bold">职位列表</span>
        <el-input style="width: 200px; margin-right: 10px" v-model="dataForm.name" placeholder="职位名称" clearable
          size="small"></el-input>
        <el-input style="width: 200px; margin-right: 10px" v-model="dataForm.detail" placeholder="职位描述" clearable
          size="small"></el-input>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchData()">搜索</el-button>
      </div>
      <!-- <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button> -->
      <div class="toolDiv">
        <el-button type="text" size="mini" @click="add()">
          <i class="iconfont icon-tianjia" style="margin-right: 3px"></i>添加</el-button>
      </div>
      <div class="tableDisplayDiv">
        <div class="tableDiv">
          <el-table :data="positionList" class="positionListTable" row-key="id" :default-expand-all="false"
            :tree-props="{ children: 'children' }" style="width: 100%">
            <el-table-column prop="name" label="名称" width="auto" />
            <el-table-column prop="description" label="职位描述" width="auto" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="updateTime" label="修改时间" />
            <el-table-column label="操作" align="center" fixed="right" width="350">
              <template slot-scope="scope">
                <el-button type="text" class="button-success" v-if="scope.row.type !== 2" icon="el-icon-plus" size="mini"
                  @click="add(scope.row)">添加下级节点</el-button>
                <el-button type="text" id="button-primary" icon="el-icon-edit" size="mini"
                  @click="edit(scope.row)">修改</el-button>
                <el-button type="text" id="button-primary" size="mini" @click="showAppointMemberList(scope.row.id)"
                  :disabled="scope.row.children.length > 0"><span
                    class="iconfont icon-huichangfuwu3-01"></span>指定成员</el-button>
                <el-button type="text" id="button-danger" icon="el-icon-delete" size="mini"
                  @click="removeDataById(scope.row.id)" :disabled="scope.row.children.length > 0">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <!-- 添加职位 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
        <el-form ref="dataForm" :model="position" label-width="9.375rem" size="small" style="padding-right: 2.5rem">
          <el-form-item label="上级职位" v-if="position.id === ''">
            <el-input v-model="position.parentName" :disabled="true" />
          </el-form-item>
          <el-form-item label="职位名称" prop="name">
            <el-input v-model="position.name" />
          </el-form-item>
          <el-form-item label="职位描述" prop="description">
            <el-input v-model="position.description" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取 消</el-button>
          <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确 定</el-button>
        </span>
      </el-dialog>

      <!-- 给职位分配成员 -->
      <el-dialog title="指定成员" :visible.sync="appointMemberDialogVisible" width="40%"
        :before-close="handleCloseAppointMemberDialog">
        <div style="display: flex; justify-content: center">
          <el-transfer filterable filter-placeholder="请输入员工姓名" v-model="appointMemberArr" :titles="['未指定成员', '已指定成员']"
            :data="appointMemberData" :props="{
              key: 'id',
              label: 'name',
            }" @change="appointChange">
          </el-transfer>
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button @click="cancelAppointMemberData()">取 消</el-button>
          <el-button type="primary" @click="saveAppointMemberData()">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>
  
<script>
import positionApi from "@/api/enterprise/position";
import userPositionApi from "@/api/enterprise/userPosition";

const defaultForm = {
  id: "",
  parentId: "",
  name: "",
  description: "",
};
export default {
  // 定义数据
  data() {
    return {
      dataForm: {},
      searchObj: [], //查询关键词的对象
      positionList: [],
      expandKeys: [], // 需要自动展开的项

      typeDisabled: false,
      type0Disabled: false,
      type1Disabled: false,
      type2Disabled: false,
      dialogTitle: "",

      dialogVisible: false,
      position: defaultForm,
      saveBtnDisabled: false,

      ///指定成员
      appointMemberDialogVisible: false,
      appointMemberData: [],
      appointMemberArr: [],
      appointMemberPositionId: undefined,
    };
  },

  //当页面加载时获取数据
  created() {
    // this.iconList = iconList;
    //页面渲染之前执行
    this.fetchData();
  },

  methods: {
    //调用api层获取数据库中的数据
    fetchData() {
      positionApi
        .findNodes({ name: this.dataForm.name, detail: this.dataForm.detail })
        .then((response) => {
          //请求成功
          //response接口返回的数据
          console.log(response);
          this.positionList = response.data;
          // console.log(this.positionList);
        });
    },

    //根据id删除数据
    removeDataById(id) {
      // debugger
      this.$confirm("此操作将永久删除该记录, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // promise
          // 点击确定，远程调用ajax
          return positionApi.removeById(id);
        })
        .then((response) => {
          this.fetchData();
          this.$message({
            type: "success",
            message: response.message,
          });
        })
        .catch(() => {
          this.$message.info("取消删除");
        });
    },

    //弹出添加或更新的表单
    add(row) {
      // debugger
      this.typeDisabled = false;
      this.dialogTitle = "添加下级节点";
      this.dialogVisible = true;

      this.position = Object.assign({}, defaultForm);
      this.position.id = "";
      if (row) {
        this.position.parentId = row.id;
        this.position.parentName = row.name;
      } else {
        this.dialogTitle = "添加根职位";
        this.position.parentId = 0;
        this.typeDisabled = true;
      }
    },

    //编辑
    edit(row) {
      // debugger
      this.dialogTitle = "修改菜单";
      this.dialogVisible = true;

      this.position = Object.assign({}, row);
      this.typeDisabled = true;
    },

    //添加或更新
    saveOrUpdate() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.saveBtnDisabled = true; // 防止表单重复提交
          if (!this.position.id) {
            this.save();
          } else {
            this.update();
          }
        }
      });
    },

    //添加
    save() {
      positionApi.save(this.position).then((response) => {
        this.$message.success(response.message || "操作成功");
        this.dialogVisible = false;
        this.fetchData(this.page);
      });
    },

    //更新
    update() {
      positionApi.updateById(this.position).then((response) => {
        this.$message.success(response.message || "操作成功");
        this.dialogVisible = false;
        this.fetchData();
      });
    },

    ////指定成员
    //所指定的成员列表发生变化
    appointChange() {
      // console.log(JSON.stringify(this.appointMemberArr))
    },
    //查询职位可以选择的成员和已经指定的成员
    showAppointMemberList(positionId) {
      this.appointMemberDialogVisible = true;
      this.appointMemberPositionId = positionId + "";
      positionApi
        .listMemberListAndAppointMemberIdList(positionId)
        .then((res) => {
          // console.log("res:" + JSON.stringify(res))
          this.appointMemberData = res.data.userInfoVoList;
          this.appointMemberArr = res.data.appointUserIdList;
          // console.log("appointMemberData:"+JSON.stringify(this.appointMemberData))
        });
    },
    //取消指定成员
    cancelAppointMemberData() {
      this.appointMemberData = [];
      this.appointMemberArr = [];
      this.appointMemberDialogVisible = false;
    },
    //关闭指定成员
    handleCloseAppointMemberDialog() {
      this.cancelAppointMemberData();
    },
    //保存指定成员的数据
    saveAppointMemberData() {
      let data = {
        positionId: this.appointMemberPositionId,
        userIdArr: this.appointMemberArr,
      };
      userPositionApi.saveAppointMemberData(data).then((res) => {
        if ((res.code = this.ResultCode.success)) {
          this.$message.success("保存成功");
          this.appointMemberDialogVisible = false;
          this.appointMemberData = [];
          this.appointMemberArr = [];
        } else {
          this.$message.error("保存失败，请重试");
        }
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.positionContainer {
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