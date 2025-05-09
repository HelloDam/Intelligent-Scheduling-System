<template>
	<view>
		<van-toast id="van-toast" />
		<view style="margin-bottom: 10rpx;">
			<van-cell-group>
				<van-field v-model="userinfo.enterpriseName" label="所属企业" placeholder="请输入所属企业" disabled />
				<van-field :value="userinfo.storeName" label="所属门店" placeholder="请输入所属门店" disabled />
				<van-field :value="userinfo.positionName" label="员工职位" placeholder="请输入员工职位" disabled />

				<van-field v-if="user.type === 10" value="普通用户" label="用户类型" disabled />
				<van-field v-if="user.type === 0" value="系统管理员" label="用户类型" disabled />
				<van-field v-if="user.type === 1" value="企业管理员" label="用户类型" disabled />
				<van-field v-if="user.type === 2" value="门店管理员" label="用户类型" disabled />
			</van-cell-group>
		</view>

		<view>
			<van-cell-group>
				<van-field :value="userinfo.username" required clearable label="用户名" placeholder="请输入用户名"
					:disabled="isdisabled" @change="onChangeUserName" />
				<van-field :value="userinfo.nickname" required clearable label="姓名" placeholder="请输入姓名"
					:disabled="isdisabled" @change="onChangeNickname" />
				<van-field :value="userinfo.gender ? '女' : '男'" label="性别" clearable placeholder="请输入性别" required
					:disabled="isdisabled" @change="onChangeGender" />
				<van-field :value="userinfo.phone" label="电话" placeholder="请输入电话" required clearable
					:disabled="isdisabled" @change="onChangePhone" />
				<van-field :value="userinfo.mail" label="邮箱" placeholder="请输入邮箱" required clearable
					:disabled="isdisabled" @change="onChangeMail" />
			</van-cell-group>
		</view>
		<view style="display: flex;position: fixed;bottom: 0;width: 100%;">
			<!-- <button style="width:50%;background-color: #4d7eeb;color: white;">编辑</button>
			<button style="width:50%;background-color: #4d7eeb;">保存</button> -->
			<van-button type="info" style="width:50%;" size="large" @click="edituserinfo">编辑</van-button>
			<van-button type="danger" style="width:50%;" size="large" @click="saveuserinfo"
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
				userinfo: null,
				isdisabled: true
			};
		},
		onLoad() {
			console.log('userinfo页面')
			this.userinfo = uni.getStorageSync('userinfo')
		},
		methods: {
			edituserinfo() {
				console.log('编辑')
				this.isdisabled = false
			},
			onChangeUserName(event) {
				this.userinfo.username = event.detail
			},
			onChangeNickname(event) {
				this.userinfo.nickname = event.detail
			},
			onChangeGender(event) {
				this.userinfo.gender = event.detail == '女' ? 1 : 0
			},
			onChangePhone(event) {
				this.userinfo.phone = event.detail
			},
			onChangeMail(event) {
				console.log(event)
				this.userinfo.mail = event.detail
			},

			saveuserinfo() {
				console.log('保存')
				this.isdisabled = true
				console.log(this.userinfo)
				// 发送新数据 保存
				// 查出新数据 存入 userinfo storage
				uni.setStorageSync('userinfo', this.userinfo)
				// const baseurl = 'http://localhost:6001'
				const baseurl = 'http://192.168.43.213:6001'
				const token = uni.getStorageSync('user')
				userApi.update(this.userinfo)
					.then((res) => {
						console.log((res))
						if (res.code == 200) {
							console.log('编辑基本信息成功')
							console.log(res.data)

							Toast({
								type: 'success',
								message: '修改成功',
							});

						} else {
							console.log('修改失败')
							Toast({
								type: 'fail',
								message: '修改失败',
							});
						}
					}).catch(() => {

					})

				// uni.request({
				// 	url: baseurl + '/system/user/update',
				// 	method:'POST',
				// 	data: this.userinfo,
				// 	header:{
				// 		'token': token
				// 	},
				// 	success: (res) => {
				// 		console.log((res))
				// 		if(res.data.code==200){
				// 			console.log('编辑基本信息成功')
				// 			console.log(res.data.data)

				// 			Toast({
				// 			  type: 'success',
				// 			  message: '修改成功',
				// 			});

				// 		}else{
				// 			console.log('修改失败')
				// 			Toast({
				// 			  type: 'fail',
				// 			  message: '修改失败',
				// 			});
				// 		}
				// 	}
				// })
			}
		}
	}
</script>

<style lang="scss">

</style>