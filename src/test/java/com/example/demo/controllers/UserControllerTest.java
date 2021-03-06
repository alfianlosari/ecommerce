package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("12345678")).thenReturn("hashed!!!");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("alfianlosari");
        r.setPassword("12345678");
        r.setConfirmPassword("12345678");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("alfianlosari", u.getUsername());
        assertEquals("hashed!!!", u.getPassword());
    }

    @Test
    public void create_user_with_password_less_than_seven_characters() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("alfianlosari");
        r.setPassword("123456");
        r.setConfirmPassword("123456");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void create_user_with_password_and_confirmation_not_equals() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("alfianlosari");
        r.setPassword("12345678");
        r.setConfirmPassword("a12345678");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void get_existing_user_by_username() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        final ResponseEntity<User> response = userController.findByUserName("alfianlosari");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals("alfianlosari", u.getUsername());
    }

    @Test
    public void get_non_existing_user_by_username() {
        final ResponseEntity<User> response = userController.findByUserName("alfianlosari");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void get_existing_user_by_id() {
        User existingUser = new User();
        existingUser.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));

        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
    }

    @Test
    public void get_non_existing_user_by_id() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

}
