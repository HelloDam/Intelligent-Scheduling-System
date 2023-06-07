<template>
	<view class="container" :style="[{height:windowHeight+'px'}]">
		<view class="CustomBar" :style="[{ height: CustomBar + 'px' }]">
			<view class="custitile">
				<view class="action" style="color:#FFF">智能排班系统</view>
			</view>
		</view>
		<image class="backgroundTexture" mode="scaleToFill" :style="[{ height: CustomBar + 130 + 'px' }]"
			src="/static/mainBG.png" />
		<view class="ev-mainBody">
			<view class="ev-fr-start padding-tb-sm">
				<image class="todayCourse" mode="heightFix" src="/static/daily_schedule.png" />
				<text class="padding-xs todaytitle" style="color:#FFF">今日我的排班</text>
			</view>
			<view v-if="user==='' " class="todaytips">未登录，无排班信息~</view>
			<view v-else-if="scheduleInfo.length === 0" class="todaytips">今天没有班次,好好休息吧~</view>
			<view v-else class="todaytips">今日共{{scheduleInfo.length}}班次，请注意上班时间～</view>
		</view>
		<block>
			<view class="topBar"></view>
			<view class="topBarShadow"></view>
			<view class="courseBox" :style="bgStyle">
				<block>
					<view class="courseInfoBox">
						<view class="cu-timeline" style="background:none">
							<view v-if="user===''" class="loginnull">
								<image style="width:100%;height:100%" mode="scaleToFill" src="/static/coursenull.png" />
								<!-- <text wx:if="{{!userInfo.courseInfo || userInfo.courseInfo.length == 0}}" class="todaytips">
									还没录入课程哦~
								</text> -->
								<text>请先登录！</text>
							</view>
							<view v-if="user!='' && scheduleInfo.length===0" class="loginnull">
								<image style="width:100%;height:100%" mode="scaleToFill" src="/static/coursenull.png" />
								<!-- <text wx:if="{{!userInfo.courseInfo || userInfo.courseInfo.length == 0}}" class="todaytips">
									还没录入课程哦~
								</text> -->
								<text>今日无班次，好好放松吧～</text>
							</view>
							<block v-else>
								<view class="cu-item text-blue ev-cuItem" v-for="(item, index) in scheduleInfo"
									v-key="index">
									<view class="scheduleinfo ev-fr-start">
										<text class="scheduleTime">{{item.showTimeStamp}}</text>
										<view class="scheduleRemind" style="background:#E2FFF9;color:#08d8d1"
											v-if="item.remind===1">
											即将开始
										</view>
										<view class="scheduleRemind" style="background:#EAF5FF;color:#5e99fb"
											v-if="item.remind===2">
											进行中
										</view>
										<view class="scheduleRemind" style="background:#f6f6f9;color:#92979d"
											v-if="item.remind===3">
											已结束
										</view>
									</view>
									<view class="bg-blue scheduleinfoBox"
										style="background-color:#5297eb;color: white;">
										<view class="image-logo">
											<image mode="scaleToFill" src="/static/sss_logo.png" />
										</view>
										<view class="padding-sm">
											<view>{{item.workType}}</view>
											<view v-if="item.mealType===0"
												style="background:#EAF5FF;color:#08d8d1;border-radius: 10rpx;text-align: center;margin-top: 10rpx;font-size: 12px;padding: 5rpx 0rpx;">
												午 餐</view>
											<view v-if="item.mealType===1"
												style="background:#EAF5FF;color:#dd5044;border-radius: 10rpx;text-align: center;margin-top: 10rpx;font-size: 12px;padding: 5rpx 0rpx;">
												晚 餐</view>
										</view>
									</view>
								</view>
							</block>
						</view>
					</view>
				</block>
			</view>
		</block>
	</view>
</template>

<script>
	import schedulingshiftApi from '@/common/api/schedulingshift.js';
	export default {
		computed: {
			style() {
				const {
					StatusBar,
					CustomBar
				} = this;
				return `height:${CustomBar}px;padding-top:${StatusBar}px;`;
			},
			bgStyle() {
				const {
					windowHeight,
					CustomBar
				} = this;
				return `height:${windowHeight-CustomBar-170}px;background:#FFFFFF;`;
			}
		},
		data() {
			return {
				CustomBar: 0,
				displayArea: {},
				StatusBar: this.StatusBar,
				CustomBar: this.CustomBar,
				windowHeight: this.windowHeight,
				// CustomBarStyle: 'background-color:#4d7eeb;width:100%;',
				CustomBarStyle: '',
				CustomBarStyle2: '',
				// bgStyle:'',
				courseBoxStyle: '',
				scheduleRemindStyle: '',
				scheduleInfo: [],
				// scheduleInfo: [
				// 	{
				// 		scheduleName:'上午',
				// 		startTime:'08:00',
				// 		endTime:'08:30',
				// 		remind: 3
				// 	},{
				// 		scheduleName:'下午',
				// 		startTime:'12:00',
				// 		endTime:'13:30',
				// 		remind: 2
				// 	},{
				// 		scheduleName:'下午',
				// 		startTime:'14:00',
				// 		endTime:'14:30',
				// 		remind: 1
				// 	},{
				// 		scheduleName:'下午',
				// 		startTime:'17:00',
				// 		endTime:'17:30',
				// 		remind: 1
				// 	}
				// ],
				user: '',
				userInfo: [{
					courseInfo: []
				}],

			}
		},
		onShow() {
			this.user = uni.getStorageSync('user')
			this.getTodayTask()
		},
		onLoad() {
			this.test()
			this.user = uni.getStorageSync('user')
			console.log(this.user == '')
			this.getTodayTask()
		},
		methods: {
			getTodayTask() {
				console.log('获取今天的排班信息')
				this.scheduleInfo = []
				// 查今天的排班信息
				const token = uni.getStorageSync('user')
				schedulingshiftApi.getTodayShiftListOfUser().then((res) => {
					console.log('获取今天的排班信息请求')
					console.log(res)
					this.scheduleInfo = res.shiftList
					console.log(this.scheduleInfo)
					for (let i = 0; i < this.scheduleInfo.length; i++) {
						let now = new Date()
						let stime = new Date(this.scheduleInfo[i].startDate)
						let etime = new Date(this.scheduleInfo[i].endDate)
						if (now > stime && now > etime) {
							console.log('任务已结束')
							this.scheduleInfo[i].remind = 3
						} else if (now > stime && now < etime) {
							console.log('任务进行中')
							this.scheduleInfo[i].remind = 2
						} else {
							console.log('任务未开始')
							this.scheduleInfo[i].remind = 1
						}
						this.scheduleInfo[i].workType = '正常班'
						let sh = stime.getHours()
						sh = sh < 10 ? '0' + sh : sh
						let eh = etime.getHours()
						eh = eh < 10 ? '0' + eh : eh
						let sm = stime.getMinutes()
						sm = sm < 10 ? '0' + sm : sm
						let em = etime.getMinutes()
						em = em < 10 ? '0' + em : em
						// console.log(stime.getMinutes())
						console.log(sh + ':' + sm + '-' + eh + ':' + em)
						this.scheduleInfo[i].showTimeStamp = sh + ':' + sm + '-' + eh + ':' + em
					}
					console.log(this.scheduleInfo)
				}).catch(() => {
					console.log('请求失败')
				})


				// uni.request({
				// 	url:baseurl + '/shift-scheduling-calculate-service/schedulingshift/getTodayShiftListOfUser',
				// 	method:'GET',
				// 	header:{
				// 		'token': token
				// 	},
				// 	success: (res) => {
				// 		console.log(res)
				// 		this.scheduleInfo = res.data.shiftList
				// 		console.log(this.scheduleInfo)
				// 		for(let i = 0; i < this.scheduleInfo.length; i++){
				// 			let now = new Date()
				// 			let stime = new Date(this.scheduleInfo[i].startDate)
				// 			let etime = new Date(this.scheduleInfo[i].endDate)
				// 			if(now>stime && now>etime){
				// 				console.log('任务已结束')
				// 				this.scheduleInfo[i].remind = 3
				// 			}else if(now>stime && now<etime){
				// 				console.log('任务进行中')
				// 				this.scheduleInfo[i].remind = 2
				// 			}else{
				// 				console.log('任务未开始')
				// 				this.scheduleInfo[i].remind = 1
				// 			}
				// 			this.scheduleInfo[i].workType = '正常班'
				// 			let sh = stime.getHours()
				// 			sh = sh < 10 ? '0' + sh : sh
				// 			let eh = etime.getHours()
				// 			eh = eh < 10 ? '0' + eh : eh
				// 			let sm = stime.getMinutes()
				// 			sm = sm < 10 ? '0' + sm : sm
				// 			let em = etime.getMinutes()
				// 			em = em < 10 ? '0' + em : em
				// 			// console.log(stime.getMinutes())
				// 			console.log(sh + ':' + sm + '-' + eh + ':' + em)
				// 			this.scheduleInfo[i].showTimeStamp = sh + ':' + sm + '-' + eh + ':' + em
				// 		}
				// 		console.log(this.scheduleInfo)
				// 	}
				// })
			},
			test() {
				console.log('test')
				uni.getSystemInfo({
					success: (e) => {
						// 获取手机状态栏的高度 statusBarHeight
						console.log(e.statusBarHeight)
						// 获取小程序下该菜单按钮的布局位置信息
						let menuButtonInfo = uni.getMenuButtonBoundingClientRect()
						// console.log(this.StatusBar)
						// console.log(this.CustomBar)
						console.log('ceshi')
						console.log(this.CustomBar)
						console.log(this.windowHeight - this.CustomBar - 50)
						// if (menuButtonInfo){
						// 	this.displayArea = {
						// 		windowHeight: e.windowHeight,
						// 		windowWidth: e.windowWidth,
						// 		screenHeight: e.screenHeight,
						// 	}
						// 	this.CustomBar = menuButtonInfo.bottom + menuButtonInfo.top - e.statusBarHeight;
						// 	console.log(this.CustomBar)
						// 	this.CustomBarStyle += 'height:'+this.CustomBar.toString()+'px'
						// 	this.CustomBarStyle2 += 'height:'+this.CustomBar.toString()+'px;padding-top:'+e.statusBarHeight.toString()+'px'
						// 	this.bgStyle += 'height:'+(this.CustomBar + 130).toString()+'px'
						// 	this.courseBoxStyle = 'height:'+(this.displayArea.windowHeight-this.CustomBar-150)+'px;background:#FFFFFF'

						// }else{
						// 	this.CustomBar = e.statusBarHeight + 50;
						// 	this.CustomBarStyle += 'height:'+this.CustomBar.toString()+'px'
						// 	this.CustomBarStyle2 += 'height:'+this.CustomBar.toString()+'px;padding-top:'+e.statusBarHeight.toString()+'px'
						// 	this.bgStyle += 'height:'+(this.CustomBar + 130).toString()+'px'
						// }
					}
				})
			}
		}
	}
</script>

<style>
	page {
		background: linear-gradient(to top, #8abffe 0%, #4d7eeb 100%);
	}

	.CustomBar {
		display: flex;
		position: relative;
		/* flex-direction: row; */
		justify-content: flex-start;
		align-items: center;
		margin-left: 16rpx;
		bottom: 0;
	}

	.custitile {
		position: absolute;
		bottom: 10rpx;
	}

	.backgroundTexture {
		position: fixed;
		top: 0;
		width: 100%;
		z-index: -1;
	}

	.ev-mainBody {
		padding: 0 32rpx;
	}

	.ev-fr-start {
		display: flex;
		flex-direction: row;
		justify-content: flex-start;
		align-items: center;
		flex-wrap: nowrap;
	}

	.padding-tb-sm {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.todayCourse {
		width: 140rpx;
		height: 140rpx;
	}

	.padding-xs {
		padding: 10rpx;
	}

	.todaytitle {
		font-size: 40rpx;
		font-weight: 600;
		line-height: 56rpx;
		color: #ffffff;
		margin-left: 6rpx;
	}

	.todaytips {
		font-size: 28rpx;
		font-weight: 400;
		line-height: 40rpx;
		color: #ffffff;
		opacity: 0.8;
	}

	.topBar {
		width: 720rpx;
		height: 30rpx;
		background-color: #5e99fb;
		border-radius: 12rpx;
		margin: 40rpx auto;
	}

	.topBarShadow {
		position: relative;
		top: 0;
		width: 686rpx;
		height: 50rpx;
		margin: -56rpx auto 55rpx;
		z-index: 99;
		background: linear-gradient(rgba(12, 62, 165, 0.6) 0%,
				rgba(12, 62, 165, 0.01) 100%);
	}

	.courseBox {
		width: 686rpx;
		overflow: auto;
		margin: -104rpx auto 0;
		border-radius: 0 0 24rpx 24rpx;
		box-shadow: 0 4rpx 20rpx rgba(12, 62, 165, 0.3);
	}

	/* .courseBox {
  width: 686rpx;
  overflow: auto;
  margin: -104rpx 32rpx 0;
  border-radius: 0 0 24rpx 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(12, 62, 165, 0.3);
} */

	/* ==================
         时间轴
 ==================== */

	/* .cu-timeline {
  display: block;
  background-color: white;
} */
	/* .cu-timeline .cu-time {
  width: 120rpx;
  text-align: center;
  padding: 20rpx 0;
  font-size: 26rpx;
  color: #888;
  display: block;
} */
	.cu-timeline>.cu-item {
		padding: 30rpx 30rpx 30rpx 120rpx;
		position: relative;
		display: block;
		z-index: 0;
	}

	.cu-timeline>.cu-item:not([class*="text-"]) {
		color: #ccc;
	}

	.cu-timeline>.cu-item::after {
		content: "";
		display: block;
		position: absolute;
		width: 1rpx;
		background-color: #ddd;
		left: 60rpx;
		height: 100%;
		top: 0;
		z-index: 8;
	}

	.cu-timeline>.cu-item::before {
		font-family: "cuIcon";
		display: block;
		position: absolute;
		top: 36rpx;
		z-index: 9;
		background-color: white;
		width: 50rpx;
		height: 50rpx;
		text-align: center;
		border: none;
		line-height: 50rpx;
		left: 36rpx;
	}

	.cu-timeline>.cu-item:not([class*="cuIcon-"])::before {
		content: "⚪";

		position: absolute;
		top: 15px;
	}


	.cu-timeline>.cu-item::before {
		background: none;
	}

	.ev-cuItem:last-child::after {
		height: 30rpx;
	}

	.courseBox .courseInfoBox {
		/* margin-bottom: 10rpx; */
	}

	.courseBox .courseInfoBox .scheduleinfo {
		margin: 10rpx 0 20rpx;
	}

	.ev-fr-start {
		display: flex;
		flex-direction: row;
		justify-content: flex-start;
		align-items: center;
		flex-wrap: nowrap;
	}

	.courseBox .courseInfoBox .scheduleinfo .scheduleTime {
		font-size: 28rpx;
		font-weight: 500;
		line-height: 40rpx;
		color: #282b2f;
	}

	.courseBox .courseInfoBox .scheduleinfo .scheduleRemind {
		width: 108rpx;
		height: 40rpx;
		background: #eaf5ff;
		border-radius: 20rpx;
		font-size: 20rpx;
		font-weight: 400;
		line-height: 40rpx;
		color: #5e99fb;
		text-align: center;
		margin-left: 12rpx;
	}

	.bg-blue {
		background-color: var(--blue);
		color: var(--white);
	}

	.courseBox .courseInfoBox .scheduleinfoBox {
		display: flex;
		align-items: center;
		width: 350rpx;
		height: 130rpx;
		box-shadow: 0 4rpx 20rpx rgba(8, 216, 209, 0.4);
		opacity: 1;
		border-radius: 14rpx;
	}

	/* .courseBox .courseInfoBox .scheduleinfoBox .timelineIcon {
  position: relative;
  width: 90rpx;
  height: 90rpx;
  top: -90rpx;
  right: -210rpx;
} */
	.padding-sm {
		padding: 20rpx;
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

	.courseBox .courseInfoBox .scheduleinfoBox .timelineIcon {
		position: relative;
		width: 90rpx;
		height: 90rpx;
		top: -100rpx;
		right: -250rpx;
	}

	.image-logo image {
		width: 90rpx;
		height: 90rpx;
		margin-left: 20rpx;
	}
</style>