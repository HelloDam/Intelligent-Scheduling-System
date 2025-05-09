<template>
	<view>
		<van-cell-group>
			<van-cell islink title="基本资料" link-type="navigateTo" url="/pages/userinfo/userinfo">
				<van-icon slot="right-icon" name="arrow" class="custom-icon" />
			</van-cell>
			<van-cell title="工作偏好" link-type="navigateTo" url="/pages/workperfer/workperfer">
				<van-icon slot="right-icon" name="arrow" class="custom-icon" />
			</van-cell>
			<van-cell title="修改密码" link-type="navigateTo" url="/pages/resetPwd/resetPwd">
				<van-icon slot="right-icon" name="arrow" class="custom-icon" />
			</van-cell>
			<van-cell title="退出此账号" @click="loginout">

			</van-cell>
		</van-cell-group>
		<van-toast id="van-toast" />
		<van-dialog id="van-dialog" />
	</view>
</template>

<script>
	import Dialog from '@/wxcomponents/vant/dist/dialog/dialog';
	import Toast from '@/wxcomponents/vant/dist/toast/toast.js';
	import loginApi from '@/common/api/login.js';
	export default {
		data() {
			return {

			};
		},
		methods: {
			loginout() {
				console.log('退出此账号')
				Dialog.confirm({
					// context: this,
					title: '退出',
					message: '确定退出此账号吗？',
					selector: "#van-dialog"
				}).then(() => {
					// on confirm
					// const baseurl = 'http://localhost:6001'
					// const baseurl = 'http://192.168.43.213:6001'
					const token = uni.getStorageSync('user')
					loginApi.logout().then((res) => {
						console.log(res)
						try {
							uni.clearStorageSync();
							Toast({
								type: 'success',
								message: '退出成功',
								onClose: () => {
									uni.switchTab({
										url: '/pages/my/my'
									})
								}
							});

						} catch (e) {
							// error
							console.log('error')
						}
					}).catch(() => {

					})

					// uni.request({
					// 	url: baseurl + '/system/login/logout',
					// 	method:'POST',
					// 	header:{
					// 		'token': token
					// 	},
					// 	success: (res) => {
					// 		console.log(res)
					// 		try {
					// 			uni.clearStorageSync();
					// 			Toast({
					// 			  type: 'success',
					// 			  message: '退出成功',
					// 			  onClose: () => {
					// 			  	uni.switchTab({
					// 					url:'/pages/my/my'
					// 				})
					// 			  }
					// 			});

					// 		} catch (e) {
					// 			// error
					// 			console.log('error')
					// 		}
					// 	}
					// })
				}).catch(() => {
					// on cancel
				});

			}
		}
	}
</script>

<style lang="scss">

</style>