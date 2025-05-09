<template>
  <div class="roleAuthorizationContainer">
    <div class="contentDiv">
      <div class="titleDiv" style="justify-content: space-between;">
        <span>
          <span style="margin-right: 5px;margin-left:15px;font-weight: bold;">角色授权</span>
          <span style="margin-right: 20px;margin-left:15px;">(角色：{{ $route.query.name }})</span>
        </span>
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

      <div class="toolDiv">
        <el-button type="text" size="small" @click="save" :loading="loading">
          <i class="iconfont icon-tianjia" style="margin-right: 3px;"></i>保存</el-button>
        <el-button id="button-danger" type="text" icon="el-icon-refresh-right" size="small"
          @click="$router.push('/enterprise/organization/role')">返回</el-button>
      </div>

      <div class="treeDisplayDiv">
        <el-tree style="padding-left: 10px;padding-top:10px" ref="tree" :data="sysMenuList" node-key="id" show-checkbox
          default-expand-all :props="defaultProps" />
      </div>
    </div>

  </div>
</template>
<script>
import api from '@/api/system/menu'
export default {
  name: 'roleAuth',

  data() {
    return {
      loading: false, // 用来标识是否正在保存请求中的标识, 防止重复提交
      sysMenuList: [], // 所有
      defaultProps: {
        children: 'children',
        label: 'name'
      },

      ////权限选择
      isExpand: true,
      isSelectAll: false,
    };
  },

  created() {
    this.fetchData()
  },

  methods: {
    /*
    初始化
    */
    fetchData() {
      const roleId = this.$route.query.id
      api.toAssign(roleId).then(result => {
        const sysMenuList = result.data
        this.sysMenuList = sysMenuList
        const checkedIds = this.getCheckedIds(sysMenuList)
        // console.log('getPermissions() checkedIds', checkedIds)
        if (checkedIds.length > 0) {
          this.isSelectAll = true;
        } else {
          this.isSelectAll = false;
        }
        this.$refs.tree.setCheckedKeys(checkedIds)
      })
    },

    /*
    得到所有选中的id列表
    */
    getCheckedIds(auths, initArr = []) {
      return auths.reduce((pre, item) => {
        if (item.select && item.children.length === 0) {
          pre.push(item.id)
        } else if (item.children) {
          this.getCheckedIds(item.children, initArr)
        }
        return pre
      }, initArr)
    },

    /*
    保存权限列表
    */
    save() {
      //获取到当前子节点
      //const checkedNodes = this.$refs.tree.getCheckedNodes()
      //获取到当前子节点及父节点
      const allCheckedNodes = this.$refs.tree.getCheckedNodes(false, true);
      let idList = allCheckedNodes.map(node => node.id);
      console.log(idList)
      let assginMenuVo = {
        roleId: this.$route.query.id,
        menuIdList: idList
      }
      this.loading = true
      api.doAssign(assginMenuVo).then(result => {
        this.loading = false
        this.$message.success(result.$message || '分配权限成功')
        this.$router.push('/enterprise/organization/role');
      })
    },

    //是否全选
    selectAllOrNot() {
      this.isSelectAll = !this.isSelectAll;
      if (this.isSelectAll === true) {
        let selectItemArr = [];
        for (let i = 0; i < this.sysMenuList.length; i++) {
          selectItemArr.push(this.sysMenuList[i].id);
        }
        this.$refs.tree.setCheckedKeys(selectItemArr);
      } else {
        this.$refs.tree.setCheckedKeys([]);
      }
      // console.log("this.positionIdArr:" + JSON.stringify(this.positionIdArr))
    },
    // 一键折叠/展开
    foldOrExpand() {
      this.isExpand = !this.isExpand;
      this.expandFunc(this.sysMenuList);
    },
    // 遍历树形数据，通过设置每一项的expanded属性，实现展开与折叠
    expandFunc(data) {
      data.forEach((item) => {
        this.$refs.tree.store.nodesMap[item.id].expanded =
          this.isExpand;
        if (item.children != null && item.children.length > 0) {
          this.expandFunc(item.children);
        }
      });
    },
  }
};
</script>
<style lang="scss" scoped>
.roleAuthorizationContainer {
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

    .treeDisplayDiv {
      background: #FFFFFF;
      // height: 300px;
      height: calc(100% - 35px - 45px);
      //添加垂直滚动条
      overflow-y: auto;
      overflow-x: hidden;
    }

    /* 定义滚动条的轨道和滑块样式 */
    .treeDisplayDiv:hover::-webkit-scrollbar-track {
      background-color: #f1f1f1;
    }

    .treeDisplayDiv:hover::-webkit-scrollbar-thumb {
      background-color: #D2D2D2;
      border-radius: 5px;
    }

    .treeDisplayDiv:hover::-webkit-scrollbar-button {
      background-color: #D2D2D2;
      height: 5px;
    }

    /* 隐藏滚动条 */
    .treeDisplayDiv::-webkit-scrollbar {
      // 隐藏滚动条宽度
      // width: 0 !important;
      // 隐藏滚动条高度
      // height: 0 !important;
      // 隐藏滚动条背景
      background-color: transparent;
      width: 10px;
      height: 5px;
    }

    /* 鼠标进来的时候显示滚动条 */
    .treeDisplayDiv:hover::-webkit-scrollbar {
      // background-color: #f1f1f1;
      width: 10px;
      height: 5px;
    }



  }
}
</style>