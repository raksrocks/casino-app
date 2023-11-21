package com.englyn.auth.service;

import com.englyn.auth.entity.UserEntity;
import com.englyn.auth.exception.AuthenticationFailedException;
import com.englyn.auth.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestUserServiceImpl {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindUserByUsername_UserFound() throws AuthenticationFailedException {
        // Mock the behavior of the userRepository
        UserEntity mockUser = new UserEntity();
        mockUser.setUsername("testUser");
        mockUser.setToken("test-token");
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Perform the test
        String actual = userService.getUserToken("testUser");

        // Assertions
        assertNotNull(actual);
        assertEquals("test-token", actual);
    }

    @Test
    void testFindUserByUsername_UserNotFound() {
        // Mock the behavior of the userRepository
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(null);

        // Perform the test and assert that AuthenticationFailedException is thrown
        assertThrows(com.englyn.auth.exception.AuthenticationFailedException.class, () -> userService.getUserToken("nonexistentUser"));
    }

}
