package com.example.finnovavalyutatask.service.impl;

import com.example.finnovavalyutatask.dto.*;
import com.example.finnovavalyutatask.entity.Role;
import com.example.finnovavalyutatask.entity.User;
import com.example.finnovavalyutatask.exps.RecordAlreadyException;
import com.example.finnovavalyutatask.exps.RecordNotFoundException;
import com.example.finnovavalyutatask.mapper.UserMapper;
import com.example.finnovavalyutatask.repository.RoleRepository;
import com.example.finnovavalyutatask.repository.UserRepository;
import com.example.finnovavalyutatask.service.JwtService;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserCreateResponseDto register(UserRequestDto userRequestDto) {
        checkUserByUsername(userRequestDto.username());

        List<Role> roles = roleRepository.findAll();
        User user = User.builder()
                .username(userRequestDto.username())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .roles(List.of(roles.get(0)))
                .build();

        User save = userRepository.save(user);

        return UserMapper.toCreateDto(save);
    }

    @Override
    public LoginResponseDto signIn(UserRequestDto loginDTO) throws JsonProcessingException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.username(),
                        loginDTO.password()
                )
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public LoginResponseDto refreshToken(RefreshTokenDto dto) throws JsonProcessingException {
        jwtService.validateRefreshToken(dto.getRefreshToken());

        String username = jwtService.refreshTokenClaims(dto.getRefreshToken()).getSubject();

        User user = getUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    @Override
    public UserResponseDto createUser(UserUpdateRequestDto userUpdateRequestDto) {
        checkUserByUsername(userUpdateRequestDto.username());

        List<Role> roles = roleRepository.findAllById(userUpdateRequestDto.roleIds());

        User user = User.builder()
                .username(userUpdateRequestDto.username())
                .password(passwordEncoder.encode(userUpdateRequestDto.password()))
                .roles(roles)
                .build();

        User save = userRepository.save(user);

        return UserMapper.toDto(save);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = getUserOrThrow(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUserForAdmin(Long id, UserUpdateRequestDto userUpdateRequestDto) {
        User user = getUserOrThrow(id);

        user.setUsername(userUpdateRequestDto.username());
        user.setPassword(passwordEncoder.encode(userUpdateRequestDto.password()));

        List<Role> roles = roleRepository.findAllById(userUpdateRequestDto.roleIds());
        user.setRoles(roles);

        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = getUserOrThrow(id);

        user.setUsername(userRequestDto.username());
        user.setPassword(passwordEncoder.encode(userRequestDto.password()));

        User save = userRepository.save(user);
        return UserMapper.toDto(save);
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = getUserOrThrow(userId);

        userRepository.delete(user);
    }


    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id: " + id));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("User not found with username: " + username));
    }

    private void checkUserByUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RecordAlreadyException("User already exists");
        }
    }
}
