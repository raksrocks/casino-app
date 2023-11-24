package com.englyn.auth.service;

import com.englyn.auth.entity.UserEntity;
import com.englyn.auth.repo.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

//@SpringBootTest
public class TestUserServiceImpl {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup(){
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testFindUserByUsername_UserFound() {
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

        // Perform the test and assert that Exception is thrown
        assertThrows(RuntimeException.class, () -> userService.getUserToken("nonexistentUser"));
    }

}
