package com.campus.smartcampus.controller;

import com.campus.smartcampus.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    public AdminAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/admin/analytics")
    public String analyticsDashboard(Model model) {
        Map<String, Object> metrics = analyticsService.getDashboardMetrics();
        model.addAllAttributes(metrics);
        return "admin-analytics";
    }
}
