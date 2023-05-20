<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
      label-width="120px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <!-- <el-form-item label="省" prop="provinceId">
        <el-input v-model="dataForm.provinceId" placeholder="省"></el-input>
      </el-form-item>
      <el-form-item label="市" prop="cityId">
        <el-input v-model="dataForm.cityId" placeholder="市"></el-input>
      </el-form-item>
      <el-form-item label="区" prop="regionId">
        <el-input v-model="dataForm.regionId" placeholder="区"></el-input>
      </el-form-item> -->
      <el-form-item label="地区">
        <el-cascader v-model="province_city_region" :options="areaItemVoOptions" clearable filterable
          @change="changeArea"></el-cascader>
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input v-model="dataForm.address" placeholder="详细地址"></el-input>
      </el-form-item>
      <el-form-item label="场所面积(m²)" prop="size">
        <el-input v-model="dataForm.size" placeholder="工作场所面积"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel()">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import storeApi from '@/api/enterprise/store'
import provinceCityRegionApi from '@/api/enterprise/provinceCityRegion'
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
        provinceId: '',
        cityId: '',
        regionId: '',
        address: '',
        size: '',
        status: ''
      },
      dataRule: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ],
        provinceId: [
          { required: true, message: '省不能为空', trigger: 'blur' }
        ],
        cityId: [
          { required: true, message: '市不能为空', trigger: 'blur' }
        ],
        regionId: [
          { required: true, message: '区不能为空', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '详细地址不能为空', trigger: 'blur' }
        ],
        size: [
          { required: true, message: '请输入0到4位小数的正实数', pattern: /^[0-9]+(.[0-9]{0,4})?$/, trigger: 'blur' }
        ]
      },
      ////选择地区
      //省市区的树形结构数据
      areaItemVoOptions: [],
      //所选中的省市区
      province_city_region: "",
    }
  },
  created() {
    this.getAreaTree()
  },
  methods: {
    init(id) {
      // console.log("初始化数据,id:" + id)
      this.dataForm.id = id
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          storeApi.info(this.dataForm.id).then(res => {
            // console.log(JSON.stringify(res))
            this.dataForm.name = res.store.name
            this.dataForm.address = res.store.address
            this.province_city_region = new Array()
            this.province_city_region[0] = res.store.provinceId
            this.province_city_region[1] = res.store.cityId
            this.province_city_region[2] = res.store.regionId
            this.dataForm.size = res.store.size
          })
        }
      })
    },
    //取消表单提交
    cancel() {
      this.province_city_region = [];
      this.visible = false;
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
            'provinceId': this.dataForm.provinceId,
            'cityId': this.dataForm.cityId,
            'regionId': this.dataForm.regionId,
            'address': this.dataForm.address,
            'size': this.dataForm.size,
            'status': this.dataForm.status
          };

          if (this.dataForm.id) {
            storeApi.updateById(data).then(res => {
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
                this.province_city_region = []
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            storeApi.save(data).then(res => {
              if (res && res.code === this.ResultCode.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                })
                this.visible = false
                this.$emit('refreshDataList')
                this.province_city_region = []
              } else {
                this.$message.error(message.message)
              }
            })
          }
        }
      })
    },

    //获取省市区的树形结构数据
    getAreaTree() {
      provinceCityRegionApi.getAreaTree().then(
        res => {
          this.areaItemVoOptions = res.areaItemVoList
          for (let i = 0; i < this.areaItemVoOptions.length; i++) {
            this.setNullToUndefined(this.areaItemVoOptions[i])
          }
        }
      )
    },
    //将没有子元素的父类的chidren设置为 undefined
    setNullToUndefined(areaItemVo) {
      if (areaItemVo.children.length < 1) {
        areaItemVo.children = undefined;
      } else {
        for (let i = 0; i < areaItemVo.children.length; i++) {
          this.setNullToUndefined(areaItemVo.children[i])
        }
      }
    },
    //设置省市区的值
    changeArea() {

      // let arr = this.province_city_region.split(",")
      // console.log("修改区域")
      // console.log("this.province_city_region:" + this.province_city_region)
      if (this.province_city_region.length > 0) {
        this.dataForm.provinceId = this.province_city_region[0]
        this.dataForm.cityId = this.province_city_region[1]
        this.dataForm.regionId = this.province_city_region[2]
      } else {
        this.dataForm.provinceId = ""
        this.dataForm.cityId = ""
        this.dataForm.regionId = ""
      }

    }

  }
}
</script>
