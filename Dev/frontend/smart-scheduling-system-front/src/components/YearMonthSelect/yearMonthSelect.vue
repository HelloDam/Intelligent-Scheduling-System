<template>
    <div>
        <span v-if="type === 0 || type === 1">
            <el-select v-if="type === 0 || type === 1" v-model="year" clearable filterable placeholder="年份"
                style="width: 80px;" size="mini" @change="sendChangeEvent()">
                <el-option v-for="item in yearOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            年
        </span>
        <span v-if="type === 0 || type === 2">
            <el-select v-model="month" clearable filterable placeholder="月份" style="width: 70px;" size="mini"
                @change="sendChangeEvent()">
                <el-option v-for="item in monthOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            月
        </span>

    </div>
</template>

<script>
export default {
    name: 'SmartSchedulingSystemMonthSelect',

    props: {
        // 0：年月都使用 1：只使用年 2：只使用月
        type: Number
    },

    data() {
        return {
            year: undefined,
            month: undefined,
            yearOptions: [],
            monthOptions: [
                { value: 1 },
                { value: 2 },
                { value: 3 },
                { value: 4 },
                { value: 5 },
                { value: 6 },
                { value: 7 },
                { value: 8 },
                { value: 9 },
                { value: 10 },
                { value: 11 },
                { value: 12 },
            ],
        };
    },

    mounted() {

    },

    created() {
        this.getCurMonth();
        for (let i = 1970; i <= 4000; i++) {
            this.yearOptions.push({ value: i });
        }
    },

    methods: {
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
            this.$emit('yearMonthChange');
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