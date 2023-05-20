<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in"  class="routerView">
      <router-view :key="key" class="routerView" />
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',

  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.app-main {
  /*50 = navbar  */
  width: 100%;
  height: calc(100vh - 70px);
  position: relative;
  overflow: hidden;
  /* background: blanchedalmond; */
}

.routerView{
  /* width: 100%; */
  height: calc(100vh - 70px);
  /* background: aqua; */
}

.fixed-header+.app-main {
  padding-top: 50px;
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
