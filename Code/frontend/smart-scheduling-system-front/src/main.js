import Vue from 'vue'

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control

////常量
import ResultCode from '@/components/constant/resultCode'
import IconList from '@/components/constant/iconList'

////组件
// 分页组件
import Pagination from "@/components/Pagination";
Vue.component('Pagination', Pagination)
// 微信登录
import vueQriously from 'vue-qriously'
Vue.use(vueQriously)
// element
import ElementUI from 'element-ui'

////样式
//自定义样式
import "./styles/customerCss.scss"
//阿里矢量图标库
import "./assets/aliIconfont/iconfont.css"
import '@/styles/index.scss' // global css
import '@/styles/ruoyi.scss' // ruoyi css
import '@/styles/chart/chartCss.css'
import '@/styles/element-variables.scss'
import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import 'element-ui/lib/theme-chalk/index.css'
//使用中文
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n


/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)

Vue.config.productionTip = false

//新增
import hasBtnPermission from '@/utils/btn-permission'
Vue.prototype.$hasBP = hasBtnPermission

//后端返回码
Vue.prototype.ResultCode = ResultCode
Vue.prototype.IconList = IconList

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
