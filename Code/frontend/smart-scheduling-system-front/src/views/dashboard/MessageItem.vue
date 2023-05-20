<template>
    <div>
        <div class="message-component">
            <div class="avatar">
                <el-avatar shape="square" size="medium" :src="messageItemVo.publishAvatar"></el-avatar>
            </div>
            <div class="message-details">
                <div class="message-header">
                    <!-- <h3 class="username">{{ messageItemVo.publishName }}</h3> -->
                    <span class="usernameSpan">{{ messageItemVo.publishName }}</span>
                    <span class="timestamp">{{ messageItemVo.publishTime }}</span>
                </div>
                <messageBubble :mesCotent="messageItemVo.content">
                </messageBubble>
                <!-- <div class="message-body" v-html="messageItemVo.content">
                </div> -->
            </div>
        </div>

    </div>
</template>

<script>
import messageBubble from './MessageBubble.vue';

export default {
    name: 'SmartSchedulingSystemMessageItem',

    props: {
        messageItemVo: {
            type: Object
        }
    },

    components: {
        messageBubble
    },

    data() {
        return {

        };
    },

    created(){
        // console.log("messageItemVo:"+JSON.stringify(this.messageItemVo))
    },

    mounted() {

    },

    methods: {

    },
};
</script>

<style lang="scss" scoped>
.message-component {
    display: flex;
    align-items: flex-start;
    /* 修改对齐方式为左上角 */
    padding: 10px;
    // border: 1px solid #ccc;
    margin: 10px;
    padding: 10px;
    border-radius: 5px;
    // background-color: #f0f1f2;
    // background: rgba(255, 255, 255, 1);
}

.avatar {
    margin-right: 10px;
}


.message-details {
    flex: 1;
}

.message-header {
    display: flex;
    justify-content: space-between;
    // align-items: center;
    margin-bottom: 5px;
}

.username {
    // margin-right: 10px;
}

.usernameSpan{
    font-size: 16px;
    font-weight: bold;
}

.timestamp {
    font-size: 14px;
    color: #666;
}

.message-body {
    font-size: 16px;
    line-height: 1.5;
}
</style>