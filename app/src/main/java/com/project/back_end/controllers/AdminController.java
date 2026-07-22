package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    // Constructor Injection
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Handles administrator login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(
            @RequestBody Login login) {

        return service.validateAdmin(login);
    }

}
