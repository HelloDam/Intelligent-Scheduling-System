<template>
	<view>
		<!-- <van-cell title="工作日偏好"  />
		<view>
			<van-radio-group :value="radio" bind:change="onChange">
			  <van-radio name="1" shape="square">单选框 1</van-radio>
			  <van-radio name="2" shape="square">单选框 2</van-radio>
			</van-radio-group>
		</view> -->
		<van-toast id="van-toast" />
		<van-collapse :value="activeNames" @change="onChange">
			<van-collapse-item title="工作日偏好" name="1">
				<view>
					<view>
						<uni-data-checkbox multiple v-model="value" :localdata="range"
							@change="checkBoxChange"></uni-data-checkbox>
					</view>
				</view>
				<!-- <van-radio-group :value="radio" @change="radioOnChange">
		      <van-radio name="1" shape="square">星期一</van-radio>
		      <van-radio name="2" shape="square">星期二</van-radio>
			  <van-radio name="3" shape="square">星期三</van-radio>
			  <van-radio name="4" shape="square">星期四</van-radio>
			  <van-radio name="5" shape="square">星期五</van-radio>
			  <van-radio name="6" shape="square">星期六</van-radio>
			  <van-radio name="7" shape="square">星期日</van-radio>
		    </van-radio-group> -->
			</van-collapse-item>
			<van-collapse-item title="工作时间偏好" name="2">
				<van-row style="text-align: center;align-items: center;color: black;">
					<van-col span="8">开始时间</van-col>
					<van-col span="8">结束时间</van-col>
					<van-col span="8">
						<view @click="addTime" style="color: #4d7eeb;"><van-icon name="plus" /> 添加时间段</view>
					</van-col>
				</van-row>
				<van-row v-for="(item, index) in workTimePrefer" :key="index"
					style="text-align: center;align-items: center;padding: 5rpx;color: black;">
					<van-col span="8">{{item.workTimePreferStartTime}}</van-col>
					<van-col span="8">{{item.workTimePreferEndTime}}</van-col>
					<van-col span="8">
						<van-button type="danger" size="mini" color="#f56c6c"
							@click="deleteworkTimePrefer(index)">删除</van-button>
					</van-col>
				</van-row>
			</van-collapse-item>
			<van-collapse-item title="班次时长偏好" name="3">
				<van-row style="text-align: center;align-items: center;color: black;padding: 5rpx;">
					<van-col span="8">每天工作时长</van-col>
					<van-col span="8">{{currentDayTime}}</van-col>
					<van-col span="8">
						<!-- <view @click="editDailyWorkTime" style="color: #4d7eeb;"><van-icon name="plus"/> 编辑</view> -->
						<van-button type="info" size="mini" @click="editWorkTime('day')">编辑</van-button>
					</van-col>
				</van-row>
				<van-row style="text-align: center;align-items: center;color: black;padding: 5rpx;">
					<van-col span="8">每周工作时长</van-col>
					<van-col span="8">{{currentWeekTime}}</van-col>
					<van-col span="8">
						<!-- <view @click="editDailyWorkTime" style="color: #4d7eeb;"><van-icon name="plus"/> 编辑</view> -->
						<van-button type="info" size="mini" @click="editWorkTime('week')">编辑</van-button>
					</van-col>
				</van-row>
			</van-collapse-item>
		</van-collapse>

		<view style="display: flex;bottom: 0;width: 100%;height: auto;">
			<!-- <button style="width:50%;background-color: #4d7eeb;color: white;">编辑</button>
			<button style="width:50%;background-color: #4d7eeb;">保存</button> -->
			<!-- <van-button type="info" style="width:50%;" size="large" @click="edituserinfo">编辑</van-button> -->
			<van-button type="danger" style="width:100%;" size="large" @click="saveCustom" color="#f56c6c"
				:disabled="saveIsDisable">保存</van-button>
		</view>



		<van-popup :show="workTimePreferShow" round position="bottom" @close="onCloseWorkTime">
			<van-cell-group inset>
				<van-cell>
					<van-row style="text-align: center;align-items: center;color: black;padding: 5rpx;">
						<text>添加工作时间偏好</text>
					</van-row>
					<van-row style="text-align: center;align-items: center;color: black;padding: 5rpx;">
						<van-col span="8">开始时间</van-col>
						<van-col span="8">{{starttemp}}</van-col>
						<van-col span="8">
							<van-button type="info" size="mini" @click="editaddtemp('start')">编辑</van-button>
						</van-col>
					</van-row>
					<van-row style="text-align: center;align-items: center;color: black;padding: 5rpx;">
						<van-col span="8">结束时间</van-col>
						<van-col span="8">{{endtemp}}</van-col>
						<van-col span="8">
							<van-button type="info" size="mini" @click="editaddtemp('end')">编辑</van-button>
						</van-col>
					</van-row>
					<view style="align-items: center;text-align: center;">
						<van-button type="info" size="large" @click="saveaddtemp()"
							style="width: 50%;color: #f56c6c;">保存</van-button>
					</view>
				</van-cell>
				<van-cell v-if="isEditStartAndEnd">
					<van-datetime-picker type="time" :value="workTimePreferTime" min-hour=0 max-hour=24
						@confirm="onConfirmWorkTimePrefer" @cancel="onCancelWorkTimePrefer">
					</van-datetime-picker>
				</van-cell>
			</van-cell-group>
		</van-popup>

		<van-popup :show="show" round position="bottom" @close="onClose">
			<van-cell-group inset>
				<van-cell>
					<van-datetime-picker type="time" :value="currentTime" :min-hour="minHour" :max-hour="maxHour"
						@confirm="onConfirm" @cancel="onCancel" />
				</van-cell>
			</van-cell-group>
		</van-popup>

	</view>
</template>

<script>
	import Toast from '@/wxcomponents/vant/dist/toast/toast.js';
	import userApi from '@/common/api/user.js';
	export default {
		data() {
			return {
				userinfo: {},
				value: [1, 2],
				range: [{
						"value": 1,
						"text": "星期一"
					}, {
						"value": 2,
						"text": "星期二"
					}, {
						"value": 3,
						"text": "星期三"
					},
					{
						"value": 4,
						"text": "星期四"
					}, {
						"value": 5,
						"text": "星期五"
					}, {
						"value": 6,
						"text": "星期六"
					},
					{
						"value": 7,
						"text": "星期日"
					}
				],
				radio: ['1'],
				activeNames: ['1', '2', '3'],
				workTimePrefer: [
					// {
					// 	workTimePreferStartTime:'08:00',
					// 	workTimePreferEndTime:'10:00',
					// },
					// {
					// 	workTimePreferStartTime:'14:00',
					// 	workTimePreferEndTime:'16:00',
					// }
				],
				workTimePreferTime: '08:00',
				currentTime: '12:00',
				minHour: 0,
				maxHour: 168,
				dayOrWeek: 'day',
				workTimePreferShow: false,
				show: false,
				currentDayTime: '08:00',
				currentWeekTime: '40:00',
				starttemp: '08:00',
				endtemp: '17:00',
				isEditStartAndEnd: false,
				startOrEnd: '',
				saveIsDisable: true
			};
		},
		onLoad() {
			console.log('工作偏好OnLoad')
			this.userinfo = uni.getStorageSync('userinfo')
			this.value = this.userinfo.workDayPreferenceList
			this.currentDayTime = this.userinfo.shiftLengthPreferenceOneDay
			this.currentWeekTime = this.userinfo.shiftLengthPreferenceOneWeek
			// let timetemp = '08:30~10:30|10:30~12:30|12:30~14:30'
			let timetemp = this.userinfo.workTimePreference
			let temp = timetemp.split("|")
			console.log(temp)

			for (let i = 0; i < temp.length; i++) {
				let itemp = temp[i].split("~")
				this.workTimePrefer.push({
					'workTimePreferStartTime': itemp[0],
					'workTimePreferEndTime': itemp[1]
				})
			}
			console.log(this.workTimePrefer)
		},
		methods: {
			checkBoxChange(e) {
				console.log('e:', e);
				this.value = e.detail.value
				console.log(this.value)
				this.saveIsDisable = false
			},
			onChange(event) {
				let self = this
				console.log(event)
				self.activeNames = event.detail
			},
			radioOnChange(event) {
				console.log('选择框')
				console.log(event)
				this.radio = event.detail
			},
			addTime() {
				console.log('添加时间段')
				this.workTimePreferShow = true
			},
			deleteworkTimePrefer(index) {
				console.log('删除时间段')
				console.log(index)
				this.workTimePrefer.splice(index, 1)
				this.saveIsDisable = false
			},
			onConfirm(event) {
				console.log('时间选择器')
				console.log(event)
				if (this.dayOrWeek == 'day') {
					this.currentDayTime = event.detail
				} else if (this.dayOrWeek == 'week') {
					this.currentWeekTime = event.detail
				}
				this.onClose()
				this.saveIsDisable = false
			},
			editWorkTime(data) {
				if (data == 'day') {
					console.log('编辑每天工作时长')
					this.currentTime = this.currentDayTime
					this.dayOrWeek = 'day'
					this.show = true
					// this.currentDayTime = this.currentTime
				} else if (data == 'week') {
					console.log('编辑每周工作时长')
					this.currentTime = this.currentWeekTime
					this.dayOrWeek = 'week'
					this.show = true
					// this.currentWeekTime = this.currentTime
				}
			},
			onClose() {
				this.show = false
			},
			onCancel() {
				this.onClose()
			},
			onCloseWorkTime() {
				this.workTimePreferShow = false
			},
			onConfirmWorkTimePrefer(event) {
				if (this.startOrEnd == 'start') {
					this.starttemp = event.detail
				} else {
					this.endtemp = event.detail
				}
				this.isEditStartAndEnd = false
				// this.onCloseWorkTime()
			},
			onCancelWorkTimePrefer() {
				this.isEditStartAndEnd = false
				// this.onCloseWorkTime()
			},
			editaddtemp(data) {
				if (data == 'start') {
					this.workTimePreferTime = this.starttemp
					this.startOrEnd = 'start'
					this.isEditStartAndEnd = true
				} else if (data == 'end') {
					this.workTimePreferTime = this.endtemp
					this.startOrEnd = 'end'
					this.isEditStartAndEnd = true
				}
			},
			saveaddtemp() {
				console.log('保存')
				this.workTimePrefer.push({
					'workTimePreferStartTime': this.starttemp,
					'workTimePreferEndTime': this.endtemp
				})
				this.onCloseWorkTime()
				this.saveIsDisable = false
			},
			saveCustom() {
				console.log('保存自定义内容')
				console.log('工作日偏好：' + this.value)
				console.log('工作时间偏好' + this.workTimePrefer)
				console.log('班次时长偏好：每天：' + this.currentDayTime + '每周：' + this.currentWeekTime)
				// 工作日数据保存
				let workDayPreference = ""
				for (let i = 0; i < this.value.length - 1; i++) {
					workDayPreference += this.value[i] + "|"
				}
				workDayPreference += this.value[this.value.length - 1]
				this.userinfo.workDayPreferenceList = this.value
				this.userinfo.workDayPreference = workDayPreference

				// 工作时间偏好
				let workTimePreference = ""
				for (let i = 0; i < this.workTimePrefer.length - 1; i++) {
					workTimePreference += this.workTimePrefer[i].workTimePreferStartTime + "~" + this.workTimePrefer[i]
						.workTimePreferEndTime + "|"
				}
				workTimePreference += this.workTimePrefer[this.workTimePrefer.length - 1].workTimePreferStartTime + "~" +
					this.workTimePrefer[this.workTimePrefer.length - 1].workTimePreferEndTime
				this.userinfo.workTimePreference = workTimePreference

				// 班次时间偏好
				this.userinfo.shiftLengthPreferenceOneDay = this.currentDayTime
				this.userinfo.shiftLengthPreferenceOneWeek = this.currentWeekTime
				this.userinfo.shiftLengthPreference = this.userinfo.shiftLengthPreferenceOneDay + "|" + this.userinfo
					.shiftLengthPreferenceOneWeek

				console.log('工作偏好数据封装')
				console.log('工作日偏好：' + workDayPreference)
				console.log('工作时间偏好:' + workTimePreference)
				console.log('班次时长偏好：' + this.userinfo.shiftLengthPreference)
				uni.setStorageSync('userinfo', this.userinfo)
				// const baseurl = 'http://localhost:6001'
				const baseurl = 'http://192.168.43.213:6001'
				const token = uni.getStorageSync('user')
				userApi.update(this.userinfo).then((res) => {
					console.log('res:' + JSON.stringify(res))
					if (res.code == 200) {
						console.log('编辑工作偏好信息成功')
						// console.log(res.data)

						Toast({
							type: 'success',
							message: '修改成功',
							onClose: () => {
								this.saveIsDisable = true
							},
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
				// 		console.log('res:' + JSON.stringify(res))
				// 		if(res.data.code==200){
				// 			console.log('编辑工作偏好信息成功')
				// 			console.log(res.data.data)

				// 			Toast({
				// 			  type: 'success',
				// 			  message: '修改成功',
				// 			  onClose: () => {
				// 				this.saveIsDisable = true
				// 			  },
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