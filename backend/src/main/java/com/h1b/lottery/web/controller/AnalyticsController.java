package com.h1b.lottery.web.controller;

import com.h1b.lottery.service.AnalyticsService;
import com.h1b.lottery.service.ReportingService;
import com.h1b.lottery.web.dto.DashboardMetricsResponse;
import com.h1b.lottery.web.dto.EmployerAnalyticsResponse;
import com.h1b.lottery.web.dto.ExportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ReportingService reportingService;

    public AnalyticsController(AnalyticsService analyticsService, ReportingService reportingService) {
        this.analyticsService = analyticsService;
        this.reportingService = reportingService;
    }

    @GetMapping("/dashboard")
    public DashboardMetricsResponse dashboard(@RequestParam Integer fiscalYear) {
        return analyticsService.dashboard(fiscalYear);
    }

    @GetMapping("/employers")
    public List<EmployerAnalyticsResponse> employerAnalytics(@RequestParam Integer fiscalYear) {
        return analyticsService.employerAnalytics(fiscalYear);
    }

    @PostMapping("/exports")
    public ExportResponse export(@RequestParam Integer fiscalYear) {
        return reportingService.exportFiscalYearSnapshot(fiscalYear);
    }
}
