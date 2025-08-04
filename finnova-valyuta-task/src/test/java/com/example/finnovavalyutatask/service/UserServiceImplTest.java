package com.example.finnovavalyutatask.service;

import com.example.finnovavalyutatask.dto.*;
import com.example.finnovavalyutatask.entity.Role;
import com.example.finnovavalyutatask.entity.User;
import com.example.finnovavalyutatask.entity.enums.UserRole;
import com.example.finnovavalyutatask.exps.RecordAlreadyException;
import com.example.finnovavalyutatask.exps.RecordNotFoundException;
import com.example.finnovavalyutatask.repository.RoleRepository;
import com.example.finnovavalyutatask.repository.UserRepository;
import com.example.finnovavalyutatask.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("should register a new user successfully when username is not taken")
    void register_ShouldRegisterNewUser_WhenUsernameIsNotTaken() {

        UserRequestDto requestDto = new UserRequestDto("testuser", "password123");
        Role defaultRole = Role.builder().id(1L).role(UserRole.ROLE_USER).build();
        User expectedUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword123")
                .roles(List.of(defaultRole))
                .build();

        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.empty());
        when(roleRepository.findAll()).thenReturn(List.of(defaultRole));
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserCreateResponseDto result = userService.register(requestDto);

        assertNotNull(result);
        assertEquals(expectedUser.getUsername(), result.getUsername());
        assertEquals(expectedUser.getId(), result.getId());
        verify(userRepository, times(1)).findByUsername(requestDto.username());
        verify(roleRepository, times(1)).findAll();
        verify(passwordEncoder, times(1)).encode(requestDto.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw RecordAlreadyException when username is already taken during registration")
    void register_ShouldThrowRecordAlreadyException_WhenUsernameIsAlreadyTaken() {

        UserRequestDto requestDto = new UserRequestDto("existinguser", "password123");
        User existingUser = User.builder().id(1L).username("existinguser").build();

        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.of(existingUser));

        RecordAlreadyException exception = assertThrows(RecordAlreadyException.class,
                () -> userService.register(requestDto));

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(requestDto.username());
        verify(roleRepository, never()).findAll();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should return login response with tokens when sign-in is successful")
    void signIn_ShouldReturnLoginResponse_WhenSignInIsSuccessful() throws JsonProcessingException {
        // Given
        UserRequestDto loginDto = new UserRequestDto("testuser", "password123");
        User authenticatedUser = User.builder().id(1L).username("testuser").build();
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        when(jwtService.generateAccessToken(authenticatedUser)).thenReturn("mockAccessToken");
        when(jwtService.generateRefreshToken(authenticatedUser)).thenReturn("mockRefreshToken");

        LoginResponseDto result = userService.signIn(loginDto);

        assertNotNull(result);
        assertEquals("mockAccessToken", result.getAccessToken());
        assertEquals("mockRefreshToken", result.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateAccessToken(authenticatedUser);
        verify(jwtService, times(1)).generateRefreshToken(authenticatedUser);
    }

    @Test
    @DisplayName("should throw BadCredentialsException when sign-in fails due to invalid credentials")
    void signIn_ShouldThrowBadCredentialsException_WhenInvalidCredentials() throws JsonProcessingException {
        // Given
        UserRequestDto loginDto = new UserRequestDto("invaliduser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> userService.signIn(loginDto));

        assertEquals("Bad credentials", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("should return new tokens when refresh token is valid")
    void refreshToken_ShouldReturnNewTokens_WhenRefreshTokenIsValid() throws JsonProcessingException {
        // Given
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("validRefreshToken");
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        io.jsonwebtoken.Claims claims = mock(io.jsonwebtoken.Claims.class);

        doNothing().when(jwtService).validateRefreshToken(refreshTokenDto.getRefreshToken());
        when(jwtService.refreshTokenClaims(refreshTokenDto.getRefreshToken())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");

        LoginResponseDto result = userService.refreshToken(refreshTokenDto);

        assertNotNull(result);
        assertEquals("newAccessToken", result.getAccessToken());
        assertEquals("newRefreshToken", result.getRefreshToken());
        verify(jwtService, times(1)).validateRefreshToken(refreshTokenDto.getRefreshToken());
        verify(jwtService, times(1)).refreshTokenClaims(refreshTokenDto.getRefreshToken());
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).generateAccessToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
    }

    @Test
    @DisplayName("should throw RecordNotFoundException when user associated with refresh token is not found")
    void refreshToken_ShouldThrowRecordNotFoundException_WhenUserNotFound() throws JsonProcessingException {
        // Given
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("validRefreshToken");
        String username = "nonexistentuser";
        io.jsonwebtoken.Claims claims = mock(io.jsonwebtoken.Claims.class);

        doNothing().when(jwtService).validateRefreshToken(refreshTokenDto.getRefreshToken());
        when(jwtService.refreshTokenClaims(refreshTokenDto.getRefreshToken())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> userService.refreshToken(refreshTokenDto));

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(jwtService, times(1)).validateRefreshToken(refreshTokenDto.getRefreshToken());
        verify(jwtService, times(1)).refreshTokenClaims(refreshTokenDto.getRefreshToken());
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, never()).generateAccessToken(any(User.class));
    }

    @Test
    @DisplayName("should create a new user successfully by admin when username is not taken")
    void createUser_ShouldCreateNewUserByAdmin_WhenUsernameIsNotTaken() {

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto("newadminuser", "adminpass", List.of(2L));
        Role adminRole = Role.builder().id(2L).role(UserRole.ROLE_ADMIN).build();
        User expectedUser = User.builder()
                .id(2L)
                .username("newadminuser")
                .password("encodedAdminPass")
                .roles(List.of(adminRole))
                .build();

        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.empty());
        when(roleRepository.findAllById(requestDto.roleIds())).thenReturn(List.of(adminRole));
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedAdminPass");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(expectedUser.getUsername(), result.username());
        assertEquals(expectedUser.getId(), result.id());
        assertEquals(1, result.roles().size());
        assertEquals(UserRole.ROLE_ADMIN.name(), result.roles().get(0).getRole().name());
        verify(userRepository, times(1)).findByUsername(requestDto.username());
        verify(roleRepository, times(1)).findAllById(requestDto.roleIds());
        verify(passwordEncoder, times(1)).encode(requestDto.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw RecordAlreadyException when username is already taken during user creation by admin")
    void createUser_ShouldThrowRecordAlreadyException_WhenUsernameAlreadyTakenByAdmin() {
        // Given
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto("existinguser", "pass", List.of(1L));
        User existingUser = User.builder().id(1L).username("existinguser").build();

        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.of(existingUser));

        RecordAlreadyException exception = assertThrows(RecordAlreadyException.class,
                () -> userService.createUser(requestDto));

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(requestDto.username());
        verify(roleRepository, never()).findAllById(anyList());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should return user by id when user exists")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        User foundUser = User.builder().id(userId).username("founduser").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(foundUser));

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("founduser", result.username());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("should throw RecordNotFoundException when user does not exist for getUserById")
    void getUserById_ShouldThrowRecordNotFoundException_WhenUserDoesNotExist() {

        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("should update user for admin successfully when user exists")
    void updateUserForAdmin_ShouldUpdateUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto("updateduser", "newpass", List.of(2L));
        Role adminRole = Role.builder().id(2L).role(UserRole.ROLE_ADMIN).build();
        User existingUser = User.builder().id(userId).username("olduser").password("oldpass").roles(Collections.emptyList()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateDto.password())).thenReturn("encodedNewPass");
        when(roleRepository.findAllById(updateDto.roleIds())).thenReturn(List.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponseDto result = userService.updateUserForAdmin(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("updateduser", result.username());
        assertEquals(1, result.roles().size());
        assertEquals("ROLE_ADMIN", result.roles().get(0).getRole().name());
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(updateDto.password());
        verify(roleRepository, times(1)).findAllById(updateDto.roleIds());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("should throw RecordNotFoundException when user does not exist for updateUserForAdmin")
    void updateUserForAdmin_ShouldThrowRecordNotFoundException_WhenUserDoesNotExist() {

        Long userId = 99L;
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto("updateduser", "newpass", List.of(2L));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> userService.updateUserForAdmin(userId, updateDto));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(roleRepository, never()).findAllById(anyList());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should update user successfully when user exists and updates own profile")
    void updateUser_ShouldUpdateUser_WhenUserExistsAndUpdatesOwnProfile() {
        // Given
        Long userId = 1L;
        UserRequestDto updateDto = new UserRequestDto("selfupdateduser", "newpass");
        User existingUser = User.builder().id(userId).username("olduser").password("oldpass").roles(Collections.emptyList()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateDto.password())).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponseDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("selfupdateduser", result.username());
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(updateDto.password());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("should throw RecordNotFoundException when user does not exist for updateUser")
    void updateUser_ShouldThrowRecordNotFoundException_WhenUserDoesNotExist() {
        // Given
        Long userId = 99L;
        UserRequestDto updateDto = new UserRequestDto("selfupdateduser", "newpass");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> userService.updateUser(userId, updateDto));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- deleteUserById Metodi Testlari ---
    @Test
    @DisplayName("should delete user successfully when user exists")
    void deleteUserById_ShouldDeleteUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        User userToDelete = User.builder().id(userId).username("todeleteuser").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).delete(userToDelete);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(userToDelete);
    }

    @Test
    @DisplayName("should throw RecordNotFoundException when user does not exist for deleteUserById")
    void deleteUserById_ShouldThrowRecordNotFoundException_WhenUserDoesNotExist() {

        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> userService.deleteUserById(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
}