package com.example.finnovavalyutatask.service.impl;

import com.example.finnovavalyutatask.payload.dto.response.RoleDTO;
import com.example.finnovavalyutatask.repository.RoleRepository;
import com.example.finnovavalyutatask.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getId(), role.getRole().toString()))
                .toList();
    }
}
