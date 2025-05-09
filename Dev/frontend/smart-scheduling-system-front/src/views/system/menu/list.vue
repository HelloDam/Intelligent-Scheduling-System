<template>
  <div class="menuContainer">
    <div class="menuDiv">
      <div class="titleDiv">
        <span style="margin-right: 20px;margin-left:15px;font-weight: bold;">菜单管理</span>
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter.native="handleQuery"
          style="width: 150px;margin-right: 10px;" size="small" />
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable size="small"
          style="width: 150px;margin-right: 10px;">
          <el-option v-for="menuStatus in menuStatusOptions" :key="menuStatus.value" :label="menuStatus.label"
            :value="menuStatus.value" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <!-- <el-button icon="el-icon-refresh" size="mini" @click="getTreeExpandedKeys()">测试</el-button> -->
      </div>

      <div class="toolDiv">
        <el-button type="text" size="small" @click="add()">
          <i class="iconfont icon-tianjia" style="margin-right: 3px;"></i>添加一级菜单</el-button>
        <el-button id="button-info" type="text" icon="el-icon-sort" size="small"
          @click="toggleExpandAll()">展开/折叠</el-button>
      </div>

      <div class="tableDiv">
        <el-table ref="menuTable" :data="sysMenuList" style="width: 100%;" height="100%" row-key="id"
          :default-expand-all="isExpandAll" v-if="refreshTable" :tree-props="{ children: 'children' }"
          @expand-change="handleExpandChange">
          <!-- <el-table-column label="" width="80">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column> -->
          <!-- <el-table-column type="expand"></el-table-column> -->
          <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true" width="160">
          </el-table-column>
          <el-table-column label="图标" width="60" align="center">
            <template slot-scope="scope">
              <i :class="scope.row.icon"></i>
            </template>
          </el-table-column>
          <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="path" label="路由地址" :show-overflow-tooltip="true" />
          <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" />
          <el-table-column prop="sort" label="排序" />
          <el-table-column label="状态">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.status === 0">正常</el-tag>
              <el-tag type="danger" v-if="scope.row.status === 1">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="150" />
          <el-table-column prop="updateTime" label="修改时间" width="150" />
          <el-table-column label="操作" width="240" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" id="button-primary" v-if="scope.row.type !== 2" icon="el-icon-plus" size="mini"
                @click="add(scope.row)">添加子节点</el-button>
              <el-button type="text" id="button-primary" icon="el-icon-edit" size="mini"
                @click="edit(scope.row)">修改</el-button>
              <el-button type="text" id="button-danger" icon="el-icon-delete" size="mini"
                @click="removeDataById(scope.row.id)" :disabled="scope.row.children.length > 0">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- <pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="queryParams.pageNum" :page-sizes="[10, 20, 30, 40]" layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </pagination> -->

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form ref="dataForm" :model="sysMenu" label-width="150px" size="small" style="padding-right: 40px">
        <el-form-item label="上级菜单" v-if="sysMenu.id === ''">
          <el-input v-model="sysMenu.parentName" :disabled="true" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="sysMenu.type" :disabled="typeDisabled">
            <el-radio :label="0" :disabled="type0Disabled">目录</el-radio>
            <el-radio :label="1" :disabled="type1Disabled">菜单</el-radio>
            <el-radio :label="2" :disabled="type2Disabled">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="sysMenu.name" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="sysMenu.type !== 2">
          <el-select v-model="sysMenu.icon" clearable filterable>
            <el-option v-for="item in iconList" :key="item.class" :label="item.class" :value="item.class">
              <span style="float: left">
                <!-- <span v-if="(item.class+'').indexOf('iconfont')!=-1" :class="item.class"></span>
                <i v-else :class="item.class"></i> -->
                <i :class="item.class"></i>
                <!-- 如果动态显示图标，这里添加判断 -->
              </span>
              <span style="padding-left: 6px">{{ item.class }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="sysMenu.sort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item prop="path">
          <span slot="label">
            <el-tooltip content="访问的路由地址，如：`sysUser`" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            路由地址
          </span>
          <el-input v-model="sysMenu.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item prop="component" v-if="sysMenu.type !== 0">
          <span slot="label">
            <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            组件路径
          </span>
          <el-input v-model="sysMenu.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="sysMenu.type === 2">
          <el-input v-model="sysMenu.perms" placeholder="请输入权限标识" maxlength="100" />
          <span slot="label">
            <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(hasAuthority('bnt.sysRole.list'))" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            权限字符
          </span>
        </el-form-item>
        <el-form-item label="状态" prop="type">
          <el-radio-group v-model="sysMenu.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
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
import api from "@/api/system/menu";
import elTableUtil from '@/utils/common/elTableUtil'
// import backgroundCss from '@/styles/background/backgroundCss.css'

const defaultForm = {
  id: "",
  parentId: "",
  name: "",
  type: 0,
  path: "",
  component: "",
  perms: "",
  icon: "",
  sortValue: 1,
  status: 0,
};
export default {
  // 定义数据
  data() {
    return {

      // 查询参数
      queryParams: {
        menuName: undefined,
        status: undefined
      },

      //菜单状态选项
      menuStatusOptions: [
        { value: "0", label: "正常" },
        { value: "1", label: "停用" }
      ],


      sysMenuList: [],
      expandKeys: [], // 需要自动展开的项
      //是否展开树
      isExpandAll: false,
      // 重新渲染表格状态
      refreshTable: true,
      ///自适应宽度
      nameWidth: 80,
      permsWidth: 80,
      pathWidth: 80,
      componentWidth: 80,

      //分页
      total: 100,
      page: 1,
      limit: 10,

      typeDisabled: false,
      type0Disabled: false,
      type1Disabled: false,
      type2Disabled: false,
      dialogTitle: "",

      dialogVisible: false,
      sysMenu: defaultForm,
      saveBtnDisabled: false,

      iconList: []
    };
  },

  //当页面加载时获取数据
  created() {
    //页面渲染之前执行
    this.fetchData();
    this.iconList = this.IconList.iconList;
  },
  mounted() {
    // console.log("执行钩子函数");
    // this.flexAllColumnWidth();
  },

  methods: {
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.queryParams.pageSize = val;
      this.queryParams.pageNum = 1
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.queryParams.pageNum = val
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.fetchData();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.menuId) {
        this.form.parentId = row.menuId;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加菜单";
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },

    //调用api层获取数据库中的数据
    fetchData() {
      // console.log("加载列表");
      api.findNodes(this.queryParams).then((response) => {
        this.sysMenuList = response.data;
        // console.log(this.sysMenuList);
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
          return api.removeById(id);
        })
        .then((response) => {
          this.fetchData();
          this.$message({
            type: "success",
            message: "删除成功",
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

      this.sysMenu = Object.assign({}, defaultForm);
      this.sysMenu.id = "";
      if (row) {
        this.sysMenu.parentId = row.id;
        this.sysMenu.parentName = row.name;
        //this.sysMenu.component = 'ParentView'
        if (row.type === 0) {
          this.sysMenu.type = 1;
          this.typeDisabled = false;
          this.type0Disabled = false;
          this.type1Disabled = false;
          this.type2Disabled = true;
        } else if (row.type === 1) {
          this.sysMenu.type = 2;
          this.typeDisabled = true;
        }
      } else {
        this.dialogTitle = "添加目录节点";
        this.sysMenu.type = 0;
        this.sysMenu.component = "Layout";
        this.sysMenu.parentId = 0;
        this.typeDisabled = true;
      }
    },

    //编辑
    edit(row) {
      // debugger
      this.dialogTitle = "修改菜单";
      this.dialogVisible = true;

      this.sysMenu = Object.assign({}, row);
      this.typeDisabled = true;
    },

    //添加或更新
    saveOrUpdate() {
      if (this.sysMenu.type === 0 && this.sysMenu.parentId !== 0) {
        this.sysMenu.component = "ParentView";
      }
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.saveBtnDisabled = true; // 防止表单重复提交
          if (!this.sysMenu.id) {
            this.save();
          } else {
            this.update();
          }
        }
      });
    },

    //添加
    save() {
      api.save(this.sysMenu).then((response) => {
        this.$message.success(response.message || "操作成功");
        this.dialogVisible = false;
        this.fetchData(this.page);
      });
    },

    //更新
    update() {
      api.updateById(this.sysMenu).then((response) => {
        this.$message.success(response.message || "操作成功");
        this.dialogVisible = false;
        this.fetchData();
      });
    },

    ////自适应表格列宽
    //自适应多有列的宽度
    flexAllColumnWidth() {
      this.nameWidth = this.flexColumnWidth('name', this.sysMenuList);
      this.permsWidth = this.flexColumnWidth('perms', this.sysMenuList);
      this.pathWidth = this.flexColumnWidth('path', this.sysMenuList);
      this.componentWidth = this.flexColumnWidth('component', this.sysMenuList);
    },
    //自适应表格列宽
    flexColumnWidth(fieldName, data, flag = 'max') {
      console.log("执行flexColumnWidth");
      let expandedItems = this.$refs.menuTable.getTreeExpandedKeys();
      return elTableUtil.flexColumnWidth(fieldName, data, expandedItems, flag)
    },
    getTreeExpandedKeys() {
      let expandedItems = this.$refs.menuTable.getTreeExpandedKeys();
      console.log("当前展开项：" + JSON.stringify(expandedItems));
    },
    //树形表格展开项发生变化
    handleExpandChange() {
      this.refreshTable = false;
      this.refreshTable = true;
      this.$nextTick(() => {
        // this.flexAllColumnWidth();
      });
    }
  },
};
</script>

<style lang="scss" scoped>
.menuContainer {
  padding: 20px;
  // background: rgb(243, 5, 183);
  height: 100%;

  .menuDiv {
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

    .tableDiv {
      background: #FFFFFF;
      // height: 300px;
      height: calc(100% - 35px - 40px);
     
    }


  }


}
</style>