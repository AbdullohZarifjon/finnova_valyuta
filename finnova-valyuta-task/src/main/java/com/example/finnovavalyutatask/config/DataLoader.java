package com.example.finnovavalyutatask.config;

import com.example.finnovavalyutatask.entity.Role;
import com.example.finnovavalyutatask.entity.User;
import com.example.finnovavalyutatask.entity.enums.UserRole;
import com.example.finnovavalyutatask.repository.RoleRepository;
import com.example.finnovavalyutatask.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DataLoader(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Role> allRoles = roleRepository.findAll();
        Role role = new Role();
        Role role2 = new Role();
        if (allRoles.isEmpty()) {

            role.setRole(UserRole.ROLE_USER);
            roleRepository.save(role);

            role2.setRole(UserRole.ROLE_ADMIN);
            roleRepository.save(role2);
        }
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRoles(List.of(role2, role));
            userRepository.save(user);

            User user2 = new User();
            user2.setUsername("string");
            user2.setPassword(passwordEncoder.encode("string"));
            user2.setRoles(List.of(role));
            userRepository.save(user2);

            User user3 = new User();
            user3.setUsername("aaaaaaa");
            user3.setPassword(passwordEncoder.encode("aaaaaa"));
            user3.setRoles(List.of(role));
            userRepository.save(user3);

            User user4 = new User();
            user4.setUsername("vvvvvvv");
            user4.setPassword(passwordEncoder.encode("vvvvvvv"));
            user4.setRoles(List.of(role));
            userRepository.save(user4);

            User user5 = new User();
            user5.setUsername("eeeeee");
            user5.setPassword(passwordEncoder.encode("eeeeee"));
            user5.setRoles(List.of(role));
            userRepository.save(user5);
        }
    }
}
