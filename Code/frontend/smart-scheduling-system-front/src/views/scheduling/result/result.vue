<template>
    <div class="container" id="bodyDiv">
        <!-- <el-tabs @tab-click="handleClick" class="tabs">
            <el-tab-pane label="月视图" class="tabPane">
                <el-tabs type="border-card" @tab-click="inHandleClick">
                    <el-tab-pane label="岗位">
                        <PostitionView :taskId="taskId" v-if="!isPerson"></PostitionView>
                    </el-tab-pane>
                    <el-tab-pane label="员工">
                        <PersonView :taskId="taskId" v-if="isPerson"></PersonView>
                    </el-tab-pane>
                </el-tabs>
            </el-tab-pane>
            <el-tab-pane label="周视图" class="tabPane">
                <WeeklyVieW :taskId="taskId" v-if="week"></WeeklyVieW>
            </el-tab-pane>
        </el-tabs> -->

        <div class="toolDiv">
            <span>
                <div :class="{ 'resultButton': true, 'viewSelect': viewSelect === 0 }" style="margin-right: 10px;"
                    @click="selectMonthView()">月视图</div>
                <div :class="{ 'resultButton': true, 'viewSelect': viewSelect === 1 }" @click="selectWeekView()">周视图</div>
            </span>
            <span class="pageTitle" v-if="viewSelect === 0 && typeSelect === 0">月 视 图<span
                    class="typeSpan">(按职位查询)</span></span>
            <span class="pageTitle" v-if="viewSelect === 0 && typeSelect === 1">月 视 图<span
                    class="typeSpan">(按员工查询)</span></span>
            <span class="pageTitle" v-if="viewSelect === 1">周 视 图</span>
            <span>
                <div v-if="viewSelect === 0">
                    <div :class="{ 'resultButton': true, 'viewSelect': typeSelect === 0 }" style="margin-right: 10px;"
                        @click="selectPostView()">岗位</div>
                    <div :class="{ 'resultButton': true, 'viewSelect': typeSelect == 1 }" @click="selectStaffView()">员工
                    </div>
                </div>
                <div v-if="viewSelect === 1">
                    <div style="width: 150px;"></div>
                </div>
            </span>
        </div>

        <div class="contentDiv">
            <PostitionView :taskId="taskId" v-if="viewSelect === 0 && typeSelect === 0"></PostitionView>
            <PersonView :taskId="taskId" v-if="viewSelect === 0 && typeSelect === 1"></PersonView>
            <WeeklyVieW :taskId="taskId" v-if="viewSelect === 1"></WeeklyVieW>
        </div>

    </div>
</template>

<script>
import SkillView from "./skill.vue"
import PostitionView from "./post.vue"
import PersonView from "./person.vue"
import WeeklyVieW from "./weekly.vue"
export default {
    components: { SkillView, PostitionView, PersonView, WeeklyVieW },
    props: {
        taskId: {
            type: Number
        },
    },
    created() {
        // if (this.$route.query.taskId) {
        //     this.taskId = this.$route.query.taskId;
        // }
    },
    data() {
        return {
            week: false,
            isPerson: false,
            //要查询结果的任务id
            // taskId: undefined

            //选择月视图还是日视图
            viewSelect: 0,
            //选择岗位还是员工
            typeSelect: 0,
        }
    },
    methods: {
        handleClick(tab, event) {
            console.log(tab, event);
            if (tab.index == 1) {
                this.week = true
            } else {
                this.week = false
            }
        },
        inHandleClick(tab, event) {
            console.log(tab, event);
            if (tab.index == 1) {
                this.isPerson = true
            } else {
                this.isPerson = false
            }
        },
        /**
         * 选择月视图
         */
        selectMonthView() {
            this.viewSelect = 0;
        },
        /**
         * 选择周视图
         */
        selectWeekView() {
            this.viewSelect = 1;
        },
        selectPostView() {
            this.typeSelect = 0;
        },
        selectStaffView() {
            this.typeSelect = 1;
        }
    }
}
</script>

<style lang="scss" scoped>
.container {
    padding: 10px;
    // background: rgb(243, 5, 183);
    height: 100%;

    .toolDiv {
        justify-content: space-between;
        display: flex;
        align-items: center;
        padding: 10px;
        height: 55px;
        border-radius: 5px;
        background: rgba(242, 247, 251, 0.6);
        backdrop-filter: blur(50px);
        box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
            inset 1px 1px 1px rgba(0, 0, 0, 0.1);

        .pageTitle {
            font-family: "SimHei", Arial, sans-serif;
            font-size: 20px;
            font-weight: bold;
            /* 可选 */
            color: #235284;

            .typeSpan {
                font-size: 16px;
                font-weight: normal;
                /* 可选 */
                color: #000000;
            }
        }

        .resultButton {
            display: inline-block;
            background-color: #8dcfec;
            border: none;
            // color: #3C4A56;
            color: #FFFFFF;
            text-align: center;
            font-size: 14px;
            padding: 10px;
            // width: 100px;
            // transition: all 0.5s;
            cursor: pointer;
            border-radius: 5px;
        }

        .viewSelect {
            background-color: #3982b8;
        }

        .resultButton:hover {
            // background-color: rgba(152, 188, 214, 1);
            background-color: #6abce2;
            transform: scale(1.05);
        }

    }

    .contentDiv {
        // 55px是toolDiv的高度 10px是间距
        height: calc(100% - 10px - 55px);
        // background: #3982b8;
    }

}

#bodyDiv {
    margin: 0;
    /* min-height: 100vh; */
    height: 100%;
    background-color: #c4d7fa;
    background-image: radial-gradient(closest-side, rgb(255, 255, 255), rgba(235, 105, 78, 0)), radial-gradient(closest-side, rgb(250, 203, 203), rgba(243, 11, 164, 0)), radial-gradient(closest-side, rgb(237, 252, 202), rgba(254, 234, 131, 0)), radial-gradient(closest-side, rgb(197, 248, 241), rgba(170, 142, 245, 0)), radial-gradient(closest-side, rgb(206, 200, 243), rgba(248, 192, 147, 0));
    background-size: 130vmax 130vmax, 80vmax 80vmax, 90vmax 90vmax, 110vmax 110vmax, 90vmax 90vmax;
    background-position: -80vmax -80vmax, 60vmax -30vmax, 10vmax 10vmax, -30vmax -10vmax, 50vmax 50vmax;
    background-repeat: no-repeat;
}</style>