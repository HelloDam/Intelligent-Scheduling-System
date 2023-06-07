<template>
	<view class="scheduleContainer" :style="[{height: windowHeight - 10 + 'px'}]">
		<view class="headBox">
			<view :style="[{height:CustomBar + 'px'}]">
				<view class="cu-bar" :style="[{padding:StatusBar + 'px 0px 0px 0px'}]">
					<view class="action" bindtap="weeksSwitch">
						<view class="ev-fc-cstart">
							<view class="weeks ev-fr-start">
								{{showyear}}年{{showmonth}}月
								<!-- <text wx:if="{{currentWeek == showWeek}}">(本周)</text> -->
								<!-- <text class="cuIcon-unfold" style="color: #1a1b1c;" />
		                        <view wx:if="{{currentWeek !== showWeek}}" class="text-blue margin-left-xs" catch:tap="backToCurrent" style="font-size: 24rpx;">
		                            回到当前周
		                        </view> -->
							</view>
							<view class="switchCouples">
								<!-- {{gradeList[userInfo.grade].label}} 第{{semester}}学期 -->
								第{{showWeek}}周
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="ev-relative" style="height: 50px;">
			<!-- <view class="flex solid-bottom padding align-center bg-white" >
		        <image class="ev-icon" mode="scaleToFill"  />
		        <text class="switchCouples margin-left-xs">切换回我的课表</text>
		    </view> -->
			<view class="ev-fr-start">
				<view class="monthBox ev-fc-center">
					{{showmonth}}
					<view class="tipsText">月</view>
				</view>
				<view class="flex  justify-between align-center weekdate">
					<view v-for="(item, index) in ['一','二','三','四','五','六','日']" v-key="index"
						:class="[ 'ev-fc', 'calendarDate',realtoday===weeksList[index]?'selectedDate':'']">
						<text>周{{item}}</text>
						<text v-if="realtoday===weeksList[index]" class="tipsText selectHiglLight">今天</text>
						<text v-else class="tipsText">{{weeksList[index].substring(5)}}</text>
					</view>
				</view>
			</view>
		</view>

		<view class="calenTime" :style="[{height: windowHeight - CustomBar - 50 + 'px'}]">
			<view class="flex  justify-start bg-white">
				<!-- <schedule-content :weekList="weeksList" :today="today" @toParent="getCurrWeekList"></schedule-content> -->
				<view class="leftcontainer">
					<!-- <view class="timest" v-for="(item,index) in classTime" v-key="index" :style="[{ height: (6 ) + 'px' }]" ></view> -->
					<view id="leftView" v-for="item in itmes" v-key="item.id" class="ev-fc-start chuizhijuz"
						:style="[{height:(windowHeight-CustomBar-50)/itmes.length+'px'}]">
						<!-- <text class="classTimeText">——</text> -->
						<text class="classTimeText" style="">{{item.time}}-</text>
						<!-- <text v-if="item.id===7" class="classTimeText" style="margin-top: 100%;">{{item.endtime}}</text> -->
					</view>
				</view>

				<view v-if="user===''" class="loginnull">
					<image style="width:100%;height:100%" mode="scaleToFill" src="/static/coursenull.png" />
					<!-- <text wx:if="{{!userInfo.courseInfo || userInfo.courseInfo.length == 0}}" class="todaytips">
						还没录入课程哦~
					</text> -->
					<text>请先登录！</text>
				</view>

				<block v-else>
					<view class="flex-row content-col" :style="[{margin: leftViewHeight/2 + 'px 0px' }]"
						@touchstart="start" @touchend="end">
						<view id="contentView" class="scheduleWeekDay"
							v-for="(item,index) in ['1','2','3','4','5','6','7']">
							<block v-for="(classItem, index2) in classTime" v-key="index2">
								<block v-if="classItem.dayOfWeek===index+1">
									<view class="classTimeText" v-for="(taskitem,taskindex) in classItem.task">
										<!-- :style="[{ margin: (10 +(taskindex)*50) + 'px 0px 0px 0px'  }]" -->
										<!-- <view v-if="taskindex===0 & taskitem.timeStart==='08:00'" class="task-block" :style="[{height:(taskitem.timeDifference*((viewHeight-leftViewHeight)/840))+'px'}]">{{taskitem.timeStart}}</view> -->
										<view v-if="taskindex===0 " class="task-block"
											:style="[{height:(taskitem.timeDifference*((viewHeight-leftViewHeight)/earliestAndLatestOffset))+'px'},{margin:  (taskitem.timeStartToLast*(viewHeight-leftViewHeight)/earliestAndLatestOffset) + 'px 0px 0px 0px'}]">
											{{taskitem.timeStart}}
										</view>
										<view v-else class="task-block"
											:style="[{ margin: taskitem.timeStartToLast*((viewHeight-leftViewHeight)/earliestAndLatestOffset) + 'px 0px 0px 0px' },{height:(taskitem.timeDifference*((viewHeight-leftViewHeight)/earliestAndLatestOffset))+'px'}]">
											{{taskitem.timeStart}}
										</view>
									</view>
								</block>

							</block>
						</view>
					</view>
				</block>
			</view>
		</view>
		<van-toast id="van-toast" />
		<uni-fab ref="fab" :pattern="pattern" :content="content" horizontal="right" vertical="bottom"
			direction="horizontal" @trigger="trigger" @fabClick="fabClick" />
	</view>
</template>

<script>
	import Toast from '@/wxcomponents/vant/dist/toast/toast.js';
	import schedulingshiftApi from '@/common/api/schedulingshift.js';
	export default {
		computed: {

		},
		data() {
			return {
				StatusBar: this.StatusBar,
				CustomBar: this.CustomBar,
				todayWeek: 1,
				showyear: 1,
				showmonth: 1,
				showWeek: 1,
				today: '',
				realtoday: '',
				weeksList: [], // 当前周日期列表
				windowHeight: this.windowHeight,
				screemWidth: this.screenWidth,
				scrollTop: 0,
				old: {
					scrollTop: 0
				},
				classTime: [{
						dayOfWeek: 1, // 星期
						task: [{
							timeStart: '08:00', // 起始时间
							timeEnd: '08:30', // 结束时间
							timeStartToLast: 0, // 该任务的起始时间距离这一天中该任务上一个任务结束的时间差距
							timeDifference: 30 // 该班次任务的时间差
						}, {
							timeStart: '10:00',
							timeEnd: '11:00',
							timeStartToLast: 90,
							timeDifference: 60
						}, {
							timeStart: '13:00',
							timeEnd: '14:30',
							timeStartToLast: 120,
							timeDifference: 90
						}, {
							timeStart: '16:00',
							timeEnd: '16:30',
							timeStartToLast: 90,
							timeDifference: 30
						}, {
							timeStart: '16:35',
							timeEnd: '18:00',
							timeStartToLast: 5,
							timeDifference: 85
						}]

					},
					{
						dayOfWeek: 2,
						task: [{
							timeStart: '09:00',
							timeEnd: '09:30',
							timeStartToLast: 60,
							timeDifference: 50
						}]

					}
				],
				itmes: [{
						id: 1,
						time: "8:00",
						endtime: "10:00"
					},
					{
						id: 2,
						time: "9:00",
						endtime: "12:00"
					},
					{
						id: 3,
						time: "10:00",
						endtime: "14:00"
					},
					{
						id: 4,
						time: "11:00",
						endtime: "16:00"
					},
					{
						id: 5,
						time: "12:00",
						endtime: "18:00"
					},
					{
						id: 6,
						time: "13:00",
						endtime: "20:00"
					},
					{
						id: 7,
						time: "14:00",
						endtime: "22:00"
					},
					{
						id: 8,
						time: "15:00",
						endtime: "22:00"
					},
					{
						id: 9,
						time: "16:00",
						endtime: "22:00"
					},
					{
						id: 10,
						time: "17:00",
						endtime: "22:00"
					},
					{
						id: 11,
						time: "18:00",
						endtime: "22:00"
					},
					{
						id: 12,
						time: "19:00",
						endtime: "22:00"
					},
					{
						id: 13,
						time: "20:00",
						endtime: "22:00"
					},
					{
						id: 14,
						time: "21:00",
						endtime: "22:00"
					},
					{
						id: 15,
						time: "22:00",
						endtime: "22:00"
					}

				],
				startData: {
					clientX: 0,
					// clientY:0
				},
				cur_day: '',
				viewHeight: 0,
				leftViewHeight: 0,
				user: '',
				earliestStartTime: '',
				latestEndTime: '',
				earliestAndLatestOffset: 0,
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF',
					iconColor: '#fff'
				},
				content: [{
					iconPath: '/static/bcakToWeek_active.png',
					selectedIconPath: '/static/bcakToWeek_active.png',
					text: '回到本周',
					active: true
				}]
			};
		},
		onTabItemTap() {
			this.user = uni.getStorageSync('user')
			// this.getLocationInfo()
			this.getCurrWeekList(new Date())
			this.realtoday = this.today
			this.cur_day = this.today
			this.getWeekTasks()
		},
		onReady() {
			this.user = uni.getStorageSync('user')
			this.getLocationInfo()
			this.getCurrWeekList(new Date())
			this.realtoday = this.today
			this.cur_day = this.today
			this.getWeekTasks()
		},
		// onLoad() {
		// 	this.user = uni.getStorageSync('user')
		// 	this.getLocationInfo()
		// 	this.getCurrWeekList(new Date())
		// 	this.realtoday = this.today
		// 	this.cur_day = this.today
		// 	this.getWeekTasks()
		// },
		methods: {
			getLocationInfo() {
				let self = this
				const query = uni.createSelectorQuery().in(self)
				query.select('#contentView').boundingClientRect(function(rect) {
					console.log('获取组件高度')
					self.viewHeight = rect.height
					console.log(self.viewHeight)
					console.log('获取组件高度')
				}).exec()
				query.select('#leftView').boundingClientRect(function(rect) {
					console.log('获取左时间轴组件高度')
					self.leftViewHeight = rect.height
					console.log(self.leftViewHeight)
					console.log('获取左时间轴组件高度')
				}).exec()
			},
			scroll: function(e) {
				console.log(e)
				this.old.scrollTop = e.detail.scrollTop
			},
			formateDate(time) {
				let year = time.getFullYear();
				let month = time.getMonth() + 1 < 10 ? '0' + (time.getMonth() + 1) : (time.getMonth() + 1);
				let day = time.getDate() < 10 ? '0' + time.getDate() : time.getDate();
				return year + '-' + month + '-' + day;
			},

			getCurrWeekList(data) {
				//根据日期获取本周周一~周日的年-月-日
				console.log(data)
				let weekListData = [],
					date = new Date(data)
				console.log(date)
				this.today = this.formateDate(date)
				this.showmonth = date.getMonth() + 1
				console.log(this.today)
				//获取当前日期为周一的日期
				date.setDate(date.getDay() == "0" ? date.getDate() - 6 : date.getDate() - date.getDay() + 1);
				// push周一数据
				weekListData.push(this.formateDate(date));
				console.log(weekListData)
				//push周二以后日期
				for (var i = 0; i < 6; i++) {
					date.setDate(date.getDate() + 1);
					weekListData.push(this.formateDate(date));
				}
				console.log(weekListData)
				this.weeksList = weekListData
				this.showyear = date.getFullYear()
				this.getMonthWeek(data)
				console.log(this.showmonth)
				console.log(date.getFullYear())
				console.log(this.weeksList)
			},
			getMonthWeek(data) {
				let date = new Date(data);
				let w = date.getDay();
				let d = date.getDate();
				if (w == 0) w = 7;
				let weekOfMonth = Math.ceil((d + 6 - w) / 7)
				this.showWeek = weekOfMonth
			},
			find_date(num, data) {
				let date1 = new Date(data);
				date1.setDate(date1.getDate() + num);
				let time2 =
					date1.getFullYear() +
					"-" +
					(date1.getMonth() + 1) +
					"-" +
					date1.getDate();
				// console.log(time2)
				return time2
			},
			start(e) {
				this.startData.clientX = e.changedTouches[0].clientX;
				// this.startData.clientY=e.changedTouches[0].clientY;
			},
			end(e) {
				// console.log(e)
				const subX = e.changedTouches[0].clientX - this.startData.clientX;
				// const subY=e.changedTouches[0].clientY - this.startData.clientY;
				if (subX > 50) {
					console.log('右滑')
					this.cur_day = this.find_date(-7, this.cur_day)
					console.log(this.cur_day)
					this.getCurrWeekList(this.cur_day)
					this.getWeekTasks()
					// this.toParent()
				} else if (subX < -50) {
					console.log('左滑')
					this.cur_day = this.find_date(7, this.cur_day)
					console.log(this.cur_day)
					this.getCurrWeekList(this.cur_day)
					this.getWeekTasks()
				} else {
					console.log('无效')
				}
			},
			getWeekTasks() {
				console.log('获取显示周的数据')
				console.log(this.weeksList)
				console.log(this.weeksList[0])
				// const baseurl = 'http://localhost:6001'
				const baseurl = 'http://192.168.43.213:6001'
				const token = uni.getStorageSync('user')
				let startDateStr = this.weeksList[0]
				let endDateStr = this.weeksList[6]
				schedulingshiftApi.getWeekShiftListOfUser(startDateStr, endDateStr)
					.then((res) => {
						console.log(res)

						this.earliestStartTime = res.earliestStartTime
						this.latestEndTime = res.latestEndTime
						this.earliestAndLatestOffset = res.earliestAndLatestOffset
						console.log(this.classTime)
						console.log(this.earliestStartTime)
						let timeItems = []
						console.log('时间段设置')
						let earliestStartTimeSplit = this.earliestStartTime.split(":")
						let latestEndTimeSplit = this.latestEndTime.split(":")
						console.log(parseInt(earliestStartTimeSplit[0]))
						console.log(latestEndTimeSplit)
						let starth = parseInt(earliestStartTimeSplit[0])
						let endh = parseInt(latestEndTimeSplit[0])
						let timelength = endh - starth
						// if(parseInt(latestEndTimeSplit[0])
						for (let i = 1; i < timelength + 1 + 1; i++) {
							let id = i
							let h = starth + i - 1
							h = h < 10 ? '0' + h : h
							let timeline = h + ':00'
							timeItems.push({
								'id': id,
								'time': timeline
							})
						}
						console.log(timeItems)
						this.itmes = timeItems
						this.classTime = res.classTime
						console.log(this.classTime)
					}).catch(() => {
						console.log('获取失败')
					})


				// uni.request({
				// 	url: baseurl + '/shift-scheduling-calculate-service/schedulingshift/getWeekShiftListOfUser',
				// 	method:'GET',
				// 	data:{
				// 		startDateStr, endDateStr
				// 		// startDateStr: this.weeksList[0],
				// 		// endDateStr: this.weeksList[6]
				// 	},
				// 	header:{
				// 		'token': token
				// 	},
				// 	success: (res) => {
				// 		console.log(res)

				// 		this.earliestStartTime = res.data.earliestStartTime
				// 		this.latestEndTime = res.data.latestEndTime
				// 		this.earliestAndLatestOffset = res.data.earliestAndLatestOffset
				// 		console.log(this.classTime)
				// 		console.log(this.earliestStartTime)
				// 		let timeItems = []
				// 		console.log('时间段设置')
				// 		let earliestStartTimeSplit = this.earliestStartTime.split(":")
				// 		let latestEndTimeSplit = this.latestEndTime.split(":")
				// 		console.log(parseInt(earliestStartTimeSplit[0]))
				// 		console.log(latestEndTimeSplit)
				// 		let starth = parseInt(earliestStartTimeSplit[0])
				// 		let endh = parseInt(latestEndTimeSplit[0])
				// 		let timelength = endh-starth
				// 		// if(parseInt(latestEndTimeSplit[0])
				// 		for(let i = 1; i<timelength+1+1; i++){
				// 			let id = i
				// 			let h = starth + i - 1
				// 			h = h < 10 ? '0'+h : h
				// 			let timeline = h + ':00'
				// 			timeItems.push({'id':id,'time':timeline})
				// 		}
				// 		console.log(timeItems)
				// 		this.itmes = timeItems
				// 		this.classTime = res.data.classTime
				// 		console.log(this.classTime)
				// 	},
				// 	fail: () => {
				// 		console.log('获取失败')
				// 	}
				// })
			},

			trigger(e) {
				if (this.user == '') {
					Toast({
						type: 'fail',
						message: '请先登录',
						onClose: () => {
							// this.popupClose()
							// this.isLogin = 1
						},
					});
					return
				}
				console.log(e)
				let self = this
				// this.content[e.index].active = true
				uni.showModal({
					title: '提示',
					content: `您确定要${e.item.text}吗？`,
					success: function(res) {
						if (res.confirm) {
							console.log('用户点击确定')
							self.getCurrWeekList(new Date())
							// this.realtoday = this.today
							self.cur_day = self.today
							self.getWeekTasks()
						} else if (res.cancel) {
							console.log('用户点击取消')
						}
					}
				})
			},
			fabClick() {

				// uni.showToast({
				// 	title: '点击了悬浮按钮',
				// 	icon: 'none'
				// })
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #fff;
	}

	.headBox {
		width: 750rpx;
		position: relative;
		background: #ffffff;
		padding-left: 10px;
	}

	.cu-bar {
		display: flex;
		position: relative;
		align-items: center;
		min-height: 100rpx;
		justify-content: space-between;
	}

	.cu-bar .action {
		display: flex;
		align-items: center;
		height: 100%;
		justify-content: center;
		max-width: 100%;
	}

	.ev-fc-cstart {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: flex-start;
		flex-wrap: nowrap;
	}

	.switchCouples {
		font-size: 24rpx;
		font-weight: 400;
		line-height: 33rpx;
		color: #92979d;
	}

	.ev-relative {
		position: relative;
	}

	.solid-bottom {
		position: relative;
	}

	.solid-bottom::after {
		content: " ";
		width: 200%;
		height: 200%;
		position: absolute;
		top: 0;
		left: 0;
		border-radius: inherit;
		transform: scale(0.5);
		transform-origin: 0 0;
		pointer-events: none;
		box-sizing: border-box;
		border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);
	}

	.ev-fr-start {
		display: flex;
		flex-direction: row;
		justify-content: flex-start;
		align-items: center;
		flex-wrap: nowrap;
	}

	.monthBox {
		width: 64rpx;
		height: 100rpx;
	}

	.ev-fc-center {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		flex-wrap: nowrap;
	}

	.tipsText {
		font-size: 20rpx;
		font-weight: 400;
		line-height: 28rpx;
		color: #92979d;
	}

	.justify-between {
		justify-content: space-between;
	}

	.align-center {
		align-items: center;
	}

	.ev-fc {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		align-items: center;
		flex-wrap: nowrap;
	}

	.weekdate {
		margin-left: auto;
		margin-right: auto;
	}

	.calendarDate {
		margin: 7rpx;
		width: 86rpx;
		height: 86rpx;
		border-radius: 14rpx;
		justify-content: center;

	}

	.selectedDate {
		background: #5e99fb;
		color: #fff;
	}

	.selectedDate .tipsText {
		color: #fff;
	}

	.flex {
		display: flex;
	}

	.content-col {
		margin-left: auto;
		margin-right: auto;
	}

	.justify-start {
		justify-content: flex-start;
	}

	.bg-white {
		background-color: white;
		color: darkGray;
	}

	.selectedDate {
		background: #5e99fb;
		color: #fff;
	}

	.selectedDate .tipsText {
		color: #fff;
	}

	.weeks {
		font-size: 32rpx;
		font-weight: 500;
		line-height: 45rpx;
		color: #1a1b1c;
	}

	.margin-top-sm {
		margin-top: 15px;
	}

	.leftcontainer {
		border-radius: 10rpx;
		box-sizing: border-box;
		display: flex;
		// justify-content: center;		
		flex-direction: column;
		align-items: center;
		text-align: center;
		// background-color: aqua;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
		// padding-bottom: 5px;
		border: 1px solid #5e99fb;
	}

	.ev-fc-start {
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
		align-items: center;
		flex-wrap: nowrap;
	}

	.chuizhijuz {
		display: flex;
		justify-content: center;
		flex-direction: column;
		align-items: center;
		text-align: center;
	}

	.classTimeText {
		align-self: center;
		font-size: 20rpx;
		font-weight: 400;
		line-height: 28rpx;
		color: #4d5560;
	}

	.flex-row {
		display: flex;
		// box-shadow: 0px 0px 5px 5px rgba(0, 0, 0, 0.2),
	}

	.scheduleWeekDay {
		border-radius: 14rpx;
		width: 86rpx;
		margin-left: 5px;
		// margin-top:16px;
		// background-color: white;

		padding: 0px;
		// justify-content: space-around;
		text-align: center;
	}

	.task-block {
		color: white;
		border-radius: 8rpx;
		background-color: #5e99fb;
		box-shadow: 0px 0px 5px 0px;
		// height: 50rpx;
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

	.calenTime {
		margin: 10rpx 6rpx;
	}
</style>