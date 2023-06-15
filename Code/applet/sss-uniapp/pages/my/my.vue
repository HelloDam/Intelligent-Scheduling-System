<template>
	<view class="my">

		<view class="my-user" id="my-user">
			<!-- <view :style="[{width:windowWidth+'px'}]"> -->
			<view>
				<view v-if="user===''" class="user-unlogin" @click="toLogin">
					<image :src="useravatar"></image>
					<!-- <uni-icons type="contact-filled" size="90"></uni-icons> -->
					<text>登录</text>
				</view>

				<view v-else>
					<view class="user-login" style="display: flex;">
						<view style="display: flex;justify-content: center;align-items: center;">
							<image :src="userinfo.avatar"></image>
							<view class="user-name-box">
								<view class="usernickname">{{userinfo.nickname}}</view>
								<view class="useridname">用户ID : {{userinfo.username}}</view>
							</view>
							<!-- <view class="userinfo-card" style="margin: 10rpx;"><text>{{userinfo.nickname}}</text></view> -->
						</view>
						<view style="margin:0px 10rpx;"></view>
						<view style="float: right;margin-right: 35rpx;" @click="mysetting">
							<view style="display: flex;flex-direction: column;align-items: center;">
								<view class="userinfo-card"><text>个人设置</text></view>
								<uni-icons type="gear" size="50" color="#fff"></uni-icons>
							</view>
						</view>
					</view>

				</view>
			</view>
			<!-- 			<view v-if="user!=''" class="staticDataBox">
				<view style="display: flex;flex-direction: column;align-items: center;">
					<view><text>16</text></view>
					<view><text>本月工作日数</text></view>
				</view>
				<view>
					<view><text>4</text></view>
					<view><text>本周工作日数</text></view>
				</view>
				<view>
					<view><text>80</text></view>
					<view><text>本月工作时长</text></view>
				</view>
				<view>
					<view><text>20</text></view>
					<view><text>本周工作时长</text></view>
				</view>
			</view> -->
		</view>

		<!-- <view class="my-setting" id="my-setting">
			<view class="my-set-card" @click="mysetting">
				<uni-icons type="gear" size="50"></uni-icons>
				<text>设置</text>
				<view class="forward-icon">
					<uni-icons type="forward" size="20"></uni-icons>
				</view>
			</view>
			<van-toast id="van-toast2" />
		</view> -->

		<view class="inform-box" id="inform-box" :style="[{height:windowHeight-myUserHeight-CustomBar-20+'px'}]">
			<view class="inform-title">
				<text style="font-size: 18px;font-weight: bold;">通知</text>
			</view>
			<van-divider />
			<scroll-view v-if="user!=''" class="message-box" id="scroll-box" scroll-y="true"
				style="height: calc(100% - 90px);" :scroll-top="scrollTop" @scroll="scroll"
				:scroll-with-animation="true">
				<view class="message-content" v-for="(message,index) in messageList" :key="index">
					<view class="mes-avater">
						<image :src="message.publishAvatar" class="avater"></image>
					</view>
					<view class="mes-detail" :style="[{width:informboxWidth-80+'px'}]">
						<view class="mes-header">
							<text class="mes-publisName">{{message.publishName}}</text>
							<text class="mes-publisTime">{{message.publishTime}}</text>
						</view>
						<view class="mes-body">
							<view class="mes-body-content">
								<!-- <rich-text :nodes="message.content"></rich-text> -->
								<!-- 富文本展示 -->
								<mp-html :content="message.content" />
							</view>
						</view>
					</view>
				</view>
			</scroll-view>

			<view v-else class="loginnull">
				<image style="width:100%;height:100%" mode="scaleToFill" src="/static/coursenull.png" />
				<!-- <text wx:if="{{!userInfo.courseInfo || userInfo.courseInfo.length == 0}}" class="todaytips">
					还没录入课程哦~
				</text> -->
				<text>请先登录！</text>
			</view>
		</view>

		<view class="dialog-content">
			<!-- 普通弹窗 -->
			<uni-popup ref="popup" @change="change">
				<view class="popup-content">
					<view class="dltext">登录</view>
					<!-- 用户名 -->

					<view class="user-name">
						<image src="/static/tab_icons/user.png" class="un"></image>
						<input v-model="username" placeholder="请输入用户名" />
					</view>
					<!-- 密码 -->
					<view class="passwordCs">
						<image src="/static/password.png" class="ps"></image>
						<input v-model="password" password placeholder="请输入密码" />
						<!-- <image src="/images/eye-open.png" class="eye"></image> -->
					</view>
					<!-- 验证码 -->
					<view class="validCode">
						<image src="/static/validcode.png" class="vc"></image>
						<input placeholder="请输入验证码" v-model="loginForm.verificationCode">
						<!-- 验证码图片 -->
						<image :src="'data:image/png;base64,' + verifyCodeImg" class="vcode"
							@click="freshVerificationCode"></image>
					</view>
					<!-- 登录按钮 -->
					<view class="denglu">
						<button class="btn-dl" type="primary" @click="login">登录</button>
					</view>

					<van-toast id="van-toast" />
					<view class="v3">
						<view class="line"></view>
						<view class="lText">您还可以使用以下方式登录</view>
						<view class="line"></view>
					</view>
					<view class="wxicon">
						<image src="../../static/wechat.png" @click="wxlogin"></image>
					</view>

				</view>

			</uni-popup>
		</view>
	</view>
</template>

<script>
	import Toast from '@/wxcomponents/vant/dist/toast/toast.js';
	import loginApi from '@/common/api/login.js';
	import userApi from '@/common/api/user.js';
	import messageApi from '@/common/api/message.js';
	import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'

	export default {
		name: "my-info",
		components: {
			mpHtml
		},
		data() {
			return {
				myUserHeight: 0,
				mySettingHeight: 0,
				informboxWidth: 0,
				scrollTop: 0,
				old: {
					scrollTop: 0
				},
				windowHeight: this.windowHeight,
				CustomBar: this.CustomBar,
				windowWidth: this.windowWidth,
				type: 'center',
				username: '',
				password: '',
				loginForm: {
					username: '',
					password: '',
					uuid: "",
					verificationCode: '',
				},
				useravatar: '/static/tab_icons/user.png',
				// 验证码
				verifyCodeImg: '../../static/logo.png',
				uuid: "",
				isLogin: 1,
				user: '',
				userinfo: {
					avatar: '',
					nickname: ''
				},
				messageList: [],

				rules: {
					username: {
						rule: /\S/,
						msg: "用户名不能为空"
					},
					password: {
						rule: /[0-9a-zA-Z]{6,20}/,
						msg: "密码应该为6-20位字符"
					}
				}
			};
		},
		onShow() {
			console.log('mymymymy')
			this.user = uni.getStorageSync('user')
			if (this.user != '') {
				console.log('user不为空')
				this.userinfo = uni.getStorageSync('userinfo')
				this.getMessageInform()
				this.goBottom()
				// this.getUserInfo()
			} else {
				console.log('user为空')
				this.userinfo = {}
			}

		},
		onLoad() {
			console.log('我的页面数据初始化')
			this.getMyLocationInfo()
			// 获取storage数据
			this.user = uni.getStorageSync('user')
			if (this.user == '') this.isLogin = 0;
			else this.isLogin = 1;
			this.getMessageInform()
			// let messageBox = document.querySelector('.message-box');
			// messageBox.scrollTop = messageBox.scrollHeight;
			// console.log(this.user)
			this.goBottom()
		},

		methods: {
			scroll: function(e) {
				this.old.scrollTop = e.detail.scrollTop
			},
			goBottom: function(e) {
				this.scrollTop = this.old.scrollTop
				this.$nextTick(function() {
					this.scrollTop = 9999999
				});
			},
			getMyLocationInfo() {
				let self = this
				const query = uni.createSelectorQuery().in(self)
				query.select('#my-user').boundingClientRect(function(rect) {
					console.log('获取my-user组件高度')
					self.myUserHeight = rect.height
					console.log(self.myUserHeight)
					console.log('获取my-user组件高度')
				})
				query.select('#inform-box').boundingClientRect(function(rect) {
					console.log('获取inform-box组件高度')
					self.informboxWidth = rect.width
					console.log(self.informboxWidth)
					console.log('获取inform-box组件高度')
				})
				query.select('#my-setting').boundingClientRect(function(rect) {
					console.log('获取my-setting组件高度')
					self.mySettingHeight = rect.height
					console.log(self.mySettingHeight)
					console.log('获取my-setting组件高度')
				}).exec()
			},
			toLogin() {
				// uni.navigateTo({
				// 	url:'/pages/login/login'
				// })
				this.toggle();
				this.freshVerificationCode()
			},
			// 登录表单验证
			validate(key) {
				let bool = true;
				if (!this.rules[key].rule.test(this[key])) {
					uni.showToast({
						title: this.rules[key].msg,
						icon: "none"
					})
					bool = false;
					return false;
				}
				return bool;
			},

			login() {
				console.log('登录')
				// console.log(this.loginForm)
				if (!this.validate('username')) return;
				if (!this.validate('password')) return;
				this.loading = true;
				this.loginForm.uuid = this.uuid;
				this.loginForm.username = this.username;
				this.loginForm.password = this.password;
				console.log("this.loginForm:" + JSON.stringify(this.loginForm))
				const {
					username,
					password,
					uuid,
					verificationCode
				} = this.loginForm
				// { username: username.trim(), password: password, uuid: uuid, verificationCode: verificationCode }

				loginApi.login({
					username: username.trim(),
					password: password,
					uuid: uuid,
					verificationCode: verificationCode
				}).then(
					(res) => {
						console.log("登录响应：" + JSON.stringify(res))
						if (res.code == 200) {
							console.log('登录成功')
							console.log(res.data)
							try {
								uni.setStorageSync('user', res.data.token)
								this.user = uni.getStorageSync('user')
								this.getUserInfo()
								Toast({
									type: 'success',
									message: '登录成功',
									onClose: () => {
										this.popupClose()
										this.isLogin = 1
									},
								});

								this.getMessageInform()

							} catch (e) {
								// error
								console.log('error')
							}
						} else if (res.code == 204) {
							console.log('验证码错误')
							Toast({
								type: 'fail',
								message: '验证码错误',
								onClose: () => {
									this.popupClose()
									this.loginForm.username = ''
									this.loginForm.password = ''
									this.loginForm.verificationCode = ''
									this.loginForm.uuid = ''
								},
							});
						}
					}
				).catch(
					() => {
						console.log('登录失败')
						Toast({
							type: 'fail',
							message: '登录失败',
							onClose: () => {
								this.popupClose()
								this.loginForm.username = ''
								this.loginForm.password = ''
								this.loginForm.verificationCode = ''
								this.loginForm.uuid = ''
							},
						});
					}
				)

			},
			getUserInfo() {
				console.log('获取用户数据')
				const token = uni.getStorageSync('user')

				userApi.getUserInfoVoByToken().then((res) => {
					console.log("getUserInfoVoByToken：" + JSON.stringify(res))
					this.userinfo = res.userInfoVo
					uni.setStorageSync('userinfo', res.userInfoVo)
					// console.log('userinfo:' + JSON.stringify(this.userinfo))
				}).catch(() => {

				})
			},
			change(e) {
				console.log('当前模式：' + e.type + ',状态：' + e.show);
			},
			toggle(type) {
				// this.type = type
				// open 方法传入参数 等同在 uni-popup 组件上绑定 type属性
				this.$refs.popup.open(type)
			},
			popupClose() {
				this.$refs.popup.close()
				this.username = ''
				this.password = ''
				this.loginForm.verificationCode = ''
			},
			/**
			 * 刷新登录验证码
			 */
			freshVerificationCode() {
				// console.log('刷新验证码')
				loginApi.generateVerificationCode().then((res) => {
					console.log(res)
					this.verifyCodeImg = res.image;
					this.uuid = res.uuid;
				}).catch(() => {

				})

			},

			mysetting() {
				console.log('设置')
				if (this.isLogin) {
					uni.navigateTo({
						url: '/pages/setting/setting'
					})
				} else {
					console.log('请先登录')
					Toast({
						type: 'fail',
						message: '请先登录',
						selector: '#van-toast2'
					});
				}
			},

			getMessageInform() {
				console.log('获取通知')
				const token = uni.getStorageSync('user')

				messageApi.listMessageOfUser().then((res) => {
					console.log(res)
					this.messageList = res.messageList
					// console.log(this.messageList[1].content)
					this.goBottom()
				}).catch(() => {

				})
			},

			wxlogin() {
				console.log('微信登录')
				uni.getUserProfile({
					desc: '获取用户头像及昵称',
					success: (res) => {
						console.log(res)
						uni.login({
							"provider:": 'weixin',
							"onlyAuthorize": true, // 微信登录仅请求授权认证
							success: function(event) {
								const {
									code
								} = event
								console.log('success')
								console.log(code)
								console.log(res)
								this.userinfo.avatar = res.userInfo.avatarUrl
								this.userinfo.nickname = res.userInfo.nickName
								//客户端成功获取授权临时票据（code）,向业务服务器发起登录请求。
								// const baseurl = 'http://localhost:6001'
								// uni.request({
								// 	url: 'https://www.example.com/loginByWeixin', //仅为示例，并非真实接口地址。
								// 	data: {
								// 		code: event.code
								// 	},
								// 	success: (res) => {
								// 		console.log(res)
								// 		//获得token完成登录
								// 		uni.setStorageSync('token',res.token)
								// 	},
								// });
								// 登录成功
								// uni.getUserInfo({
								// 	provider: 'weixin',
								// 	success: function(info) {
								// 		console.log(info)
								// 		// 获取用户信息成功, info.authResult保存用户信息
								// 	}
								// })
								// console.log(uni.getUserProfile)
								// uni.getUserProfile({
								// 	desc:'获取用户头像及昵称',
								// 	success: (res) => {
								// 		console.log(res)
								// 	},
								// 	fail: (res) => {
								// 		console.log('fail')
								// 	}
								// })
							}
						})

					},
					fail: (res) => {
						console.log('fail')
					}
				})

			}
		}
	}
</script>

<style lang="scss">
	.my-user {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex-direction: column;
		width: 100%;
		height: 180rpx;
		background: linear-gradient(to top, #8abffe 0%, #4d7eeb 100%);
		// background-color: #4d7eeb;
		border-radius: 0px 0px 0px 0px;
	}

	.user-unlogin {
		margin-left: 20rpx;
		display: flex;
		align-items: center;
		padding-left: 28rpx;
		width: 750rpx;
	}

	.user-unlogin image {
		width: 141rpx;
		height: 141rpx;
		background: #FFF;
		border-radius: 50%;
		opacity: 1;
	}

	.user-unlogin text {
		padding: 22rpx;
	}

	.user-login {
		display: flex;
		align-items: center;
		padding-left: 28rpx;
		width: 750rpx;
		justify-content: space-around;
	}

	.user-login image {

		width: 141rpx;
		height: 141rpx;
		background: #FFF;
		border-radius: 50%;
		opacity: 1;
	}

	.user-login text {
		padding: 22rpx;
	}

	.my-setting {
		display: flex;
		padding-top: 30rpx;
		// border-radius: 10px;
		align-items: center;
	}

	.my-setting .my-set-card {
		display: flex;
		align-items: center;
		margin-left: auto;
		margin-right: auto;
		width: 90%;
		height: 120rpx;
		border-radius: 20px;
		background: #FFF;
		padding-left: 30rpx;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
	}

	.my-setting .my-set-card text {
		padding-left: 10rpx;
		font-size: 20px;
	}

	.my-setting .my-set-card .forward-icon {
		margin-left: auto;
		margin-right: 30rpx;
	}

	.popup-content {
		display: flex;
		// align-items: center;
		// justify-content: center;
		padding: 15px;
		// height: 70%;
		justify-content: center;
		background-color: #ffffff;
		// border-radius: 30rpx;

		left: 100rpx;
		width: 545rpx;
		height: 850rpx;
		// background: #f5f5f5;
		border-radius: 50rpx;
	}

	.popup-content .dltext {
		// margin-top: 50rpx;
		position: absolute;
		// margin-left:80rpx;
		margin-top: 40rpx;
		width: 150rpx;
		height: 100rpx;
		font-size: 60rpx;
		font-family: Helvetica;
		color: #000000;
		line-height: 100rpx;
		letter-spacing: 2rpx;
	}

	.popup-content .user-name {
		margin-top: 200rpx;
		// margin-left: 35rpx;
		position: absolute;
		display: flex;
		width: 450rpx;
		height: 80rpx;
		border-bottom: 3rpx solid rgb(58, 57, 57);
	}

	.popup-content .user-name .un {
		margin-top: 5rpx;
		// margin-left: 30rpx;
		width: 55rpx;
		height: 55rpx;
	}

	.popup-content .user-name input {
		width: 200rpx;
		font-size: 25rpx;
		margin-top: 15rpx;
		margin-left: 30rpx;
	}

	.popup-content .passwordCs {
		margin-top: 350rpx;
		// margin-left: 35rpx;
		position: absolute;
		display: flex;
		width: 450rpx;
		height: 80rpx;
		border-bottom: 3rpx solid rgb(58, 57, 57);
	}

	.popup-content .passwordCs .ps {
		margin-top: 5rpx;
		// margin-left: 30rpx;
		width: 55rpx;
		height: 55rpx;
	}

	.popup-content .passwordCs input {
		width: 200rpx;
		font-size: 25rpx;
		margin-top: 15rpx;
		margin-left: 30rpx;
	}

	.popup-content .validCode {
		margin-top: 500rpx;
		// margin-left: 35rpx;
		position: absolute;
		display: flex;
		width: 450rpx;
		height: 80rpx;
		border-bottom: 3rpx solid rgb(58, 57, 57);
	}

	.popup-content .validCode .vc {
		margin-top: 5rpx;
		// margin-left: 30rpx;
		width: 55rpx;
		height: 55rpx;
	}

	.popup-content .validCode input {
		width: 200rpx;
		font-size: 25rpx;
		margin-top: 15rpx;
		margin-left: 30rpx;
	}

	.popup-content .validCode .vcode {
		// margin-top: 5rpx;
		margin-left: auto;
		// margin-bottom: 5rpx;
		width: 120rpx;
		height: 70rpx;
	}

	.popup-content .denglu {
		width: 350rpx;
		height: 50rpx;
		position: absolute;
		margin-top: 650rpx;
		// margin-left: 85rpx;
	}

	.popup-content .denglu button {
		padding: 0rpx;
		background-color: #4d7eeb;
		line-height: 50rpx;
		font-size: 25rpx;
		width: 100%;
		height: 100%;
		border-radius: 30rpx;
	}

	.popup-content .v3 {
		// padding-left: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;

		position: absolute;

		width: 100%;
		height: 60rpx;
		padding-top: 50rpx;
		margin-top: 680rpx;

	}

	.popup-content .v3 .line {
		margin-left: 5rpx;
		margin-top: 10rpx;
		width: 120rpx;
		height: 3rpx;
		background-color: #3a3939;
	}

	.popup-content .v3 .lText {
		color: #3a3939;
		font-size: 25rpx;
	}


	.popup-content .wxicon {
		position: absolute;
		margin-top: 785rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 80rpx;
	}

	.popup-content .wxicon image {

		width: 80rpx;
		height: 80rpx;
	}

	.userinfo-card {
		// display: flex;
		// align-items: center;
		// padding-left: 28rpx;
		// justify-items: center;
		// margin-left: 20rpx;
		margin-left: auto;
		margin-right: auto;
		// width: 530rpx;
		// width: 100%;
		padding: 10rpx;
		background-color: #fff;
		font-size: 10px;
		font-weight: bold;
		border-radius: 10rpx;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
	}

	.inform-box {
		// display: flex;
		background-color: #fff;
		width: 90%;
		margin: 20rpx auto;
		// text-align: center;
		// align-items: center;
		// padding: 20rpx;
		border-radius: 20px;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
	}

	.inform-box .inform-title {
		display: flex;
		align-items: center;
		text-align: center;
		justify-content: center;
		padding-top: 10px;
		height: 30px;
	}

	.message-box {
		overflow: auto;
	}

	.message-content {
		display: flex;
		align-items: flex-start;
		padding: 10px;
		margin: 15px;
		padding: 10px;
		border-radius: 5px;
	}

	.avater {
		width: 80rpx;
		height: 80rpx;
		margin-right: 10px;
	}

	.mes-detail {
		flex: 1
	}

	.mes-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 5px;
	}

	.mes-publisName {
		color: black;
		font-size: 16px;
		font-weight: bold;
	}

	.mes-publisTime {
		font-size: 14px;
		color: #666;
	}

	.mes-body {
		display: flex;
		flex-direction: row;
		align-items: flex-end;
		margin-bottom: 10px;
	}

	.mes-body-content {
		background: rgba(255, 255, 255, 1);
		color: #333;
		padding: 10px;
		border-radius: 10px;
	}

	.user-name-box {
		display: flex;
		// align-items: center;
		flex-direction: column;

		// background-color: #000000;
		color: #fff;
		margin-left: 20rpx;
	}

	.usernickname {
		margin: 5rpx 0rpx;
		font-size: 16px;
		font-weight: bold;
	}

	.useridname {
		margin: 5rpx 0rpx;
		font-size: 12px;
		float: left;
	}

	.loginnull {
		display: block;
		margin: 120rpx auto;
		width: 400rpx;
		height: 400rpx;
	}

	.loginnull text {
		display: block;
		margin: -50rpx auto 0;
		text-align: center;
		font-size: 28rpx;
		line-height: 40rpx;
		color: #92979d;
	}

	.staticDataBox {
		display: flex;
		justify-content: space-around;
		width: 750rpx;
		font-size: 12px;
		color: white;
		padding: 5rpx;
		text-align: center;
		margin-bottom: 30rpx;
	}
</style>