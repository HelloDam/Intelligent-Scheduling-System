package com.dam.controller.monitor;

import com.dam.model.entity.system.monitor.Server;
import com.dam.model.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 服务器监控
 * 
 * @author ktg
 */
@RestController
@RequestMapping("/system/monitor")
public class ServerController
{
//    @PreAuthorize("@ss.hasPermi('monitor.server.list')")
    @GetMapping("/getServerInfo")
    public R getServerInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return R.ok().addData("server",server);
    }
}
