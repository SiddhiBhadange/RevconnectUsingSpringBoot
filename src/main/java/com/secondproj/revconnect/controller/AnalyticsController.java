package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.AnalyticsDTO;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/me")
    public AnalyticsDTO getMyAnalytics(@AuthenticationPrincipal User user) {
        return analyticsService.getUserAnalytics(user);
    }
}