<template>
  <div class="redisContainer">
    <el-row :gutter="10" style="margin-top: 10px;">
      <el-col :span="24" class="elCol">
        <el-card class="cardCss">
          <div slot="header"><span>基本信息</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium transparent">
            <table cellspacing="0" style="width: 100%" class="transparent">
              <tbody class="transparent">
                <tr class="transparent">
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell">Redis版本</div>
                  </td>
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell" v-if="cache.info">{{ cache.info.redis_version }}</div>
                  </td>
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell">运行模式</div>
                  </td>
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell" v-if="cache.info">{{ cache.info.redis_mode == "standalone" ? "单机" : "集群" }}</div>
                  </td>
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell">端口</div>
                  </td>
                  <td class="el-table__cell is-leaf transparent">
                    <div class="cell" v-if="cache.info">{{ cache.info.tcp_port }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">客户端数</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.connected_clients }}</div>
                  </td>
                </tr>
                <tr class="transparent">
                  <td class="el-table__cell is-leaf">
                    <div class="cell">运行时间(天)</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.uptime_in_days }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">使用内存</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.used_memory_human }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">使用CPU</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ parseFloat(cache.info.used_cpu_user_children).toFixed(2) }}
                    </div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">内存配置</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.maxmemory_human }}</div>
                  </td>
                </tr>
                <tr class="transparent">
                  <td class="el-table__cell is-leaf">
                    <div class="cell transparent">AOF是否开启</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell transparent" v-if="cache.info">{{ cache.info.aof_enabled == "0" ? "否" : "是" }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell transparent">RDB是否成功</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell transparent" v-if="cache.info">{{ cache.info.rdb_last_bgsave_status }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell transparent">Key数量</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.dbSize">{{ cache.dbSize }} </div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">网络入口/出口</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.instantaneous_input_kbps
                    }}kps/{{ cache.info.instantaneous_output_kbps }}kps</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="elCol" style="margin-top: 10px;">
        <el-card class="cardCss">
          <div slot="header"><span>命令统计</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium transparent">
            <div ref="commandstats" style="height: 420px" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="elCol" style="margin-top: 10px;">
        <el-card class="cardCss">
          <div slot="header">
            <span>内存信息</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium transparent">
            <div ref="usedmemory" style="height: 420px" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
  
<script>
import cacheMonitorApi from "@/api/statistics/cacheMonitor";
import * as echarts from 'echarts'

export default {
  name: "Server",
  data() {
    return {
      // 统计命令信息
      commandstats: null,
      // 使用内存
      usedmemory: null,
      // cache信息
      cache: [],
    };
  },
  created() {
    this.getList();
    this.openLoading();
  },
  methods: {
    /** 查缓存询信息 */
    getList() {
      cacheMonitorApi.getRedisCacheInfo().then((response) => {
        this.cache = response.result;
        // this.$modal.closeLoading();

        this.commandstats = echarts.init(this.$refs.commandstats, "macarons");
        this.commandstats.setOption({
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} ({d}%)",
          },
          series: [
            {
              name: "命令",
              type: "pie",
              roseType: "radius",
              radius: [15, 95],
              center: ["50%", "38%"],
              data: response.result.commandStats,
              animationEasing: "cubicInOut",
              animationDuration: 1000,
            },
          ],
        });
        this.usedmemory = echarts.init(this.$refs.usedmemory, "macarons");
        this.usedmemory.setOption({
          tooltip: {
            formatter: "{b} <br/>{a} : " + this.cache.info.used_memory_human,
          },
          series: [
            {
              name: "峰值",
              type: "gauge",
              min: 0,
              max: 1000,
              detail: {
                formatter: this.cache.info.used_memory_human,
              },
              data: [
                {
                  value: parseFloat(this.cache.info.used_memory_human),
                  name: "内存消耗",
                },
              ],
            },
          ],
        });
      });
    },
    // 打开加载层
    openLoading() {
      // this.$modal.loading("正在加载缓存监控数据，请稍候！");
    },
  },
};
</script>
<style lang="scss" scoped>
.redisContainer {
  .elCol {
    .cardCss {
      background: rgba(255, 255, 255, 0.6);
      border-radius: 10px;
      box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2);

      .transparent {
        background: rgba(255, 255, 255, 0);
      }
    }

  }


}

.transparent {
  background: rgba(255, 255, 255, 0);
}
</style>
  