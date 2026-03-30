package com.hotely5d.controller;

import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/data")
    public Result getDashboardData() {
        // 按照你项目的 Result 构造函数：Result(boolean flag, Integer code, String message, Object data)
        return new Result(true, StatusCode.OK, "查询成功", dashboardService.getDashboardData());
    }
}
