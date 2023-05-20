<template>
  <div class="containerDiv" id="bodyDiv">
    <!-- 系统管理员首页 -->
    <systemManagerHomePage v-if="userType===0"></systemManagerHomePage>
    <!-- 企业管理员或门店管理员首页 -->
    <storeHomePage v-if="userType===1||userType===2"></storeHomePage>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import storeHomePage from './storeHomePage.vue'
import systemManagerHomePage from '@/views/dashboard/systemManager/systemManagerHomePage'
// import backgroundCss from '@/styles/background/backgroundCss.css'

export default {
  components: { storeHomePage ,systemManagerHomePage},
  name: 'Dashboard',

  computed: {
    ...mapGetters([
      'name',
      'userType'
    ])
  },
  created() {

  }
}
</script>

<style lang="scss" scoped>
.containerDiv {
  padding: 10px;
  height: calc(100vh - 70px);
  // background: darkmagenta;
}

#bodyDiv {
  // margin: 0;
  /* min-height: 100vh; */
  height: 100%;
  background-color: #c4d7fa;
  background-image: radial-gradient(closest-side, rgb(255, 255, 255), rgba(235, 105, 78, 0)), radial-gradient(closest-side, rgb(250, 203, 203), rgba(243, 11, 164, 0)), radial-gradient(closest-side, rgb(237, 252, 202), rgba(254, 234, 131, 0)), radial-gradient(closest-side, rgb(197, 248, 241), rgba(170, 142, 245, 0)), radial-gradient(closest-side, rgb(206, 200, 243), rgba(248, 192, 147, 0));
  background-size: 130vmax 130vmax, 80vmax 80vmax, 90vmax 90vmax, 110vmax 110vmax, 90vmax 90vmax;
  background-position: -80vmax -80vmax, 60vmax -30vmax, 10vmax 10vmax, -30vmax -10vmax, 50vmax 50vmax;
  background-repeat: no-repeat;
}
</style>
