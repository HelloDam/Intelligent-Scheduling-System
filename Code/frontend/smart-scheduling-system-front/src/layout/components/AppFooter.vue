<template>
    <div class="by-app-footer">
        Copyright&nbsp;&nbsp;&copy;2023&nbsp;&nbsp;智能排班系统&nbsp;&nbsp;All&nbsp;Rights&nbsp;Reserved
    </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
    computed: {
        ...mapState('webSocket', ['socketData']),
    },
    data() {
        return {

        }
    },
    watch: {

    },
    methods: {

    }
}
</script>

<style scoped lang="scss"></style>
<style lang="scss">
@import "~@/styles/variables.scss";

.by-app-footer {
    height: 20px;
    width: 100%;
    text-align: center;
    font-size: 12px;
    line-height: 20px;
    color: #808080;
    letter-spacing: 1px;
    background: white;
}
</style>
