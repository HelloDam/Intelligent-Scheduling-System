<template>
	<view>
		<view style="margin-bottom: 10rpx;">
			<van-cell-group>
				<van-field :value="oldPassword" label="旧密码" placeholder="请输入旧密码" password="true" required
					:error="iserror" maxlength=20 :error-message="oldErrMes" @change="onChangeOldPassword" />
				<van-field :value="newPassword" label="新密码" placeholder="请输入新密码" password="true" required
					:error="iserror" maxlength=20 :error-message="newErrMes" @change="onChangeNewPassword" />
				<van-field :value="confirmPassword" label="确认密码" placeholder="请确认密码" password="true" required
					:error="iserror" maxlength=20 :error-message="confirmErrMes" @change="onChangeConfirmPassword" />
			</van-cell-group>
		</view>
		<van-toast id="van-toast" />
		<view style="display: flex;position: fixed;bottom: 0;width: 100%;">
			<!-- <button style="width:50%;background-color: #4d7eeb;color: white;">编辑</button>
			<button style="width:50%;background-color: #4d7eeb;">保存</button>
			<!-- <van-button type="info" style="width:50%;" size="large" @click="edituserinfo">编辑</van-button> -->
			<van-button type="danger" form-type style="width:100%;" size="large" @click="savePwd"
				:disabled="isdisabled">保存</van-button>
		</view>
	</view>
</template>

<script>
	import Toast from '@/wxcomponents/vant/dist/toast/toast.js';
	import userApi from '@/common/api/user.js';
	export default {
		data() {
			return {
				isdisabled: true,
				oldPassword: '',
				newPassword: '',
				confirmPassword: '',
				user: {
					oldPassword: '',
					newPassword: '',
					confirmPassword: ''
				},
				iserror: false,
				oldErrMes: '',
				newErrMes: '',
				confirmErrMes: '',
				oldisok: false,
				newisok: false,
				confirmisok: false,
				rules: {
					oldPassword: {
						rule: /[0-9a-zA-Z]{6,20}/,
						msg: "密码应该为6-20位字符"
					},
					newPassword: {
						rule: /[0-9a-zA-Z]{6,20}/,
						msg: "密码应该为6-20位字符"
					},
					confirmPassword: {
						rule: /[0-9a-zA-Z]{6,20}/,
						msg: "密码应该为6-20位字符"
					}
				}
			};
		},

		methods: {
			onChangeOldPassword(event) {
				this.oldPassword = event.detail
				this.isdisabled = false
				// 旧密码表单验证
				// if(this.user.oldPassword==''){
				// 	this.oldErrMes = '旧密码不能为空'
				// }else if(this.user.oldPassword.length<6 || this.user.oldPassword.length>20){
				// 	this.oldErrMes = '长度在 6 到 20 个字符'
				// }else{
				// 	this.oldErrMes = ''
				// 	this.oldisok = true
				// }
			},
			onChangeNewPassword(event) {
				this.newPassword = event.detail
				this.isdisabled = false
				// 新密码表单验证
				// if(this.user.newPassword==''){
				// 	this.newErrMes = '新密码不能为空'
				// }else if(this.user.newPassword.length<6 || this.user.newPassword.length>20){
				// 	this.newErrMes = '长度在 6 到 20 个字符'
				// }else{
				// 	this.newErrMes = ''
				// 	this.newisok = true
				// }
			},
			onChangeConfirmPassword(event) {
				this.confirmPassword = event.detail
				this.isdisabled = false
				// 确认密码表单验证
				// if(this.user.confirmPassword==''){
				// 	this.confirmErrMes = '确认密码不能为空'
				// }else if(this.user.confirmPassword.length<6 || this.user.confirmPassword.length>20){
				// 	this.confirmErrMes = '长度在 6 到 20 个字符'
				// }else{
				// 	this.confirmErrMes = ''
				// 	this.confirmisok = true
				// }
			},

			// 表单验证
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
			savePwd() {
				if (!this.validate('oldPassword')) return;
				if (!this.validate('newPassword')) return;
				if (!this.validate('confirmPassword')) return;
				console.log('保存并验证表单')


				// 新密码确认是否一致
				if (this.newPassword != '' && this.confirmPassword != '' && this.newPassword == this.confirmPassword) {
					this.user.oldPassword = this.oldPassword
					this.user.newPassword = this.newPassword
					this.user.confirmPassword = this.confirmPassword
					console.log(this.user)

					userApi.changePassword(this.user.oldPassword, this.user.newPassword).then((res) => {
						console.log(res)
						Toast({
							type: 'success',
							message: '修改成功',
							onClose: () => {
								this.isdisabled = true
								this.user.oldPassword = ''
								this.user.newPassword = ''
								this.user.confirmPassword = ''
								this.oldErrMes = ''
								this.newErrMes = ''
								this.confirmErrMes = ''
							}
						});
					}).catch(() => {

					})

					// uni.request({
					// 	url: baseurl + '/system/user/changePassword',
					// 	method:'GET',
					// 	data:{
					// 		oldPassword: this.user.oldPassword,
					// 		newPassword: this.user.newPassword
					// 	},
					// 	header:{
					// 		'token': token
					// 	},
					// 	success: (res) => {
					// 		console.log(res)
					// 		Toast({
					// 		  type: 'success',
					// 		  message: '修改成功',
					// 		  onClose: () => {
					// 			this.isdisabled = true
					// 			this.user.oldPassword = ''
					// 			this.user.newPassword = ''
					// 			this.user.confirmPassword = ''
					// 			this.oldErrMes = ''
					// 			this.newErrMes = ''
					// 			this.confirmErrMes = ''
					// 		  }
					// 		});
					// 	}
					// })

				} else {
					console.log('false')
					Toast({
						type: 'fail',
						message: '修改失败',
						onClose: () => {
							this.isdisabled = true
							this.oldPassword = ''
							this.newPassword = ''
							this.confirmPassword = ''
							// this.oldErrMes = ''
							// this.newErrMes = ''
							// this.confirmErrMes = ''
						}
					});
				}

			}
		}
	}
</script>

<style lang="scss">

</style>