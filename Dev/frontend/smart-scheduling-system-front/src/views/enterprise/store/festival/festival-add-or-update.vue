<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible" width="25%">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px" style="padding: 20px;">
      <el-form-item label="节日名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="节日名称"></el-input>
      </el-form-item>
      <el-form-item label="起始日期" prop="startDate">
        <el-date-picker v-model="dataForm.startDate" align="right" type="date" placeholder="选择起始日期" style="width: 100%;"
          :picker-options="pickerOptions" value-format="yyyy-MM-dd HH:mm:ss" format="MM-dd">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="截止日期" prop="endDate">
        <el-date-picker v-model="dataForm.endDate" align="right" type="date" placeholder="选择截止日期" style="width: 100%;"
          :picker-options="pickerOptions" value-format="yyyy-MM-dd HH:mm:ss" format="MM-dd">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="日期类型" prop="type">
        <el-select v-model="dataForm.type" placeholder="请选择" style="width: 100%;">
          <el-option v-for="item in dateOptions" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import festivalApi from '@/api/enterprise/festival'

export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        createTime: '',
        updateTime: '',
        isDeleted: '',
        name: '',
        startDate: '',
        endDate: '',
        storeId: '',
        type:undefined
      },

      //日期选择器选择范围
      pickerOptions: {
        disabledDate(time) {
          return false;
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date());
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
      //日期类型
      dateOptions: [
        { label: "农历", value: 0 },
        { label: "新历", value: 1 },
      ],

      dataRule: {
        name: [
          { required: true, message: '节日名称不能为空', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '起始日期不能为空', trigger: 'blur' }
        ],
        endDate: [
          { required: true, message: '截止日期不能为空', trigger: 'blur' }
        ],
        storeId: [
          { required: true, message: '门店id不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          festivalApi.info(this.dataForm.id).then(res => {
            // console.log(JSON.stringify(res))
            this.dataForm = res.festival;
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {

          let data = {
            'id': this.dataForm.id || undefined,
            'createTime': this.dataForm.createTime,
            'updateTime': this.dataForm.updateTime,
            'isDeleted': this.dataForm.isDeleted,
            'name': this.dataForm.name,
            'startDate': this.dataForm.startDate,
            'endDate': this.dataForm.endDate,
            'storeId': this.dataForm.storeId
          }


          if (this.dataForm.id) {
            festivalApi.updateById(data).then(res => {
              // console.log("this.ResultCode.success:" + this.ResultCode.success)
              // console.log("res:" + JSON.stringify(res))
              if (res.code === this.ResultCode.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
                this.visible = false
                this.$emit('refreshDataList')
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            festivalApi.save(data).then(res => {
              if (res && res.code === this.ResultCode.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
                this.visible = false
                this.$emit('refreshDataList')
              } else {
                this.$message.error(message.message)
              }
            })
          }


        }
      })
    }
  }
}
</script>
