import { login, logout, getInfo } from '@/api/login/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: '',
    buttons: [], // 新增
    menus: ''//新增
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_USER_TYPE: (state, userType) => {
    state.userType = userType
  },
  // 新增
  SET_BUTTONS: (state, buttons) => { state.buttons = buttons },
  // 新增
  SET_MENUS: (state, menus) => { state.menus = menus }
}

const actions = {
  //用户登录方法
  login({ commit }, userInfo) {
    console.log("调用login方法0")
    const { username, password, uuid, verificationCode } = userInfo
    return new Promise((resolve, reject) => {
      console.log("调用login方法1")
      login({ username: username.trim(), password: password, uuid: uuid, verificationCode: verificationCode }).then(response => {
        //  debugger
        console.log("登录成功")
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        console.log("登录失败")
        // alert("response:"+JSON.stringify(response))
        //  debugger
        reject(error)
      })

    })
  },

  //微信登录方法
  weixinLogin({ commit }, token) {
    console.log("设置token信息:" + token)
    commit('SET_TOKEN', token)
    setToken(token)
  },

  //获取用户信息
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response
        // console.log("获取用户信息，response:" + JSON.stringify(response))
        if (!data) {
          return reject('Verification failed, please Login again.')
        }

        const { name, avatar, userType } = data
        // console.log("用户类型，userType:" + userType);
        commit('SET_NAME', name)
        commit('SET_AVATAR', avatar)
        commit('SET_USER_TYPE', userType)
        commit("SET_BUTTONS", data.buttons)
        commit("SET_MENUS", data.routers)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  //用户登出
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken() // must remove  token  first
        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  //移除token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

