package com.project.back_end.mvc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    // Shared service used for token validation
    @Autowired
    private Service service;

    /**
     * Displays the Admin Dashboard after validating the token.
     *
     * @param token Admin authentication token
     * @return Admin dashboard view if valid, otherwise redirect to home page
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        Map<String, String> validationResult =
                service.validateToken(token, "admin");

        if (validationResult.isEmpty()) {
            return "admin/adminDashboard";
        }

        return "redirect:/";
    }

    /**
     * Displays the Doctor Dashboard after validating the token.
     *
     * @param token Doctor authentication token
     * @return Doctor dashboard view if valid, otherwise redirect to home page
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        Map<String, String> validationResult =
                service.validateToken(token, "doctor");

        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        }

        return "redirect:/";
    }

}
