<template>
    <div class="message">
        <div class="message-bubble">
            <span v-html="mesCotent"></span>
        </div>
    </div>
</template>

<script>
export default {
    name: 'SmartSchedulingSystemMessageBubble',

    props: {
        mesCotent: {
            type: String
        }
    },

    data() {
        return {
            // content:"dsadghuasdhisaodhsaodhao"
        };
    },

    created() {
        // console.log("content:" + this.content)
    },

    mounted() {

    },

    methods: {

    },
};
</script>

<style lang="scss" scoped>
.message {
    display: flex;
    flex-direction: row;
    align-items: flex-end;
    margin-bottom: 10px;
}

.message-bubble {
    background: rgba(255, 255, 255, 1);
    color: #333;
    padding: 10px;
    border-radius: 10px;
}

.message-bubble p {
    margin: 0;
}
</style>