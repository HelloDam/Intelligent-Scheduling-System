<template>
    <div>
      <canvas ref="canvas" width="120" height="60" @click="refresh" style="border-radius: 10px;"></canvas>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        input: "",
        placeholder: "请输入验证码",
        code: "",
        canvas: null,
        context: null
      }
    },
    mounted() {
      this.canvas = this.$refs.canvas
      this.context = this.canvas.getContext("2d")
      this.refresh()
    },
    methods: {
      refresh() {
        this.code = this.generateCode()
        this.draw()
      },
      generateCode() {
        const characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const length = 4
        let code = ""
        for (let i = 0; i < length; i++) {
          const index = Math.floor(Math.random() * characters.length)
          code += characters.charAt(index)
        }
        return code
      },
      draw() {
        const width = this.canvas.width
        const height = this.canvas.height
        this.context.clearRect(0, 0, width, height)
        this.context.fillStyle = "#ffffff"
        this.context.fillRect(0, 0, width, height)
        this.context.font = "20px Arial"
        for (let i = 0; i < this.code.length; i++) {
          const x = (i + 1) * width / (this.code.length + 1)
          const y = height / 2
          this.context.fillStyle = this.randomColor()
          this.context.fillText(this.code.charAt(i), x, y)
        }
      },
      randomColor() {
        const r = Math.floor(Math.random() * 256)
        const g = Math.floor(Math.random() * 256)
        const b = Math.floor(Math.random() * 256)
        return `rgb(${r},${g},${b})`
      },
      check() {
        if (this.input.toLowerCase() === this.code.toLowerCase()) {
          alert("验证成功！")
        } else {
          alert("验证失败，请重试。")
          this.refresh()
        }
        this.input = ""
      }
    }
  }
  </script>
  