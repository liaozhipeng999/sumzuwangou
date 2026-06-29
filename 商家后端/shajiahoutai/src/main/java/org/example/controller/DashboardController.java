package org.example.controller;

import org.example.common.Result;
import org.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "week") String range) {
        try {
            return Result.success(dashboardService.stats(shopId, range));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
