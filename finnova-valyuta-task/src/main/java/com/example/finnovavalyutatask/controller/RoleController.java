package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.ApiResponse;
import com.example.finnovavalyutatask.payload.ApiResponseFactory;
import com.example.finnovavalyutatask.payload.dto.response.RoleDTO;
import com.example.finnovavalyutatask.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        return ApiResponseFactory.success(roleService.getAllRoles());
    }
}
