<template>
  <div class="storeContainer">
    <div class="contentDiv">
      <div class="titleDiv">
        <span style="margin-right: 20px; margin-left: 15px; font-weight: bold">门店列表</span>
        <el-input v-model="dataForm.key" placeholder="参数名" clearable style="width: 150px; margin-right: 10px"
          @keyup.enter.native="getDataList" size="small" />
        <el-button type="primary" icon="el-icon-search" size="mini" @click="getDataList()">查询</el-button>
      </div>
      <div class="toolDiv">
        <el-button type="text" size="small" @click="addOrUpdateHandle()">
          <i class="iconfont icon-tianjia" style="margin-right: 3px"></i>添加</el-button>
        <el-button id="button-danger" type="text" icon="el-icon-delete" size="small"
          :disabled="dataListSelections.length <= 0" @click="deleteHandle()">批量删除</el-button>
      </div>
      <!-- <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">查询</el-button>
          <el-button type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        </el-form-item>
      </el-form> -->
      <div class="tableDisplayDiv">
        <div class="tableDiv">
          <el-table :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandle"
            style="width: 100%" height="100%">
            <el-table-column type="selection" header-align="center" align="center" width="50">
            </el-table-column>
            <el-table-column prop="name" header-align="center" align="center" label="名称">
            </el-table-column>
            <el-table-column prop="provinceName" header-align="center" align="center" label="省">
            </el-table-column>
            <el-table-column prop="cityName" header-align="center" align="center" label="市">
            </el-table-column>
            <el-table-column prop="regionName" header-align="center" align="center" label="区">
            </el-table-column>
            <el-table-column prop="address" header-align="center" align="center" label="详细地址" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="size" header-align="center" align="center" label="工作场所面积">
            </el-table-column>
            <!-- <el-table-column prop="status" header-align="center" align="center" label="0：营业中 1：休息中（默认0）">
             </el-table-column> -->
            <el-table-column prop="createTime" header-align="center" align="center" label="创建时间">
            </el-table-column>
            <el-table-column prop="updateTime" header-align="center" align="center" label="更新时间">
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
                <el-button type="text" size="small" id="button-danger" @click="deleteHandle(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination v-show="totalPage > 0" :total="totalPage" :page.sync="pageIndex" :limit.sync="pageSize"
          @pagination="getDataList" />
      </div>
      <!-- <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage"
          layout="total, sizes, prev, pager, next, jumper">
        </el-pagination> -->
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </div>
</template>

<script>
import AddOrUpdate from "./store-add-or-update";
import storeApi from "@/api/enterprise/store";
export default {
  data() {
    return {
      dataForm: {
        key: "",
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getDataList();
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        key: this.dataForm.key,
      };
      storeApi.list(params).then((res) => {
        // console.log(JSON.stringify(res))
        if (res.code === this.ResultCode.success) {
          this.dataList = res.page.list;
          this.totalPage = res.page.totalCount;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
          return item.id;
        });
      this.$confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      ).then(() => {
        storeApi.deleteBatch(ids).then((res) => {
          if (res.code === this.ResultCode.success) {
            this.getDataList();
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
  },
};
</script>

<!-- <style scoped>
.mod-config {
  margin: 20px;
}
</style> -->
<style lang="scss" scoped>
.storeContainer {
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
