<template>
    <div>
        <el-tree ref="positionSelectTree" :props="positionProps" :data="positionSelectTree" show-checkbox node-key="id"
            :default-checked-keys="positionIdArr" :default-expand-all="true" @check="checkChange()">
        </el-tree>
    </div>
</template>

<script>
import positionApi from '@/api/enterprise/position';

export default {
    name: 'SmartSchedulingSystemPositionTree',

    props: {
        isExpand: {
            type: Boolean
        },
        isSelectAll: {
            type: Boolean
        }
    },

    data() {
        return {
            ////职位选择
            //职位选择树
            positionSelectTree: [],
            //选中的职位
            positionIdArr: [],
            //展示的内容对应
            positionProps: {
                label: 'label',
                children: 'children'
            },
        };
    },

    mounted() {

    },

    created() {
        this.initPositionTree();
       
    },
    mounted(){
        this.selectAllOrNot();
    },

    watch: {
        isExpand: {
            handler(newValue, oldValue) {
                //监听到展开、折叠变化，执行方法
                this.foldOrExpand()
            },
            // 如果设置了false，那么在页面第一次渲染以后不会触发侦听器
            // immediate: true,
            // 深度监听，可以监听对象里面的属性变化
            // deep: true
        },
        isSelectAll: {
            handler(newValue, oldValue) {
                //监听到展开、折叠变化，执行方法
                this.selectAllOrNot()
            },
        }
    },

    methods: {
        ////职位选择
        //初始化职位树
        initPositionTree() {
            positionApi.getPositionSelectTree().then(res => {
                this.positionSelectTree = res.positionTree;
                // for (let i = 0; i < this.positionSelectTree.length; i++) {
                //     this.positionIdArr.push(this.positionSelectTree[i].id);
                // }
                // console.log("this.positionIdArr:"+JSON.stringify(this.positionIdArr))
            })
        },
        //是否全选
        selectAllOrNot() {
            this.isSelectAll = !this.isSelectAll;
            // console.log("isSelectAll:" + this.isSelectAll)
            if (this.isSelectAll === true) {
                for (let i = 0; i < this.positionSelectTree.length; i++) {
                    this.positionIdArr.push(this.positionSelectTree[i].id);
                }
                this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
                this.checkChange();
            } else {
                this.positionIdArr.length = 0;
                this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
                this.checkChange();
            }
            // console.log("this.positionIdArr:" + JSON.stringify(this.positionIdArr))
        },
        // 一键折叠/展开
        foldOrExpand() {
            // this.isExpand = !this.isExpand
            this.expandFunc(this.positionSelectTree)
        },
        //是否全选
        selectAllOrNot() {
            if (this.isSelectAll === true) {
                for (let i = 0; i < this.positionSelectTree.length; i++) {
                    this.positionIdArr.push(this.positionSelectTree[i].id);
                }
                this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
            } else {
                this.positionIdArr.length = 0;
                this.$refs.positionSelectTree.setCheckedKeys(this.positionIdArr);
            }
            this.$emit('positionIdArrChange', this.positionIdArr);
        },
        // 遍历树形数据，通过设置每一项的expanded属性，实现展开与折叠
        expandFunc(data) {
            data.forEach(item => {
                this.$refs.positionSelectTree.store.nodesMap[item.id].expanded = this.isExpand
                if (item.children != null && item.children.length > 0) {
                    this.expandFunc(item.children)
                }
            })
        },
        //勾选内容发生改变
        checkChange() {
            this.positionIdArr = this.$refs.positionSelectTree.getCheckedKeys();
            this.$emit('positionIdArrChange', this.positionIdArr);
        },
        //获取所选中的职位id数据
        getPositionIdArr() {
            return this.positionIdArr;
        }
    },
};
</script>

<style lang="scss" scoped></style>