<template>
    <div >
        <el-tooltip class="item" effect="dark" content="上一月" placement="top">
            <el-button icon="el-icon-arrow-up" circle @click="handleLast" class="button" size="mini"></el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="回到本月" placement="top">
            <el-button @click="back2CurMonth" class="button" size="mini"><span class="dateDiv">本月</span></el-button>
        </el-tooltip>

        <el-tooltip class="item" effect="dark" content="下一月" placement="top">
            <el-button icon="el-icon-arrow-down" circle @click="handleNext" class="button" size="mini"></el-button>
        </el-tooltip>
    </div>
</template>

<script>
export default {
    name: 'SmartSchedulingSystemMonthSelect',

    data() {
        return {
            year: undefined,
            month: undefined
        };
    },

    mounted() {

    },

    created() {
        this.getCurMonth();
    },

    methods: {
        /**
                * 上一周
                */
        handleLast() {
            if (this.month > 1) {
                this.month--;
            } else {
                this.year--;
                this.month = 12;
            }
            this.sendChangeEvent();
            // console.log("year:" + this.year + ",month:" + this.month)
        },
        /**
         * 下一周
         */
        handleNext() {
            if (this.month < 12) {
                this.month++;
            } else {
                this.year++;
                this.month = 1;
            }
            this.sendChangeEvent();
            // console.log("year:" + this.year + ",month:" + this.month)
        },
        /**
       * 回到本月
       */
        back2CurMonth() {
            this.getCurMonth();
            // console.log("year:" + this.year + ",month:" + this.month)
            this.sendChangeEvent();
        },
        /**
         * 获取本月
         */
        getCurMonth() {
            let date = new Date();
            this.year = date.getFullYear();
            this.month = date.getMonth() + 1;
        },
        getYear() {
            return this.year;
        },
        getMonth() {
            return this.month;
        },
        sendChangeEvent() {
            this.$emit('monthChange');
        }
    },
};
</script>

<style lang="scss" scoped>
.dateDiv {
    // font-family: "SimHei", Arial, sans-serif;
    // font-weight: bold;
    /* 可选 */
    color: #235284;
}

.button {
    background: rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.2),
        inset 1px 1px 2px rgba(0, 0, 0, 0.1);
}
</style>